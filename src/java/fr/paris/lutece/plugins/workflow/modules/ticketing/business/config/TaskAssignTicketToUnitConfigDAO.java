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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskAssignTicketToUnitConfigDAO implements ITaskConfigDAO<TaskAssignTicketToUnitConfig>
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, level_1, level_2, level_3 FROM workflow_task_ticketing_assign_unit_config "
            + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_assign_unit_config ( id_task, level_1, level_2, level_3 ) "
            + " VALUES ( ?,?,?,? ) ";
    private static final String SQL_QUERY_UPDATE = " UPDATE workflow_task_ticketing_assign_unit_config SET level_1 = ?, level_2 = ?, level_3 = ?  "
            + " WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_ticketing_assign_unit_config WHERE id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskAssignTicketToUnitConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, config.getIdTask( ) );
            daoUtil.setBoolean( nIndex++, config.isLevel1( ) );
            daoUtil.setBoolean( nIndex++, config.isLevel2( ) );
            daoUtil.setBoolean( nIndex, config.isLevel3( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskAssignTicketToUnitConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndex = 1;

            daoUtil.setBoolean( nIndex++, config.isLevel1( ) );
            daoUtil.setBoolean( nIndex++, config.isLevel2( ) );
            daoUtil.setBoolean( nIndex++, config.isLevel3( ) );

            daoUtil.setInt( nIndex, config.getIdTask( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskAssignTicketToUnitConfig load( int nIdTask )
    {
        TaskAssignTicketToUnitConfig config = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            daoUtil.setInt( 1, nIdTask );

            daoUtil.executeQuery( );

            int nIndex = 1;

            if ( daoUtil.next( ) )
            {
                config = new TaskAssignTicketToUnitConfig( );
                config.setIdTask( daoUtil.getInt( nIndex++ ) );
                config.setLevel1( daoUtil.getBoolean( nIndex++ ) );
                config.setLevel2( daoUtil.getBoolean( nIndex++ ) );
                config.setLevel3( daoUtil.getBoolean( nIndex ) );
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

}
