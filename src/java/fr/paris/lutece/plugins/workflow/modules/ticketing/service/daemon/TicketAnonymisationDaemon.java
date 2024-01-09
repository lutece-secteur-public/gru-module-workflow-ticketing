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

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexerException;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexerActionUtil;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;


/**
 * Daemon used to anonymize Tickets
 */
public class TicketAnonymisationDaemon extends Daemon
{

    private static final int                          DELAI_ANONYMISATION           = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_DELAI, 1096 );
    private static final int                          MAX_ANONYMISATION_PAR_DOMAINE = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_TICKET_MAX_DOMAINE, 200 );

    private static ITicketEmailExternalUserMessageDAO dao                           = SpringContextService.getBean( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE );
    private static Plugin                             plugin                        = WorkflowTicketingPlugin.getPlugin( );

    private static final String                       REGEX_EMAIL2                  = "[A-Za-z0-9+-_.]+@([A-Za-z0-9+-.]+\\.[A-Za-z]{2,4})";
    private static final String                       REGEX_TELEPHONE2              = "[0-9]{2}([-. ]?([0-9]{2})){4}";

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

        sb.add( "Début de l'anonymisation" );
        anonymisation( sb );
        sb.add( "Fin de l'anonymisation" );
        setLastRunLogs( sb.toString( ) );

    }

    /**
     * Anonymize the ticket
     *
     * @param sb
     *            the logs
     */
    private void anonymisation( StringJoiner sb )
    {
        List<Integer> allIdDomaines = TicketCategoryHome.selectIdCategoriesDomainList( );

        for ( Integer idCategory : allIdDomaines )
        {
            TicketCategory categorieDomaine = TicketCategoryService.getInstance( true ).findCategoryById( idCategory );

            if ( null != categorieDomaine )
            {
                try
                {
                    TransactionManager.beginTransaction( plugin );
                    anonymizeByDomaine( categorieDomaine, sb );
                    TransactionManager.commitTransaction( plugin );
                } catch ( Exception e )
                {
                    TransactionManager.rollBack( plugin );
                    sb.add( "Annulation de l'anonymisation pour le domaine" + categorieDomaine.getLabel( ) );
                    AppLogService.error( e );
                }
            }
        }
    }

    /**
     * Anomynize by domaine
     *
     * @param categorieDomaine
     *            the category domaine
     * @param sb
     *            The logs
     */
    private void anonymizeByDomaine( TicketCategory categorieDomaine, StringJoiner sb )
    {
        List<Integer> allChildrenForADomaine = TicketCategoryService.getInstance( true ).getAllChildren( categorieDomaine );

        int delaiAnonymisation = ( !categorieDomaine.getDelaiAnonymisation( ).isEmpty( ) ) ? Integer.parseInt( categorieDomaine.getDelaiAnonymisation( ).trim( ) ) : DELAI_ANONYMISATION;

        if ( !allChildrenForADomaine.isEmpty( ) )
        {
            java.sql.Date date = findDateClotureForAnonymisationDomaine( delaiAnonymisation, categorieDomaine, sb );

            List<Integer> listIdTickets = TicketHome.getForAnonymisationForDomaine( date, allChildrenForADomaine, MAX_ANONYMISATION_PAR_DOMAINE );

            if ( !listIdTickets.isEmpty( ) )
            {
                sb.add( "nombre de tickets à anonymiser : " + listIdTickets.size( ) );
                for ( Integer idTicket : listIdTickets )
                {
                    Ticket ticket = TicketHome.findByPrimaryKeyWithoutFiles( idTicket );
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
                    anoymizeAddress( ticket.getId( ) );
                    indexingTicketAnonymize( ticket.getId( ), sb );

                    // suppression des pieces jointes
                    deleteAttachmentTicket( ticket, sb );
                }
            } else
            {
                sb.add( "aucun ticket à anonymiser " );
            }
        }
    }

    /**
     * Find the date closed max for a domaine accross a delay
     *
     * @param delaiAnonymisation
     *            the delay for a domaine
     * @param category
     *            the category of the ticket to anonymize
     * @param sb
     *            the logs
     * @return
     */
    private java.sql.Date findDateClotureForAnonymisationDomaine( int delaiAnonymisation, TicketCategory category, StringJoiner sb )
    {
        Date date = new Date( );
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        Calendar calDate = Calendar.getInstance( );
        calDate.setTime( date );
        calDate.add( Calendar.DAY_OF_YEAR, -delaiAnonymisation );
        date = calDate.getTime( );
        sb.add( "- anonymisation des tickets du domaine " + category.getLabel( ) + " dont la date de clôture est plus ancienne que le " + sdf.format( date ) );
        return new java.sql.Date( date.getTime( ) );
    }

    /**
     * Call anonymizeAddress
     *
     * @param idTicket
     *            the id of the ticket to anonymize
     */
    public void anoymizeAddress( int idTicket )
    {
        TicketHome.anonymizeAddress( idTicket );
    }

    /**
     * Delete attachemant for a ticket core_file and core_physical_fle
     *
     * @param ticket
     *            the ticket to anonymize
     * @param sb
     */
    public void deleteAttachmentTicket( Ticket ticket, StringJoiner sb )
    {
        List<Integer> coreFilesId = TicketHome.getIdAttachmentCoreFileListByTicket( ticket.getId( ) );

        if ( !coreFilesId.isEmpty( ) )
        {
            List<Integer> corePhysicalFilesId = TicketHome.getIdAttachmentCorePhysicalFileListByTicket( coreFilesId );

            if ( !corePhysicalFilesId.isEmpty( ) )
            {
                TicketHome.deleteCorePhysicalFile( corePhysicalFilesId );
            }

            TicketHome.deleteCoreFile( coreFilesId );
        }
    }

    /**
     * Anonymize historic data with message exchanges
     *
     * @param ticket
     *            the ticket to anonymize
     */
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

    /**
     * Anonymize comments
     *
     * @param ticket
     *            the ticket to anonymize
     * @return the clear comment
     */
    private String sanitizeCommentTicket( Ticket ticket )
    {
        String anonymizeCommemtTicket = "";

        if ( ( null != ticket.getTicketComment( ) ) && !ticket.getTicketComment( ).trim( ).isEmpty( ) )
        {
            anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, "(?i)" + ticket.getFirstname( ), "" );
            anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, "(?i)" + ticket.getLastname( ), "" );
            anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, REGEX_EMAIL2, "" );
            anonymizeCommemtTicket = sanitizeValue( anonymizeCommemtTicket, REGEX_TELEPHONE2, "" );
        }
        return anonymizeCommemtTicket;
    }

    /**
     * Anonymize messages
     *
     * @param ticket
     *            the ticket to anonymize
     * @param messageToAnonymise
     *            the message to anonymize
     * @return the final clear message
     */
    private String sanitizeEntryMessage( Ticket ticket, String messageToAnonymise )
    {
        String anonymizeMessageTicket = "";

        if ( ( null != messageToAnonymise ) && !messageToAnonymise.trim( ).isEmpty( ) )
        {
            anonymizeMessageTicket = messageToAnonymise;

            anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, "(?i)" + ticket.getFirstname( ), "" );
            anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, "(?i)" + ticket.getLastname( ), "" );
            anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, REGEX_EMAIL2, "" );
            anonymizeMessageTicket = sanitizeValue( anonymizeMessageTicket, REGEX_TELEPHONE2, "" );
        }
        return anonymizeMessageTicket;

    }

    /**
     * Replace a comment by a substitute
     *
     * @param comment
     *
     * @param valueToAnonymise
     *
     * @param substitute
     *
     * @return a clear value in a comment
     */
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
                sb.add( "Le ticket id " + idTicket + " anonymisé est en attente pour indexation" );

                // The indexation of the Ticket fail, we will store the Ticket in the table for the daemon
                IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
            }
        }
    }

}
