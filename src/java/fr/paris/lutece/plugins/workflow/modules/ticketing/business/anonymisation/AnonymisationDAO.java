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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.anonymisation;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

public class AnonymisationDAO implements IAnonymisationDAO
{
    private static final String SQL_QUERY_SELECT_EMAIL_MESSAGE_NOTIFY_GRU_HISTORY    = "SELECT message_email FROM workflow_task_notify_gru_history WHERE id_history = ?";
    private static final String SQL_QUERY_UPDATE_ANONYMISATION_NOTIFY_GRU_HISTORY    = "UPDATE workflow_task_notify_gru_history SET message_email =? WHERE id_history = ?";
    private static final String SQL_QUERY_SELECT_EMAIL_COMMENT_VALUE_HISTORY         = "SELECT comment_value FROM workflow_task_comment_value WHERE id_history = ?";
    private static final String SQL_QUERY_UPDATE_ANONYMISATION_COMMENT_VALUE_HISTORY = "UPDATE workflow_task_comment_value SET comment_value=? WHERE id_history = ?";
    private static final String SQL_QUERY_SELECT_UPLOAD_FILES_HISTORY                = "SELECT id_file FROM workflow_task_upload_files WHERE id_history = ?";


    /**
     * {@inheritDoc }
     */
    @Override
    public String loadMessageNotifyHIstory( int idHistory, Plugin plugin )
    {
        String message = "";
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EMAIL_MESSAGE_NOTIFY_GRU_HISTORY, plugin ) )
        {
            daoUtil.setInt( 1, idHistory );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                message = daoUtil.getString( 1 );

            }
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeAnonymisationNotifyGruHistory( String message, int idHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ANONYMISATION_NOTIFY_GRU_HISTORY, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, message );
            daoUtil.setInt( nIndex++, idHistory );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String loadCommentValue( int idHistory, Plugin plugin )
    {
        String message = "";
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EMAIL_COMMENT_VALUE_HISTORY, plugin ) )
        {
            daoUtil.setInt( 1, idHistory );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                message = daoUtil.getString( 1 );

            }
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeAnonymisationCommentValue( String message, int idHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ANONYMISATION_COMMENT_VALUE_HISTORY, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, message );
            daoUtil.setInt( nIndex++, idHistory );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIdUploadFilesByIdHistory( int idHistory, Plugin plugin )
    {
        List<Integer> uploadIdFilesHistoryList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_UPLOAD_FILES_HISTORY, plugin ) )
        {
            daoUtil.setInt( 1, idHistory );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                uploadIdFilesHistoryList.add( daoUtil.getInt( 1 ) );
            }
        }
        return uploadIdFilesHistoryList;
    }

}
