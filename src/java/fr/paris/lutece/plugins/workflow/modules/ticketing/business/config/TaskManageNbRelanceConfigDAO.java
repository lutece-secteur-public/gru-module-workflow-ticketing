/*
 * Copyright (c) 2002-2026, City of Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskManageNbRelanceConfigDAO implements ITaskConfigDAO<TaskManageNbRelanceConfig>
{

    // language=MySQL
    private static final String SQL_QUERY_SELECT = "SELECT id_task, is_reinit, is_usager FROM workflow_task_manage_nb_relance_config WHERE id_task = ?";
    // language=MySQL
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_manage_nb_relance_config (id_task, is_reinit, is_usager) VALUES (?, ?, ?)";
    // language=MySQL
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_manage_nb_relance_config SET is_reinit = ?, is_usager = ? WHERE id_task = ?";
    // language=MySQL
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_manage_nb_relance_config WHERE id_task = ?";

    @Override
    public void insert( TaskManageNbRelanceConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, config.getIdTask( ) );
            daoUtil.setBoolean( nIndex++, config.getIsReinit( ) );
            daoUtil.setBoolean( nIndex, config.getIsUsager( ) );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void store( TaskManageNbRelanceConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setBoolean( nIndex++, config.getIsReinit( ) );
            daoUtil.setBoolean( nIndex++, config.getIsUsager( ) );
            daoUtil.setInt( nIndex, config.getIdTask( ) );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public TaskManageNbRelanceConfig load( int nIdTask )
    {
        TaskManageNbRelanceConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                config = new TaskManageNbRelanceConfig( );
                config.setIdTask( daoUtil.getInt( 1 ) );
                config.setIsReinit( daoUtil.getBoolean( 2 ) );
                config.setIsUsager( daoUtil.getBoolean( 3 ) );
            }
        }
        return config;
    }

    @Override
    public void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }
}
