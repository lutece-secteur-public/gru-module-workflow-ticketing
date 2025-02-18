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

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskChooseNextStateConfigDAO implements ITaskConfigDAO<TaskChooseNextStateConfig>
{

    // Queries
    private static final String SQL_INSERT = "INSERT INTO workflow_task_choose_state_config (id_task, controller_name, id_state_ok, id_state_ko) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE workflow_task_choose_state_config SET controller_name = ?, id_state_ok = ?, id_state_ko = ? WHERE id_task = ?";
    private static final String SQL_DELETE = "DELETE FROM workflow_task_choose_state_config WHERE id_task = ?";
    private static final String SQL_SELECT = "SELECT id_task, controller_name, id_state_ok, id_state_ko FROM workflow_task_choose_state_config WHERE id_task = ?";

    @Override
    public void insert( TaskChooseNextStateConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_INSERT ) )
        {
            int nPos = 1;
            daoUtil.setInt( nPos++, config.getIdTask( ) );
            daoUtil.setString( nPos++, config.getControllerName( ) );
            daoUtil.setInt( nPos++, config.getIdStateOK( ) );
            daoUtil.setInt( nPos++, config.getIdStateKO( ) );
            daoUtil.executeUpdate( );
        }

    }

    @Override
    public void store( TaskChooseNextStateConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_UPDATE ) )
        {
            int nPos = 1;
            daoUtil.setString( nPos++, config.getControllerName( ) );
            daoUtil.setInt( nPos++, config.getIdStateOK( ) );
            daoUtil.setInt( nPos++, config.getIdStateKO( ) );
            daoUtil.setInt( nPos++, config.getIdTask( ) );

            daoUtil.executeUpdate( );
        }

    }

    @Override
    public TaskChooseNextStateConfig load( int nIdTask )
    {
        TaskChooseNextStateConfig config = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_SELECT ) )
        {
            int nPos = 1;
            daoUtil.setInt( nPos++, nIdTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nIndex = 1;
                config = new TaskChooseNextStateConfig( );
                config.setIdTask( daoUtil.getInt( nIndex++ ) );
                config.setControllerName( daoUtil.getString( nIndex++ ) );
                config.setIdStateOK( daoUtil.getInt( nIndex++ ) );
                config.setIdStateKO( daoUtil.getInt( nIndex++ ) );
            }
        }
        return config;
    }

    @Override
    public void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_DELETE ) )
        {
            int nPos = 1;
            daoUtil.setInt( nPos++, nIdTask );

            daoUtil.executeUpdate( );
        }
    }
}
