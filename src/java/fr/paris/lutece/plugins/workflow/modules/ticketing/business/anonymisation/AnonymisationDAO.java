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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.anonymisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class AnonymisationDAO implements IAnonymisationDAO
{
    private static final String SQL_QUERY_SELECT_EMAIL_COMMENT_VALUE_HISTORY = "SELECT comment_value FROM workflow_task_comment_value WHERE id_history = ?";
    private static final String SQL_QUERY_UPDATE_ANONYMISATION_COMMENT_VALUE_HISTORY = "UPDATE workflow_task_comment_value SET comment_value=? WHERE id_history = ?";
    private static final String SQL_QUERY_SELECT_UPLOAD_FILES_HISTORY = "SELECT id_file FROM workflow_task_upload_files WHERE id_history = ?";
    private static final String SQL_QUERY_DELETE_UPLOAD_FILE_REFERENCE_FOR_ANONYMISATION = "DELETE FROM workflow_task_upload_files WHERE id_history = ?";
    private static final String SQL_QUERY_SELECT_MESSAGE_NOTIFY_GRU_HISTORY_TOTAL = "SELECT message_email, message_guichet, message_agent,message_broadcast,message_sms FROM workflow_task_notify_gru_history WHERE id_history = ?";
    private static final String SQL_QUERY_UPDATE_ANONYMISATION_NOTIFY_GRU_HISTORY_TOTAL = "UPDATE workflow_task_notify_gru_history SET ";
    private static final String SQL_QUERY_WHERE_ID_HISTORY = " WHERE id_history = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, String> loadMessageNotifyHIstoryTotal( int idHistory, Plugin plugin )
    {
        Map<String, String> messageListHistory = new HashMap<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MESSAGE_NOTIFY_GRU_HISTORY_TOTAL, plugin ) )
        {
            daoUtil.setInt( 1, idHistory );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                messageListHistory.put( "message_email", daoUtil.getString( 1 ) );
                messageListHistory.put( "message_guichet", daoUtil.getString( 2 ) );
                messageListHistory.put( "message_agent", daoUtil.getString( 3 ) );
                messageListHistory.put( "message_broadcast", daoUtil.getString( 4 ) );
                messageListHistory.put( "message_sms", daoUtil.getString( 5 ) );

            }
        }
        return messageListHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeAnonymisationNotifyGruHistoryTotal( Map<String, String> messagesList, int idHistory, Plugin plugin )
    {
        StringBuilder sbSQL = new StringBuilder( );
        sbSQL.append( SQL_QUERY_UPDATE_ANONYMISATION_NOTIFY_GRU_HISTORY_TOTAL );
        int nbCount = 1;

        for ( Map.Entry<String, String> mapEntry : messagesList.entrySet( ) )
        {
            sbSQL.append( mapEntry.getKey( ) + " = ? " );
            if ( nbCount < messagesList.size( ) )
            {
                sbSQL.append( ", " );
            }
            nbCount++;
        }

        sbSQL.append( SQL_QUERY_WHERE_ID_HISTORY );

        try ( DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin ) )
        {
            int nIndex = 1;

            for ( Map.Entry<String, String> mapEntry : messagesList.entrySet( ) )
            {
                daoUtil.setString( nIndex++, mapEntry.getValue( ) );
            }
            daoUtil.setInt( nIndex, idHistory );

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
    public void storeAnonymisationCommentValue( String message, int idHistory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ANONYMISATION_COMMENT_VALUE_HISTORY, plugin ) )
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUploadLines( int idHistory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_UPLOAD_FILE_REFERENCE_FOR_ANONYMISATION, plugin ) )
        {
            daoUtil.setInt( 1, idHistory );

            daoUtil.executeUpdate( );
        }
    }

}
