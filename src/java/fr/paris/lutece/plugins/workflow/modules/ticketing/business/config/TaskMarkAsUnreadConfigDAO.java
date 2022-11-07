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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskMarkAsUnreadConfigDAO implements ITaskConfigDAO<TaskMarkAsUnreadConfig>
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, id_marking FROM workflow_task_ticketing_mark_unread_config "
            + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_mark_unread_config ( id_task, id_marking ) " + " VALUES ( ?,? ) ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_ticketing_mark_unread_config SET id_marking = ? " + " WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_ticketing_mark_unread_config WHERE id_task = ? ";

    private static final String SQL_QUERY_FIND_BY_MARKING_ID = " SELECT id_task FROM workflow_task_ticketing_mark_unread_config " + " WHERE id_marking = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskMarkAsUnreadConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, config.getIdTask( ) );
            daoUtil.setInt( nIndex++, config.getIdMarking( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskMarkAsUnreadConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, config.getIdMarking( ) );

            daoUtil.setInt( nIndex++, config.getIdTask( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskMarkAsUnreadConfig load( int nIdTask )
    {
        TaskMarkAsUnreadConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            daoUtil.setInt( 1, nIdTask );

            daoUtil.executeQuery( );

            int nIndex = 1;

            if ( daoUtil.next( ) )
            {
                config = new TaskMarkAsUnreadConfig( );
                config.setIdTask( daoUtil.getInt( nIndex++ ) );
                config.setIdMarking( daoUtil.getInt( nIndex++ ) );
            }
        }

        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    /**
     *
     * @param nIdMarking
     *            id marking
     * @param plugin
     *            plugin
     * @return Id List By Markin gId
     */
    public List<Integer> loadTicketIdListByMarkingId( int nIdMarking, Plugin plugin )
    {
        List<Integer> listIdTask = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_MARKING_ID, plugin ) )
        {
            daoUtil.setInt( 1, nIdMarking );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listIdTask.add( daoUtil.getInt( 1 ) );
            }
        }

        return listIdTask;
    }
}
