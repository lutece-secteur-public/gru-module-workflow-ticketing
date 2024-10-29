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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.information;

import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 * ChooseNextStateTaskInformationDAO
 *
 */
public class ChooseNextStateTaskInformationDAO implements IChooseNextStateTaskInformationDAO
{

    private static final String SQL_QUERY_SELECT = "SELECT id_history, id_task, new_state FROM workflow_task_choose_state_information WHERE id_history = ? AND id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_choose_state_information ( id_history, id_task, new_state ) VALUES (?,?,?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_choose_state_information WHERE id_history = ? AND id_task = ?";

    @Override
    public void insert( ChooseNextStateTaskInformation taskInformation )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            int index = 1;
            daoUtil.setInt( index++, taskInformation.getIdHistory( ) );
            daoUtil.setInt( index++, taskInformation.getIdTask( ) );
            daoUtil.setString( index++, taskInformation.getState( ) );

            daoUtil.executeUpdate( );
        }

    }

    @Override
    public ChooseNextStateTaskInformation load( int nIdHistory, int nIdTask )
    {
        ChooseNextStateTaskInformation res = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            int index = 1;
            daoUtil.setInt( index++, nIdHistory );
            daoUtil.setInt( index++, nIdTask );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                index = 1;
                res = new ChooseNextStateTaskInformation( );
                res.setIdHistory( daoUtil.getInt( index++ ) );
                res.setIdTask( daoUtil.getInt( index++ ) );
                res.setNewState( daoUtil.getString( index++ ) );
            }
        }
        return res;
    }

    @Override
    public void delete( int nIdHistory, int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            int index = 1;
            daoUtil.setInt( index++, nIdHistory );
            daoUtil.setInt( index++, nIdTask );

            daoUtil.executeUpdate( );
        }
    }
}