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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.purgeanonymisation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import fr.paris.lutece.plugins.ticketing.business.file.TicketFileHome;
import fr.paris.lutece.plugins.ticketing.business.profilstrois.Profilstrois;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPj;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPjHome;
import fr.paris.lutece.plugins.ticketing.service.strois.StockageService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.anonymisation.IAnonymisationDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc.ITicketEmailExternalUserCcDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.ITicketEmailExternalUserHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.recipient.ITicketEmailExternalUserRecipientDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.information.ITaskInformationDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.IEditableTicketDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.IEditableTicketFieldDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 *
 * TaskInformationService
 *
 */
public class PurgeAnonymisationService implements IPurgeAnonymisationService
{
    /** The Constant BEAN_SERVICE. */
    public static final String                          BEAN_SERVICE                     = "workflow-ticketing.purgeAnonymisationService";

    @Inject
    private ITicketEmailExternalUserMessageDAO   daoEmailExternalUsermessage;
    @Inject
    private ITaskInformationDAO                  daoTaskInfo;
    @Inject
    private IEditableTicketDAO                   daoEditableTicketHist;
    @Inject
    private IEditableTicketFieldDAO              daoEditableTicketFieldDAO;
    @Inject
    private IAnonymisationDAO                    daoAnonymisation;
    @Inject
    private ITicketEmailExternalUserRecipientDAO daoEmailExternalUserRecipientDAO;
    @Inject
    private ITicketEmailExternalUserCcDAO        daoEmailExternalUserCcHist;
    @Inject
    private ITicketEmailExternalUserHistoryDAO   daoEmailExternalUserHist;

    private final StockageService                     _stockageS3DaemonMinio  = new StockageService( Profilstrois.PROFIL_MINIO_DAEMON_NAME );
    private final StockageService                     _stockageS3DaemonNetapp = new StockageService( Profilstrois.PROFIL_NETAPP_DAEMON_NAME );

    private static Plugin                             plugin                      = WorkflowTicketingPlugin.getPlugin( );


    @Override
    public void purgeHistoryAnonymisation( List<Integer> idHistoryList, Plugin plugin )
    {
        purgeInformations( idHistoryList, plugin );

        purgeEditableTables( idHistoryList, plugin );

        purgeNotifyGru( idHistoryList, plugin );

        purgeComments( idHistoryList, plugin );

        purgeUploads( idHistoryList, plugin );

        purgeExternalEmails( idHistoryList, plugin );
    }

    private void purgeInformations( List<Integer> idHistoryList, Plugin plugin2 )
    {
        // table workflow_task_ticketing_information
        daoTaskInfo.deleteByHistoryList( idHistoryList, plugin );
    }

    private void purgeEditableTables( List<Integer> idHistoryList, Plugin plugin2 )
    {
        // table workflow_task_ticketing_editable_ticket
        daoEditableTicketHist.deleteByIdHistoryList( idHistoryList );
        // table workflow_task_ticketing_editable_ticket_field
        daoEditableTicketFieldDAO.deleteByHistoryList( idHistoryList, plugin );
    }

    private void purgeNotifyGru( List<Integer> idHistoryList, Plugin plugin2 )
    {
        // table workflow_task_notify_gru_history
        daoAnonymisation.deleteMessageNotifyGruByIdHistoryList( idHistoryList, plugin );
    }

    private void purgeComments( List<Integer> idHistoryList, Plugin plugin2 )
    {
        // table workflow_task_comment_value
        daoAnonymisation.deleteCommentValueIdHistoryList( idHistoryList, plugin );
    }

    private void purgeUploads( List<Integer> idHistoryList, Plugin plugin2 )
    {
        // table workflow_task_upload_files
        daoAnonymisation.deleteUploadFilesIdHistoryList( idHistoryList, plugin );
        // table workflow_task_upload_history
        daoAnonymisation.deleteUploadHistoryList( idHistoryList, plugin );
    }

    private void purgeExternalEmails( List<Integer> idHistoryList, Plugin plugin2 )
    {
        // table ticket_email_external_user_recipient
        daoEmailExternalUserRecipientDAO.deleteByHistoryList( idHistoryList, plugin );
        // table workflow_task_ticketing_email_external_user_cc
        daoEmailExternalUserCcHist.deleteEmailExternalUserCcByIdHistoryList( idHistoryList );
        // table workflow_task_ticketing_email_external_user_history
        daoEmailExternalUserHist.deleteEmailExternalUserByIdHistoryList( idHistoryList );
    }

    @Override
    public void purgeHistoryWorkflowTablesAnonymisation( List<Integer> idHistoryList, Plugin plugin )
    {

        // table workflow_resource_user_history
        daoAnonymisation.deleteWorkflowUserHistoryList( idHistoryList, plugin );
        // table workflow_resource_history
        daoAnonymisation.deleteHistoryWorkflowHistoryList( idHistoryList, plugin );
        // table workflow_resource_history_ticketing
        daoAnonymisation.deleteWorkflowTicketingHistoryList( idHistoryList, plugin );

    }

    @Override
    public void purgeWorkflowResourceAnonymisation( int idTicket, Plugin plugin )
    {
        // table workflow_resource_workflow
        daoAnonymisation.deleteWorkflowResource( idTicket, plugin );
    }

    // pas de retrait ligne de centralisation table ticketing_ticket_pj
    @Override
    public void deleteAllAttachment( int idTicket )
    {
        Map<Integer, Integer> attachmentListToDelete = findAllAttachment( idTicket );

        if ( !attachmentListToDelete.isEmpty( ) )
        {
            for ( Entry<Integer, Integer> entry : attachmentListToDelete.entrySet( ) )
            {
                TicketPj pj = TicketPjHome.findByIdFile( entry.getKey( ) );
                // Stockage sur BDD
                if ( entry.getValue( ) == 0 )
                {
                    int idPhysicalFile = TicketFileHome.findIdPhysicalFile( entry.getKey( ) );
                    TicketPjHome.removePhysicalFile( idPhysicalFile );
                    FileHome.remove( entry.getKey( ) );
                } else
                {
                    if ( pj.getStockageTicketing( ) == 1 )
                    {
                        _stockageS3DaemonMinio.deleteFileOnS3Serveur( pj.getUrlTicketing( ) );
                    } else
                    {
                        _stockageS3DaemonNetapp.deleteFileOnS3Serveur( pj.getUrlTicketing( ) );
                    }
                    FileHome.remove( entry.getKey( ) );
                }
            }
        }
    }

    /**
     * Find attachment for usager
     *
     * @param ticket
     *            the ticket to clean
     * @param usager
     *            boolean true if the attachement is from usager
     *
     */
    private Map<Integer, Integer> findAllAttachment( int idTicket )
    {
        return TicketPjHome.getAllIdFileToDeleteAndStockage( idTicket );
    }

    // usager
    @Override
    public void removeMessageExternalUserByIdTicket( int idTicket )
    {
        // table workflow_ticketing_email_external_user
        daoEmailExternalUsermessage.deleteMessageExternalUserByIdTicket( idTicket, plugin );
    }


}
