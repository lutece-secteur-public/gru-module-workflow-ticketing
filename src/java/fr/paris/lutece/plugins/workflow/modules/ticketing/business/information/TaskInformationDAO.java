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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.information;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 * TaskInformationDAO
 *
 */
public class TaskInformationDAO implements ITaskInformationDAO
{
    private static final String IDS_TO_REPLACE                       = "%IDS%";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_history,id_task,information_value  "
            + "FROM workflow_task_ticketing_information WHERE id_history=? AND id_task=?";
    private static final String SQL_QUERY_FIND_BY_ID_HISTORY = "SELECT  information_value FROM workflow_task_ticketing_information WHERE id_history=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_ticketing_information "
            + "(id_history,id_task,information_value ) VALUES( ?, ?, ? )";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_ticketing_information SET information_value=? WHERE id_history = ?";
    private static final String SQL_QUERY_DELETE_BY_HISTORY_AND_TASK = "DELETE FROM workflow_task_ticketing_information WHERE id_history=? AND id_task=?";
    private static final String SQL_QUERY_DELETE_BY_TASK = "DELETE FROM workflow_task_ticketing_information WHERE id_task=?";
    private static final String SQL_QUERY_DELETE_BY_HISTORY_LIST     = "DELETE FROM workflow_task_ticketing_information WHERE id_history IN (" + IDS_TO_REPLACE + ")";
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskInformation taskInformation, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, taskInformation.getIdResourceHistory( ) );
            daoUtil.setInt( 2, taskInformation.getIdTask( ) );
            daoUtil.setString( 3, taskInformation.getValue( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskInformation load( int nIdHistory, int nIdTask, Plugin plugin )
    {
        TaskInformation taskInformation = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.setInt( 2, nIdTask );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                taskInformation = new TaskInformation( );
                taskInformation.setIdResourceHistory( daoUtil.getInt( 1 ) );
                taskInformation.setIdTask( daoUtil.getInt( 2 ) );
                taskInformation.setValue( daoUtil.getString( 3 ) );
            }
        }
        return taskInformation;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update( int idHistory, String infoValue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setString( 1, infoValue );
            daoUtil.setInt( 2, idHistory );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfoHistoryValueByIdHistory( int idTicket, Plugin plugin )
    {
        String infoHistoryValue = "";

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_HISTORY, plugin ) )
        {
            daoUtil.setInt( 1, idTicket );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                infoHistoryValue = daoUtil.getString( 1 );
            }
        }
        return infoHistoryValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY_AND_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.setInt( 2, nIdTask );

            daoUtil.executeUpdate( );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTask( int nIdTask, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    //// PURGE ANONYMISATION ////

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByHistoryList( List<Integer> idHistoryList, Plugin plugin )
    {
        final String sql = StringUtils.replace( SQL_QUERY_DELETE_BY_HISTORY_LIST, IDS_TO_REPLACE, StringUtils.join( idHistoryList, "," ) );
        try ( DAOUtil daoUtil = new DAOUtil( sql, plugin ) )
        {
            daoUtil.executeUpdate( );
        }
    }
}
