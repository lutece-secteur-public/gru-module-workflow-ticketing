/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPj;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPjHome;
import fr.paris.lutece.plugins.ticketing.service.TicketTransfertPjService;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.anonymisation.IAnonymisationDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.information.ITaskInformationDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.IResourceHistoryDAO;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Daemon used to Pj Migration Tickets
 */
public class TicketPjMigrationDaemon extends Daemon
{
    private static final int MAX_TICKETS_PAR_LOT_MIGRATION_PJ = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_MIGRATION_TICKET_MAX_PJ, 200 );
    private static final int           STATUS_TRANSFERT_OK              = 1;
    private static Plugin              _plugin                          = PluginService.getPlugin( "ticketing" );

    private static IResourceHistoryDAO _daoResourceHist                 = SpringContextService.getBean( IResourceHistoryDAO.BEAN_SERVICE );
    private static ITaskInformationDAO _daoTaskInfo                     = SpringContextService.getBean( ITaskInformationDAO.BEAN_SERVICE );
    private static IAnonymisationDAO   _daoAnonymisation                = SpringContextService.getBean( IAnonymisationDAO.BEAN_SERVICE );

    /**
     * Constructor
     */
    public TicketPjMigrationDaemon( )
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

        sb.add( "Début de la migration" );
        migratePj( sb );
        sb.add( "Fin de la migration" );
        setLastRunLogs( sb.toString( ) );
    }


    /**
     * Migrate file informations to centralize info link to a ticket in ticketing_ticket_pj
     *
     * @param sb
     *            the logs
     */
    private void migratePj( StringJoiner sb )
    {
        List<Integer> ticketsList = TicketPjHome.getIdTicketListForPjMigration( MAX_TICKETS_PAR_LOT_MIGRATION_PJ );

        if ( !ticketsList.isEmpty( ) )
        {
            sb.add( "nombre de tickets dont les PJ sont à transférer : " + ticketsList.size( ) );
            for ( Integer idTicket : ticketsList )
            {
                Ticket ticket = TicketHome.findByPrimaryKeyWithoutFiles( idTicket );
                if ( !TicketPjHome.isTicketExistInTicketPj( idTicket ) )
                {
                    try
                    {
                        TransactionManager.beginTransaction( _plugin );

                        // recuperation des PJ usager
                        managePjforS3ForUsager( ticket );

                        // recuperation des PJ agent par id_history
                        List<Integer> idResponseTotal = new ArrayList<>( );
                        List<Integer> idCoreUploadFinal = new ArrayList<>( );

                        List<Integer> idHistoryList = _daoResourceHist.getIdHistoryListByResource( ticket.getId( ), _plugin );
                        for ( int idHistory : idHistoryList )
                        {
                            // tables workflow_task_ticketing_information et workflow_task_notify_gru_history
                            idResponseTotal = searchPjFromTaskInfoAndNotifyHistory( idHistory, idResponseTotal );

                            // table workflow_task_upload_files
                            idCoreUploadFinal = searchPjFromTaskUploadFiles( idHistory, idCoreUploadFinal );
                        }

                        if ( !idResponseTotal.isEmpty( ) )
                        {
                            managePjforS3WithIdResponse( idResponseTotal, ticket );
                        }
                        if ( !idCoreUploadFinal.isEmpty( ) )
                        {
                            // usager false
                            managePjforS3ForAgent( idCoreUploadFinal, ticket, false );
                        }

                        TicketPjHome.updateTransfertStatus( STATUS_TRANSFERT_OK, ticket.getId( ) );

                    } catch ( Exception e )
                    {
                        TransactionManager.rollBack( _plugin );
                        sb.add( "Annulation de la migration pour le ticket id : " + idTicket );
                        AppLogService.error( e );
                    }
                    TransactionManager.commitTransaction( _plugin );
                }
                TicketPjHome.updateTransfertStatus( STATUS_TRANSFERT_OK, ticket.getId( ) );
            }
        }
        else
        {
            sb.add( "La liste est vide, il n'y a plus de pj à transférer" );
        }
    }

    /**
     * Search ditinct id_core from workflow_task_upload_files
     *
     * @param idHistory
     *            the id history
     * @param idCoreUploadFinal
     *            the list of id file to complete
     * @return the list of distinct id_file
     */
    private List<Integer> searchPjFromTaskUploadFiles( int idHistory, List<Integer> idCoreUploadFinal )
    {
        List<Integer> idCoreUploadList = findIsFileFromTaskUploadFiles( idHistory, idCoreUploadFinal );
        if ( !idCoreUploadList.isEmpty( ) )
        {
            idCoreUploadFinal.addAll( idCoreUploadList );
        }
        return idCoreUploadFinal.stream( ).distinct( ).collect( Collectors.toList( ) );
    }

    /**
     * Search list of distinct id_response
     *
     * @param idHistory
     *            the id history
     * @param idResponseTotal
     *            the list of id response to complete
     * @return the list of distinct id_response
     */
    private List<Integer> searchPjFromTaskInfoAndNotifyHistory( int idHistory, List<Integer> idResponseTotal )
    {
        List<Integer> idResponseTaskInfoList = findIdResponseFromTaskInfo( idHistory, idResponseTotal );
        List<Integer> idResponseNotifyHistoryList = findIdResponseFromNotifyHistory( idHistory, idResponseTotal );

        if ( !idResponseTaskInfoList.isEmpty( ) )
        {
            idResponseTotal.addAll( idResponseTaskInfoList );
        }
        if ( !idResponseNotifyHistoryList.isEmpty( ) )
        {
            idResponseTotal.addAll( idResponseNotifyHistoryList );
        }
        return idResponseTotal.stream( ).distinct( ).collect( Collectors.toList( ) );
    }

    /**
     * Manage pj for usager
     *
     * @param ticket
     *            the ticket
     */
    private void managePjforS3ForUsager( Ticket ticket )
    {
        List<Integer> usagerAttachment = TicketTransfertPjService.findUsagerAttachment( ticket );

        if ( ( null != usagerAttachment ) && !usagerAttachment.isEmpty( ) )
        {
            usagerAttachment = cleanIdCoreList( usagerAttachment );
            // usager true
            insertTicketPjAndCleanIdCoreList( usagerAttachment, ticket, true );
        }
    }

    /**
     * Keep in list only the id_file still existinf in core_file
     *
     * @param idFileList
     *            the list of id file
     * @return the clean list of id file
     */
    private List<Integer> cleanIdCoreList( List<Integer> idFileList )
    {
        List<Integer> cleanidList = new ArrayList<>( );
        for ( Integer idFile : idFileList )
        {
            if ( TicketPjHome.isFileExistInCoreFile( idFile ) )
            {
                File file = FileHome.findByPrimaryKey( idFile );
                if ( TicketPjHome.isFileExistInCorePhysicalFile( file.getPhysicalFile( ).getIdPhysicalFile( ) ) )
                {
                    cleanidList.add( idFile );
                }
            }
        }
        return cleanidList;
    }

    /**
     * Manage pj for agent
     *
     * @param idCoreUploadFinal
     *            the list of id file to complete
     * @param ticket
     *            the ticket
     * @param isUsagerPj
     *            true if the pj is from usager otherwise false
     */
    private void managePjforS3ForAgent( List<Integer> idCoreUploadFinal, Ticket ticket, boolean isUsagerPj )
    {
        idCoreUploadFinal = cleanIdCoreList( idCoreUploadFinal );
        insertTicketPjAndCleanIdCoreList( idCoreUploadFinal, ticket, isUsagerPj );
    }

    /**
     * Manage Pj fron id_response list
     *
     * @param idResponseTotal
     *            the list of id response to complete
     * @param ticket
     *            the ticket
     */
    private void managePjforS3WithIdResponse( List<Integer> idResponseTotal, Ticket ticket )
    {
        Map<Integer, Integer> coreIdFileAgentFromIdResponseListClean = new HashMap<Integer, Integer>( );
        // map id_response id_file
        Map<Integer, Integer> coreIdFileAgentFromIdResponseList = TicketHome.selectCoreFileForAgentPjMap( idResponseTotal );
        if ( !coreIdFileAgentFromIdResponseList.isEmpty( ) )
        {
            for ( Entry<Integer, Integer> entry : coreIdFileAgentFromIdResponseList.entrySet( ) )
            {
                if ( TicketPjHome.isFileExistInCoreFile( entry.getValue( ) ) )
                {
                    coreIdFileAgentFromIdResponseListClean.put( entry.getKey( ), entry.getValue( ) );
                }
            }
            // usager false
            insertTicketPjFromMap( coreIdFileAgentFromIdResponseListClean, ticket, false );
        }
    }

    /**
     * Find the final list of id core from workflow_task_upload_files
     *
     * @param idHistory
     *            the id history
     * @param idCoreUploadFinal
     *            the list of id file to complete
     * @return the final list of id core from workflow_task_upload_files
     */
    private List<Integer> findIsFileFromTaskUploadFiles( int idHistory, List<Integer> idCoreUploadFinal )
    {
        List<Integer> idCoreUploadFound = TicketTransfertPjService.findUploadFiles( idHistory );
        idCoreUploadFinal.addAll( idCoreUploadFound );
        return idCoreUploadFinal;
    }

    /**
     * Find the final list of id core from workflow_task_ticketing_information
     *
     * @param idHistory
     *            the id history
     * @param idResponseTotal
     *            the list of id response to complete
     * @return the final list of id core from workflow_task_ticketing_information
     */
    private List<Integer> findIdResponseFromTaskInfo( int idHistory, List<Integer> idResponseTotal )
    {
        List<Integer> idResponseFromTaskInfo = ticketTaskInfoService( idHistory );
        idResponseTotal.addAll( idResponseFromTaskInfo );
        return idResponseTotal;
    }

    /**
     * Find the final list of id core from workflow_task_notify_gru_history
     *
     * @param idHistory
     *            the id history
     * @param idResponseTotal
     *            the list of id response to complete
     * @return the final list of id core from workflow_task_notify_gru_history
     */
    private List<Integer> findIdResponseFromNotifyHistory( int idHistory, List<Integer> idResponseTotal )
    {
        List<Integer> idResponseFromNotifyHistory = ticketNotifyHsitoryService( idHistory );
        idResponseTotal.addAll( idResponseFromNotifyHistory );
        return idResponseTotal;
    }

    /**
     * Insert pj in ticketing_ticket_pj and update file name in core_file with a id File list
     *
     * @param idFileList
     *            the id pj list
     * @param ticket
     *            the ticket
     * @param isUsagerPj
     *            true if the pj is from usager otherwise false
     */
    private void insertTicketPjAndCleanIdCoreList( List<Integer> idFileList, Ticket ticket, boolean isUsagerPj )
    {
        idFileList = cleanIdCoreList( idFileList );
        insertTicketPj( idFileList, ticket, isUsagerPj );
    }

    /**
     * Insert pj in ticketing_ticket_pj from id file list
     *
     * @param idFileList
     *            the id file list
     * @param ticket
     *            the ticket
     * @param isUsagerPj
     *            true if the pj is from usager otherwise false
     */
    private void insertTicketPj( List<Integer> idFileList, Ticket ticket, boolean isUsagerPj )
    {
        if ( ( null != idFileList ) && !idFileList.isEmpty( ) )
        {
            for ( Integer idFile : idFileList )
            {
                TicketPj pj = new TicketPj( );
                pj.setIdTicket( ticket.getId( ) );
                pj.setIdFile( idFile );
                for ( Response response : ticket.getListResponse( ) )
                {
                    if ( ( null != response.getFile( ) ) && ( response.getFile( ).getIdFile( ) == idFile ) )
                    {
                        pj.setIdResponse( response.getIdResponse( ) );
                        break;
                    }
                }
                pj.setUrlTicketing( "" );
                pj.setStockageTicketing( 0 );
                pj.setUsager( isUsagerPj );
                TicketPjHome.create( pj );
            }
        }
    }

    /**
     * Insert pj in ticketing_ticket_pj from id file and id response map
     *
     * @param idsMaps
     *            the id file id response map
     * @param ticket
     *            the ticket
     * @param isUsagerPj
     *            true if the pj is from usager otherwise false
     */
    private void insertTicketPjFromMap( Map<Integer, Integer> idsMaps, Ticket ticket, boolean isUsagerPj )
    {
        if ( ( null != idsMaps ) && !idsMaps.isEmpty( ) )
        {
            for ( Entry<Integer, Integer> entry : idsMaps.entrySet( ) )
            {
                TicketPj pj = new TicketPj( );
                pj.setIdTicket( ticket.getId( ) );
                pj.setIdFile( entry.getValue( ) );
                pj.setIdResponse( entry.getKey( ) );
                pj.setUrlTicketing( "" );
                pj.setStockageTicketing( 0 );
                pj.setUsager( isUsagerPj );
                TicketPjHome.create( pj );
            }
        }
    }

    /**
     * find historic data From task info table
     *
     * @param ticket
     *            the ticket to anonymizes
     */
    private List<Integer> ticketTaskInfoService( int idHistory )
    {
        String valueInfo = _daoTaskInfo.getInfoHistoryValueByIdHistory( idHistory, _plugin );
        List<Integer> idResponseTotal = new ArrayList<>( );
        if ( !valueInfo.isEmpty( ) )
        {
            String valueInfoUpdated = _daoTaskInfo.getInfoHistoryValueByIdHistory( idHistory, _plugin );
            if ( valueInfoUpdated.contains( "a href=" ) )
            {
                List<Integer> idResponseListForAgent = TicketTransfertPjService.extractIdResponse( valueInfoUpdated );
                idResponseTotal.addAll( idResponseListForAgent );
            }
        }
        return idResponseTotal;
    }

    /**
     * find historic data From task info table
     *
     * @param ticket
     *            the ticket to anonymizes
     */
    private List<Integer> ticketNotifyHsitoryService( int idHistory )
    {
        Map<String, String> valueNotifyMessages = _daoAnonymisation.loadMessageNotifyHIstoryTotal( idHistory, _plugin );
        List<Integer> idResponseTotal = new ArrayList<>( );
        if ( ( null != valueNotifyMessages ) && !valueNotifyMessages.isEmpty( ) )
        {
            for ( Map.Entry<String, String> mapEntry : valueNotifyMessages.entrySet( ) )
            {
                if ( ( null != mapEntry.getValue( ) ) && mapEntry.getValue( ).contains( "a href=" ) )
                {
                    List<Integer> idResponseListForAgent = TicketTransfertPjService.extractIdResponse( mapEntry.getValue( ) );
                    idResponseTotal.addAll( idResponseListForAgent );
                }
            }
        }
        return idResponseTotal;
    }
}
