/*
 * Copyright (c) 2002-2022, City of Paris
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
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
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

    private static ITicketEmailExternalUserMessageDAO _dao = SpringContextService.getBean( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE );
    private static Plugin _plugin = WorkflowTicketingPlugin.getPlugin( );
    
    private static final String REGEX_EMAIL = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}";
    private static final String REGEX_TELEPHONE = "(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}|00 33 \\(0\\)[1-9]{0,2}(?:[\\s.-]*\\d{2}){4}";

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
        String date = dateForAnonymisation( );
        List<Ticket> listTickets = TicketHome.getForAnonymisation( date, TICKET_STATUS_ARCHIVE );

        for ( Ticket ticket : listTickets )
        {
            // suppression des données sensibles dans l'historique
            anonymizeTicketHistoryData( ticket );
            // anonymisation du ticket

            String newComment = "";
            newComment = ticket.getTicketComment( ).replaceAll( "(?i)" + ticket.getFirstname( ), "" );
            newComment = newComment.replaceAll( "(?i)" + ticket.getLastname( ), "" );
            newComment = newComment.replaceAll( REGEX_EMAIL, "" );
            newComment = newComment.replaceAll( REGEX_TELEPHONE, "" );
            newComment = newComment.replaceAll( REGEX_TELEPHONE, "" );
            ticket.setTicketComment( newComment );

            ticket.setFirstname( ticket.getReference( ) );

            ticket.setLastname( ticket.getReference( ) );
            ticket.setIdUserTitle( 0 );
            ticket.setEmail( ticket.getReference( ) + "@yopmail.com" );
            ticket.setFixedPhoneNumber( null );
            ticket.setMobilePhoneNumber( null );
            ticket.setDateUpdate( new Timestamp( new Date( ).getTime( ) ) );
            TicketHome.update( ticket );
            sb.add( "Id Ticket anonymisé: " + ticket.getId( ) );
        }
    }

    private String dateForAnonymisation( )
    {
        Date date = new Date( );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        Calendar calDate = Calendar.getInstance( );
        calDate.setTime( date );
        calDate.add( Calendar.DAY_OF_YEAR, -DELAI_ANONYMISATION );
        date = calDate.getTime( );
        return sdf.format( date );
    }

    public static void anonymizeTicketHistoryData( Ticket ticket )
    {
        List<Integer> listEmailExternalUser = _dao.getListIDMessageExternalUser( ticket.getId( ), _plugin );
        for ( int idEmailExternalUser : listEmailExternalUser )
        {
            Map<String, String> data = _dao.getHistoryEmailToAnonymize( idEmailExternalUser, _plugin );
            for ( Entry<String, String> entry : data.entrySet( ) )
            {
                String newValue = "";
                newValue = entry.getValue( ).replaceAll( "(?i)" + ticket.getFirstname( ), "" );
                newValue = newValue.replaceAll( "(?i)" + ticket.getLastname( ), "" );
                newValue = newValue.replaceAll( REGEX_EMAIL, "" );
                newValue = newValue.replaceAll( REGEX_TELEPHONE, "" );
                newValue = newValue.replaceAll( REGEX_TELEPHONE, "" );
                entry.setValue( newValue );
            }
            _dao.update( data, idEmailExternalUser, _plugin );
        }

    }

}
