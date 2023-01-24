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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 */
public class TaskModifyTicketCategoryConfigDAO implements ITaskConfigDAO<TaskModifyTicketCategoryConfig>
{
    private static final String SQL_QUERY_SELECT_FOR_TASK = "SELECT id_entry FROM workflow_task_ticketing_modify_config WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE_FOR_TASK = "DELETE FROM workflow_task_ticketing_modify_config WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_ticketing_modify_config ( id_task, id_entry ) VALUES ( ?,? )";
    private static final String SQL_QUERY_ADD_INSERT = ", ( ?,? )";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskModifyTicketCategoryConfig config )
    {
        if ( config.getSelectedEntries( ).isEmpty( ) )
        {
            return;
        }

        StringBuilder strQuery = new StringBuilder( SQL_QUERY_INSERT );

        for ( int nIdx = 1; nIdx < config.getSelectedEntries( ).size( ); nIdx++ )
        {
            strQuery.append( SQL_QUERY_ADD_INSERT );
        }

        try ( DAOUtil daoUtil = new DAOUtil( strQuery.toString( ), PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndexDao = 1;

            for ( int nIdx = 0; nIdx < config.getSelectedEntries( ).size( ); nIdx++ )
            {
                daoUtil.setInt( nIndexDao++, config.getIdTask( ) );
                daoUtil.setInt( nIndexDao++, config.getSelectedEntries( ).get( nIdx ) );
            }
            daoUtil.executeUpdate( );
        }

    }

    /**
     * Store for TaskModifyTicketCategoryConfig delete all existing and insert new config {@inheritDoc}
     */
    @Override
    public void store( TaskModifyTicketCategoryConfig config )
    {
        delete( config.getIdTask( ) );
        insert( config );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskModifyTicketCategoryConfig load( int nIdTask )
    {
        TaskModifyTicketCategoryConfig config = new TaskModifyTicketCategoryConfig( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FOR_TASK, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );


            config.setIdTask( nIdTask );

            while ( daoUtil.next( ) )
            {
                config.addSelectedEntry( daoUtil.getInt( 1 ) );
            }
        }

        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FOR_TASK, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }
}
