/*
 * Copyright (c) 2002-2024, City of Paris
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.address.TicketAddress;
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
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.anonymisation.IAnonymisationDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.information.ITaskInformationDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.IResourceHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.IEditableTicketDAO;
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

    private static final int DELAI_ANONYMISATION = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_DELAI, 1096 );
    private static final int MAX_ANONYMISATION_PAR_DOMAINE = PluginConfigurationService
            .getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_TICKET_MAX_DOMAINE, 200 );

    private static ITicketEmailExternalUserMessageDAO dao = SpringContextService.getBean( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE );
    private static ITaskInformationDAO daoTaskInfo = SpringContextService.getBean( ITaskInformationDAO.BEAN_SERVICE );
    private static IResourceHistoryDAO daoResourceHist = SpringContextService.getBean( IResourceHistoryDAO.BEAN_SERVICE );
    private static IEditableTicketDAO daoEditableTicketHist = SpringContextService.getBean( IEditableTicketDAO.BEAN_SERVICE );
    private static IAnonymisationDAO daoAnonymisation = SpringContextService.getBean( IAnonymisationDAO.BEAN_SERVICE );

    private static Plugin plugin = WorkflowTicketingPlugin.getPlugin( );

    private static final String REGEX_EMAIL2 = "[A-Za-z0-9+-_.]+@([A-Za-z0-9+-.]+\\.[A-Za-z]{2,4})";
    private static final String REGEX_TELEPHONE2 = "[0-9]{2}([-. ]?([0-9]{2})){4}";

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
    public void anonymisation( StringJoiner sb )
    {
        List<Integer> allIdDomaines = TicketCategoryHome.selectIdCategoriesDomainList( );

        for ( Integer idCategory : allIdDomaines )
        {
            TicketCategory categorieDomaine = TicketCategoryService.getInstance( true ).findCategoryById( idCategory );

            if ( null != categorieDomaine )
            {
                anonymizeByDomaine( categorieDomaine, sb );
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

        int delaiAnonymisation = ( !categorieDomaine.getDelaiAnonymisation( ).trim( ).isEmpty( ) )
                ? Integer.parseInt( categorieDomaine.getDelaiAnonymisation( ).trim( ) )
                : DELAI_ANONYMISATION;

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
                    try
                    {
                        TransactionManager.beginTransaction( plugin );
                        // suppression des données sensibles dans l'historique
                        anonymizeTicketHistoryData( ticket );

                        // anonymisation du ticket
                        String newComment = sanitizeCommentTicket( ticket );
                        ticket.setTicketComment( newComment );
                        String userMessage = sanitizeUserMessageTicket( ticket );
                        ticket.setUserMessage( userMessage );
                        ticket.setFirstname( ticket.getReference( ) );
                        ticket.setLastname( ticket.getReference( ) );
                        ticket.setIdUserTitle( 0 );
                        ticket.setEmail( ticket.getReference( ) + "@yopmail.com" );
                        ticket.setFixedPhoneNumber( null );
                        ticket.setMobilePhoneNumber( null );
                        ticket.setDateUpdate( new Timestamp( new Date( ).getTime( ) ) );
                        ticket.setAnonymisation( 1 );
                        TicketAddress cleanAddress = new TicketAddress( );
                        ticket.setTicketAddress( cleanAddress );
                        TicketHome.update( ticket );
                        anonymizeAddress( ticket.getId( ) );
                        indexingTicketAnonymize( ticket.getId( ), sb );
                    }
                    catch( Exception e )
                    {
                        TransactionManager.rollBack( plugin );
                        sb.add( "Annulation de l'anonymisation pour le domaine" + categorieDomaine.getLabel( ) );
                        AppLogService.error( e );
                    }
                    TransactionManager.commitTransaction( plugin );
                }
            }
            else
            {
                sb.add( "aucun ticket à anonymiser " );
            }
        }

    }

    ////// GENERAL

    /**
     * Find the date closed max for a domaine accross a delay
     *
     * @param delaiAnonymisation
     *            the delay for a domaine
     * @param category
     *            the category of the ticket to anonymize
     * @param sb
     *            the logs
     * @return the date for cloture for a domaine
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
     * Anonymize historic data with message exchanges
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void anonymizeTicketHistoryData( Ticket ticket )
    {
        anonymizeTicketHistoryFromUsager( ticket );
        anonymizeTicketHistoryFromAgent( ticket );
    }

    /**
     * Call anonymizeAddress
     *
     * @param idTicket
     *            the id of the ticket to anonymize
     */
    private void anonymizeAddress( int idTicket )
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
    private void deleteAttachment( List<Integer> coreFileId )
    {
        if ( !coreFileId.isEmpty( ) )
        {
            List<Integer> corePhysicalFilesId = TicketHome.getIdAttachmentCorePhysicalFileListByTicket( coreFileId );

            if ( !corePhysicalFilesId.isEmpty( ) )
            {
                TicketHome.deleteCorePhysicalFile( corePhysicalFilesId );
            }
            TicketHome.deleteCoreFile( coreFileId );
        }
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
            }
            catch( TicketIndexerException ticketIndexerException )
            {
                sb.add( "Le ticket id " + idTicket + " anonymisé est en attente pour indexation" );

                // The indexation of the Ticket fail, we will store the Ticket in the table for the daemon
                IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
            }
        }
    }

    ////// USAGER

    /**
     * Anonymize historic data with message exchanges From Usager
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void anonymizeTicketHistoryFromUsager( Ticket ticket )
    {
        List<Integer> listEmailExternalUser = dao.getListIDMessageExternalUser( ticket.getId( ), plugin );
        for ( int idEmailExternalUser : listEmailExternalUser )
        {
            Map<String, String> data = dao.getHistoryEmailToAnonymize( idEmailExternalUser, plugin );
            for ( Entry<String, String> entry : data.entrySet( ) )
            {
                String newValue = sanitizeEntryMessage( ticket, entry.getValue( ) );
                if ( !newValue.equals( entry.getValue( ) ) )
                {
                    entry.setValue( newValue );
                }
            }
            dao.update( data, idEmailExternalUser, plugin );
        }
        // suppression pieces jointes usager
        deleteAttachmentUsagerTicket( ticket );
    }

    /**
     * Find attachment for usager
     *
     * @param ticket
     *            the ticket to clean
     */
    private List<Integer> findUsagerAttachment( Ticket ticket )
    {
        return TicketHome.getIdAttachmentCoreFileListByTicket( ticket.getId( ) );
    }

    /**
     * Delete attachemant for a usager
     *
     * @param ticket
     *            the ticket to anonymize
     * @param sb
     */
    private void deleteAttachmentUsagerTicket( Ticket ticket )
    {
        List<Integer> usagerAttachment = findUsagerAttachment( ticket );

        deleteAttachment( usagerAttachment );
    }

    ////// AGENT

    /**
     * Anonymize historic data with message exchanges From Usager
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void anonymizeTicketHistoryFromAgent( Ticket ticket )
    {
        List<Integer> idResponseTotal = new ArrayList<>( );
        List<Integer> idCoreUploadFinal = new ArrayList<>( );

        List<Integer> idHistoryList = daoResourceHist.getIdHistoryListByResource( ticket.getId( ), plugin );
        for ( int idHistory : idHistoryList )
        {
            // table workflow_task_ticketing_information
            List<Integer> idHistInfo = anoymiseFromTaskInfo( ticket, idHistory );
            idResponseTotal.addAll( idHistInfo );
            // table workflow_task_ticketing_editable_ticket
            anoymiseFromEditableTicket( ticket, idHistory );
            // table workflow_task_notify_gru_history
            anoymiseNotifyGruHistory( ticket, idHistory );
            // table workflow_task_comment_value
            anoymiseCommentValue( ticket, idHistory );
            // table workflow_task_upload_files
            List<Integer> idCoreUploadFound = findUploadFiles( idHistory );
            idCoreUploadFinal.addAll( idCoreUploadFound );
            cleanUploadFiles( idHistory, plugin );
        }
        if ( !idResponseTotal.isEmpty( ) )
        {
            List<Integer> coreIdFileAgent = findAgentAttachment( idResponseTotal );
            coreIdFileAgent.addAll( idCoreUploadFinal );

            deleteAttachment( coreIdFileAgent );
        }
        else
            if ( !idCoreUploadFinal.isEmpty( ) )
            {
                deleteAttachment( idCoreUploadFinal );
            }

    }

    /**
     * Anonymize historic data From task info table
     *
     * @param ticket
     *            the ticket to anonymizes
     */
    private List<Integer> anoymiseFromTaskInfo( Ticket ticket, int idHistory )
    {
        String valueInfo = daoTaskInfo.getInfoHistoryValueByIdHistory( idHistory, plugin );
        List<Integer> idResponseTotal = new ArrayList<>( );
        if ( !valueInfo.isEmpty( ) )
        {
            if ( valueInfo.contains( "MESSAGE-IN-WORKFLOW" ) || valueInfo.contains( "<b>" ) )
            {
                anonymizeCommentFromAgent( ticket, idHistory, valueInfo );
            }
            String valueInfoUpdated = daoTaskInfo.getInfoHistoryValueByIdHistory( idHistory, plugin );
            if ( valueInfoUpdated.contains( "a href=" ) )
            {
                List<Integer> idResponseListForAgent = extractIdResponse( valueInfoUpdated );
                idResponseTotal.addAll( idResponseListForAgent );
                deleteHrefNotStillUsedInTaskInfo( valueInfoUpdated, idHistory );
            }
        }
        return idResponseTotal;
    }

    /**
     * Anonymize historic data with comment in specific task From Agent
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void anonymizeCommentFromAgent( Ticket ticket, int idHistory, String value )
    {
        String newValue = sanitizeEntryMessage( ticket, value );
        if ( !value.equals( newValue ) )
        {
            daoTaskInfo.update( idHistory, newValue, plugin );
        }
    }

    /**
     * Anonymize historic data with Editable ticket
     *
     * @param ticket
     *            the ticket to anonymize
     * @param idHistory
     *            the id history
     * @param value
     *            the value to update
     */
    private void anoymiseFromEditableTicket( Ticket ticket, int idHistory )
    {
        String valueMessage = daoEditableTicketHist.loadByIdHistory( idHistory );
        if ( ( null != valueMessage ) && !valueMessage.isEmpty( ) )
        {
            String newValue = sanitizeEntryMessage( ticket, valueMessage );
            if ( !valueMessage.equals( newValue ) )
            {
                daoEditableTicketHist.storeAnonymisation( newValue, idHistory );
            }
        }
    }

    /**
     * Anonymize historic data with notifygru history
     *
     * @param ticket
     *            the ticket to anonymize
     * @param idHistory
     *            the id history
     * @param value
     *            the value to update
     */
    private void anoymiseNotifyGruHistory( Ticket ticket, int idHistory )
    {
        Map<String, String> valueNotifyMessages = daoAnonymisation.loadMessageNotifyHIstoryTotal( idHistory, plugin );
        Map<String, String> cleanValueNotifyMessages = new HashMap<>( );
        if ( ( null != valueNotifyMessages ) && !valueNotifyMessages.isEmpty( ) )
        {
            for ( Map.Entry<String, String> mapEntry : valueNotifyMessages.entrySet( ) )
            {
                if ( null != mapEntry.getValue( ) )
                {
                    String newValue = sanitizeEntryMessage( ticket, mapEntry.getValue( ) );
                    String cleanHrefValue = cleanHrefNotStillUsedInNotifyHistory( newValue );
                    if ( !( mapEntry.getValue( ) ).equals( cleanHrefValue ) )
                    {
                        cleanValueNotifyMessages.put( mapEntry.getKey( ), cleanHrefValue );
                    }
                    else
                    {
                        cleanValueNotifyMessages.put( mapEntry.getKey( ), mapEntry.getValue( ) );
                    }
                }
            }
            if ( !valueNotifyMessages.equals( cleanValueNotifyMessages ) )
            {
                daoAnonymisation.storeAnonymisationNotifyGruHistoryTotal( cleanValueNotifyMessages, idHistory, plugin );
            }
        }
    }

    /**
     * Clean the value if a href exists in a notify value history
     *
     * @param valueNotify
     *            the value to clean for link
     * @return the info value without file link
     */
    private String cleanHrefNotStillUsedInNotifyHistory( String valueNotify )
    {
        String finalValue = valueNotify;
        String [ ] partPhrase = valueNotify.split( " " );
        List<String> stringWithoutHref = cleanPJStockInString( partPhrase );

        if ( !String.join( " ", partPhrase ).equals( String.join( " ", stringWithoutHref ) ) )
        {
            finalValue = String.join( " ", stringWithoutHref );
        }
        return finalValue;
    }

    /**
     * Anonymize historic data of commment task
     *
     * @param ticket
     *            the ticket to anonymize
     * @param idHistory
     *            the id history
     * @param value
     *            the value to update
     */
    private void anoymiseCommentValue( Ticket ticket, int idHistory )
    {
        String valueComment = daoAnonymisation.loadCommentValue( idHistory, plugin );
        if ( ( null != valueComment ) && !valueComment.isEmpty( ) )
        {
            String newValue = sanitizeEntryMessage( ticket, valueComment );
            if ( !valueComment.equals( newValue ) )
            {
                daoAnonymisation.storeAnonymisationCommentValue( newValue, idHistory, plugin );
            }
        }
    }

    /**
     * Find id file from workflow_task_upload_files
     *
     * @param idHistory
     *            the id history
     * @return the list of id file from workflow_task_upload_files
     */
    private List<Integer> findUploadFiles( int idHistory )
    {
        List<Integer> uploadIdFilesHistoryList = new ArrayList<>( );
        List<Integer> uploadIdList = daoAnonymisation.getIdUploadFilesByIdHistory( idHistory, plugin );
        if ( ( null != uploadIdList ) && !uploadIdList.isEmpty( ) )
        {
            for ( Integer idFile : uploadIdList )
            {
                uploadIdFilesHistoryList.add( idFile );
            }
        }
        return uploadIdFilesHistoryList;
    }

    /**
     * Anonymize historic data in upload files table
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void cleanUploadFiles( int idHistory, Plugin plugin )
    {
        daoAnonymisation.cleanUploadLines( idHistory, plugin );
    }

    /**
     * Find attachment for agents
     *
     * @param idResponseList
     *            the list of id response
     *
     * @return the list id_file from genatt_response
     */
    private List<Integer> findAgentAttachment( List<Integer> idResponseList )
    {
        return TicketHome.getCoreFileForAgent( idResponseList );
    }

    /**
     * Extract from historic data from task info table info value
     *
     * @param value
     *            the value which maybe contains id response
     * @return The list of id response
     */
    private List<Integer> extractIdResponse( String value )
    {
        List<Integer> idResponseList = new ArrayList<>( );
        String [ ] partPhrase = value.split( "id_response=" );

        if ( partPhrase.length > 1 )
        {
            for ( int i = 0; i < partPhrase.length; i++ )
            {
                StringBuilder idReponseBuild = new StringBuilder( );
                for ( int j = 0; j < partPhrase [i].length( ); j++ )
                {
                    char charPart = partPhrase [i].charAt( j );
                    if ( Character.isDigit( charPart ) )
                    {
                        idReponseBuild.append( charPart );
                    }
                    else
                    {
                        break;
                    }
                }
                if ( idReponseBuild.length( ) > 0 )
                {
                    idResponseList.add( Integer.parseInt( idReponseBuild.toString( ) ) );
                }
            }
        }
        return idResponseList;

    }

    /**
     * Update the value if a href deleted in a info value
     *
     * @param valueInfo
     *            the value to clean for link
     * @param idHistory
     *            the id of history data
     */
    private void deleteHrefNotStillUsedInTaskInfo( String valueInfo, int idHistory )
    {
        String [ ] partPhrase = valueInfo.split( " " );
        List<String> stringWithoutHref = cleanPJStockInString( partPhrase );

        if ( !String.join( " ", partPhrase ).equals( String.join( " ", stringWithoutHref ) ) )
        {
            daoTaskInfo.update( idHistory, String.join( " ", stringWithoutHref ), plugin );
        }
    }

    /**
     * Clean the link to download file from task info table value split in array
     *
     * @param partPhrase
     *            the info value in a string array
     * @return The list of part of the string array without the link to file or response
     */
    private List<String> cleanPJStockInString( String [ ] partPhrase )
    {
        List<String> stringWithoutHref = new ArrayList<>( );
        if ( partPhrase.length > 1 )
        {
            for ( int i = 0; i < partPhrase.length; i++ )
            {
                if ( !partPhrase [i].startsWith( "<a" ) && !partPhrase [i].startsWith( "href" ) )
                {
                    stringWithoutHref.add( partPhrase [i] );
                }
            }
        }
        return stringWithoutHref;
    }

    ////// UTIL

    /**
     * Anonymize user message
     *
     * @param ticket
     *            the ticket to anonymize
     * @return the clear user message
     */
    private String sanitizeUserMessageTicket( Ticket ticket )
    {
        String anonymizeUserMessageTicket = "";

        if ( ( null != ticket.getUserMessage( ) ) && !ticket.getUserMessage( ).trim( ).isEmpty( ) )
        {
            anonymizeUserMessageTicket = ticket.getUserMessage( );
            anonymizeUserMessageTicket = sanitizeAllValues( ticket, anonymizeUserMessageTicket );
        }
        return anonymizeUserMessageTicket;
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
        String anonymizeCommentTicket = "";

        if ( ( null != ticket.getTicketComment( ) ) && !ticket.getTicketComment( ).trim( ).isEmpty( ) )
        {
            anonymizeCommentTicket = ticket.getTicketComment( );
            anonymizeCommentTicket = sanitizeAllValues( ticket, anonymizeCommentTicket );
        }
        return anonymizeCommentTicket;
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
            anonymizeMessageTicket = sanitizeAllValues( ticket, anonymizeMessageTicket );
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
     * Sanitize messages with accent and htmlEncode
     *
     * @param ticket
     *            the ticket to anonymize
     * @param messageToSanitize
     *            the message to anonymize
     * @return the final sanitized message
     */
    private String sanitizeAllValues( Ticket ticket, String messageToSanitize )
    {
        messageToSanitize = sanitizeValue( messageToSanitize, "(?i)" + ticket.getFirstname( ), "" );
        messageToSanitize = sanitizeValue( messageToSanitize, "(?i)" + StringUtils.stripAccents( ticket.getFirstname( ) ), "" );
        messageToSanitize = sanitizeValue( messageToSanitize, "(?i)" + convertToaccenthtml( ticket.getFirstname( ) ), "" );
        messageToSanitize = sanitizeValue( messageToSanitize, "(?i)" + ticket.getLastname( ), "" );
        messageToSanitize = sanitizeValue( messageToSanitize, "(?i)" + StringUtils.stripAccents( ticket.getLastname( ) ), "" );
        messageToSanitize = sanitizeValue( messageToSanitize, "(?i)" + convertToaccenthtml( ticket.getLastname( ) ), "" );
        messageToSanitize = sanitizeValue( messageToSanitize, REGEX_EMAIL2, "" );
        messageToSanitize = sanitizeValue( messageToSanitize, REGEX_TELEPHONE2, "" );

        return messageToSanitize;
    }

    /**
     * Convert a string with accent in accent htmlEncode For anonymisation with htmlEncoding
     *
     * @param str
     *            the string to convert
     * @return the final converted string in htmlEncoding
     */
    private String convertToaccenthtml( String str )
    {
        str = str.replaceAll( "á", "&aacute;" );
        str = str.replaceAll( "â", "&acirc;" );
        str = str.replaceAll( "é", "&eacute;" );
        str = str.replaceAll( "è", "&egrave;" );
        str = str.replaceAll( "ë", "&euml;" );
        str = str.replaceAll( "É", "&Eacute;" );
        str = str.replaceAll( "È", "&Egrave;" );
        str = str.replaceAll( "í", "&iacute;" );
        str = str.replaceAll( "ï", "&iuml;" );
        str = str.replaceAll( "î", "&icirc;" );
        str = str.replaceAll( "ó", "&oacute;" );
        str = str.replaceAll( "ô", "&ocirc;" );
        str = str.replaceAll( "œ", "&oelig;" );
        str = str.replaceAll( "æ", "&aelig;" );
        str = str.replaceAll( "ú", "&uacute;" );
        str = str.replaceAll( "ñ", "&ntilde;" );
        str = str.replaceAll( "ç", "&ccedil;" );

        return str;
    }

}
