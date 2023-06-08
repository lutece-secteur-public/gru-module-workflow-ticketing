/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.daemon;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexerException;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexerActionUtil;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class TicketAnonymisationDaemon extends Daemon
{

    private static final int DELAI_ANONYMISATION = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_DELAI, 10 );
    private static final int TICKET_STATUS_ARCHIVE = AppPropertiesService.getPropertyInt( "ticketing.daemon.anonymisation.state.id.archive", 308 );

    private static ITicketEmailExternalUserMessageDAO dao                   = SpringContextService.getBean( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE );
    private static Plugin                             plugin                = WorkflowTicketingPlugin.getPlugin( );

    private static final String                       REGEX_EMAIL2          = "[A-Za-z0-9+-_.]+@([A-Za-z0-9+-.]+\\.[A-Za-z]{2,4})";
    private static final String                       REGEX_TELEPHONE2      = "[0-9]{2}([-. ]?([0-9]{2})){4}";


    /*
     * Constructor
     */
    public TicketAnonymisationDaemon( )
    {
        super( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        StringJoiner sb = new StringJoiner( "\n\r" );

        sb.add( "Début de l'anomymisation" );
        anonymisation( sb );
        sb.add( "Fin de l'anomymisation" );
        setLastRunLogs( sb.toString( ) );

    }

    private void anonymisation( StringJoiner sb )
    {
        String date = dateForAnonymisation( sb );
        List<Ticket> listTickets = TicketHome.getForAnonymisation( date, TICKET_STATUS_ARCHIVE );

        if ( !listTickets.isEmpty( ) )
        {
            sb.add( "nombre de tickets à anonymiser : " + listTickets.size( ) );
        }
        else
        {
            sb.add( "aucun ticket à anonymiser" );
        }

        for ( Ticket ticket : listTickets )
        {
            // suppression des données sensibles dans l'historique
            anonymizeTicketHistoryData( ticket );
            // anonymisation du ticket

            String newComment = sanitizeCommentTicket( ticket );
            ticket.setTicketComment( newComment );
            ticket.setFirstname( ticket.getReference( ) );
            ticket.setLastname( ticket.getReference( ) );
            ticket.setIdUserTitle( 0 );
            ticket.setEmail( ticket.getReference( ) + "@yopmail.com" );
            ticket.setFixedPhoneNumber( null );
            ticket.setMobilePhoneNumber( null );
            ticket.setDateUpdate( new Timestamp( new Date( ).getTime( ) ) );
            ticket.setAnonymisation( 1 );
            TicketHome.update( ticket );
            indexingTicketAnonymize( ticket.getId( ), sb );
            sb.add( "Id Ticket anonymisé: " + ticket.getId( ) );
        }
    }

    private String dateForAnonymisation( StringJoiner sb )
    {
        Date date = new Date( );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat sdf2 = new SimpleDateFormat( "dd/MM/yyyy" );
        Calendar calDate = Calendar.getInstance( );
        calDate.setTime( date );
        calDate.add( Calendar.DAY_OF_YEAR, -DELAI_ANONYMISATION );
        date = calDate.getTime( );
        sb.add( "anonymisation tickets archivés avant le " + sdf2.format( date ) );
        return sdf.format( date );
    }

    public void anonymizeTicketHistoryData( Ticket ticket )
    {
        List<Integer> listEmailExternalUser = dao.getListIDMessageExternalUser( ticket.getId( ), plugin );
        for ( int idEmailExternalUser : listEmailExternalUser )
        {
            Map<String, String> data = dao.getHistoryEmailToAnonymize( idEmailExternalUser, plugin );
            for ( Entry<String, String> entry : data.entrySet( ) )
            {
                String newValue = sanitizeEntryMessage( ticket, entry.getValue( ) );
                entry.setValue( newValue );
            }
            dao.update( data, idEmailExternalUser, plugin );
        }

    }

    private String sanitizeCommentTicket( Ticket ticket )
    {
        String anonymizeCommemtTicket = ticket.getTicketComment( );

        anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, "(?i)" + ticket.getFirstname( ), "" );
        anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, "(?i)" + ticket.getLastname( ), "" );
        anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, REGEX_EMAIL2, "" );
        anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, REGEX_TELEPHONE2, "" );

        return anonymizeCommemtTicket;

    }

    private String sanitizeEntryMessage( Ticket ticket, String messageToAnonymise )
    {
        String anonymizeMessageTicket = messageToAnonymise;

        anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, "(?i)" + ticket.getFirstname( ), "" );
        anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, "(?i)" + ticket.getLastname( ), "" );
        anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, REGEX_EMAIL2, "" );
        anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, REGEX_TELEPHONE2, "" );

        return anonymizeMessageTicket;

    }

    private String sanitizeValue( String comment, String valueToAnonymise, String substitute )
    {
        return comment.replaceAll( valueToAnonymise, substitute );
    }

    /**
     * Immediate indexation of a Ticket for the anonymisation
     *
     * @param idTicket
     *            the id of the Ticket to index
     */
    protected void indexingTicketAnonymize( int idTicket, StringJoiner sb )
    {
        Ticket ticket = TicketHome.findByPrimaryKey( idTicket );
        if ( ticket != null )
        {
            try
            {
                TicketIndexer ticketIndexer = new TicketIndexer( );
                ticketIndexer.indexTicket( ticket );
            } catch ( TicketIndexerException ticketIndexerException )
            {
                sb.add( "Le ticket id " + idTicket + "anonymisé est en attente pour indexation" );

                // The indexation of the Ticket fail, we will store the Ticket in the table for the daemon
                IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
            }
        }
    }

}
