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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implementation of the TicketEmailExternalUserHistory DAO
 */
public class TicketEmailExternalUserHistoryDAO implements ITicketEmailExternalUserHistoryDAO
{
    private static final String IDS_TO_REPLACE               = "%IDS%";
    private static final String SQL_QUERY_FIND_BY_ID_HISTORY = " SELECT id_task, id_history, id_message_external_user FROM workflow_task_ticketing_email_external_user_history "
            + " WHERE id_history = ? ";
    private static final String SQL_QUERY_FIND_BY_ID_MESSAGE = " SELECT id_task, id_history, id_message_external_user FROM workflow_task_ticketing_email_external_user_history "
            + " WHERE id_message_external_user = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_email_external_user_history ( id_task, id_history, id_message_external_user ) "
            + " VALUES ( ?,?,? ) ";
    private static final String SQL_QUERY_DELETE_BY_HISTORY = " DELETE FROM workflow_task_ticketing_email_external_user_history WHERE id_history = ? ";
    private static final String SQL_QUERY_DELETE_BY_HISTORY_LIST = " DELETE FROM workflow_task_ticketing_email_external_user_history WHERE id_history IN (" + IDS_TO_REPLACE + ")";
    private static final Plugin PLUGIN                           = WorkflowTicketingPlugin.getPlugin( );

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public synchronized void insert( TicketEmailExternalUserHistory emailExternalUser )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, PLUGIN ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, emailExternalUser.getIdTask( ) );
            daoUtil.setInt( nIndex++, emailExternalUser.getIdResourceHistory( ) );
            daoUtil.setInt( nIndex++, emailExternalUser.getIdMessageExternalUser( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketEmailExternalUserHistory loadByIdHistory( int nIdHistory )
    {
        TicketEmailExternalUserHistory emailExternalUser = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_HISTORY, PLUGIN ) )
        {
            daoUtil.setInt( 1, nIdHistory );

            daoUtil.executeQuery( );

            int nIndex = 1;

            if ( daoUtil.next( ) )
            {
                emailExternalUser = new TicketEmailExternalUserHistory( );
                emailExternalUser.setIdTask( daoUtil.getInt( nIndex++ ) );
                emailExternalUser.setIdResourceHistory( daoUtil.getInt( nIndex++ ) );
                emailExternalUser.setIdMessageExternalUser( daoUtil.getInt( nIndex++ ) );
            }
        }
        return emailExternalUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TicketEmailExternalUserHistory> loadByIdMessageExternalUser( int nIdMessageAgent )
    {
        TicketEmailExternalUserHistory emailExternalUser = null;
        List<TicketEmailExternalUserHistory> lstEmailExternalUser = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_MESSAGE, PLUGIN ) )
        {
            daoUtil.setInt( 1, nIdMessageAgent );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nIndex = 1;
                emailExternalUser = new TicketEmailExternalUserHistory( );
                emailExternalUser.setIdTask( daoUtil.getInt( nIndex++ ) );
                emailExternalUser.setIdResourceHistory( daoUtil.getInt( nIndex++ ) );
                emailExternalUser.setIdMessageExternalUser( daoUtil.getInt( nIndex++ ) );
                lstEmailExternalUser.add( emailExternalUser );
            }
        }

        return lstEmailExternalUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void deleteByHistory( int nIdHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, PLUGIN ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEmailExternalUserByIdHistoryList( List<Integer> idHistoryList )
    {
        final String sql = StringUtils.replace( SQL_QUERY_DELETE_BY_HISTORY_LIST, IDS_TO_REPLACE, StringUtils.join( idHistoryList, "," ) );
        try ( DAOUtil daoUtil = new DAOUtil( sql, PLUGIN ) )
        {
            daoUtil.executeUpdate( );
        }
    }
}
