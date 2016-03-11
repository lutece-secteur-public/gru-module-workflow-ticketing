/*
 * Copyright (c) 2002-2015, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * TaskInformationDAO
 *
 */
public class TaskInformationDAO implements ITaskInformationDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_history,id_task,information_value,id_channel  " +
        "FROM workflow_task_ticketing_information WHERE id_history=? AND id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_ticketing_information " +
        "(id_history,id_task,information_value,id_channel ) VALUES( ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE_BY_HISTORY = "DELETE FROM workflow_task_ticketing_information WHERE id_history=? AND id_task=?";
    private static final String SQL_QUERY_DELETE_BY_TASK = "DELETE FROM workflow_task_ticketing_information WHERE id_task=?";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskInformation taskInformation, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 1, taskInformation.getIdResourceHistory(  ) );
        daoUtil.setInt( 2, taskInformation.getIdTask(  ) );
        daoUtil.setString( 3, taskInformation.getValue(  ) );
        daoUtil.setInt( 4, taskInformation.getIdChannel(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskInformation load( int nIdHistory, int nIdTask, Plugin plugin )
    {
        TaskInformation taskInformation = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdHistory );
        daoUtil.setInt( 2, nIdTask );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            taskInformation = new TaskInformation(  );
            taskInformation.setIdResourceHistory( daoUtil.getInt( 1 ) );
            taskInformation.setIdTask( daoUtil.getInt( 2 ) );
            taskInformation.setValue( daoUtil.getString( 3 ) );
            taskInformation.setIdChannel( daoUtil.getInt( 4 ) );
        }

        daoUtil.free(  );

        return taskInformation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, plugin );

        daoUtil.setInt( 1, nIdHistory );
        daoUtil.setInt( 2, nIdTask );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTask( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
