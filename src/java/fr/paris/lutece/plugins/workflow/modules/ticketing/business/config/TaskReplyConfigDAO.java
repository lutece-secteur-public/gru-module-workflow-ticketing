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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class is a data access object for {@link fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskReplyConfig}
 *
 */
public class TaskReplyConfigDAO implements ITaskConfigDAO<TaskReplyConfig>
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task, message_direction, close_ticket"
            + " FROM workflow_task_ticketing_reply_config WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_ticketing_reply_config"
            + " (id_task, message_direction, close_ticket) VALUES (?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_ticketing_reply_config" + " SET id_task = ?, message_direction = ?, close_ticket = ?"
            + " WHERE id_task = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_ticketing_reply_config  WHERE id_task = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( TaskReplyConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin( ) );

        daoUtil.setInt( 1, config.getIdTask( ) );
        daoUtil.setInt( 2, config.getMessageDirection( ).ordinal( ) );
        daoUtil.setBoolean( 3, config.isCloseTicket( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskReplyConfig load( int nIdTask )
    {
        TaskReplyConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            config = new TaskReplyConfig( );
            config.setIdTask( daoUtil.getInt( 1 ) );
            config.setMessageDirection( MessageDirection.valueOf( daoUtil.getInt( 2 ) ) );
            config.setCloseTicket( daoUtil.getBoolean( 3 ) );
        }

        daoUtil.free( );

        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskReplyConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) );

        daoUtil.setInt( 1, config.getIdTask( ) );
        daoUtil.setInt( 2, config.getMessageDirection( ).ordinal( ) );
        daoUtil.setBoolean( 3, config.isCloseTicket( ) );
        daoUtil.setInt( 4, config.getIdTask( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
