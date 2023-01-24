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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implementation of the TicketEmailExternalUserCc DAO
 */
public class TicketEmailExternalUserCcDAO implements ITicketEmailExternalUserCcDAO
{
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_cc ) FROM workflow_task_ticketing_email_external_user_cc";
    private static final String SQL_QUERY_FIND_BY_ID_HISTORY = " SELECT  id_cc, id_task, id_history, email FROM workflow_task_ticketing_email_external_user_cc "
            + " WHERE id_history = ? AND id_task = ? ORDER BY id_cc ASC";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_email_external_user_cc ( id_cc, id_task, id_history, email ) "
            + " VALUES ( ?,?,?,? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_ticketing_email_external_user_cc WHERE id_cc = ?";
    private static final String SQL_QUERY_DELETE_BY_HISTORY = " DELETE FROM workflow_task_ticketing_email_external_user_cc WHERE id_history = ?";

    /**
     * Generates a new primary key
     *
     * @return The new primary key
     */
    public int newPrimaryKey( )
    {
        int nKey = 1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }
        }
        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public synchronized void insert( TicketEmailExternalUserCc infosEmailExternalUser )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            infosEmailExternalUser.setIdInfosHistory( newPrimaryKey( ) );

            int nIndex = 1;

            daoUtil.setInt( nIndex++, infosEmailExternalUser.getIdInfosHistory( ) );
            daoUtil.setInt( nIndex++, infosEmailExternalUser.getIdTask( ) );
            daoUtil.setInt( nIndex++, infosEmailExternalUser.getIdResourceHistory( ) );
            daoUtil.setString( nIndex++, infosEmailExternalUser.getEmail( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TicketEmailExternalUserCc> loadByIdHistory( int nIdHistory, int nIdTask )
    {
        TicketEmailExternalUserCc infosEmailExternalUser;
        List<TicketEmailExternalUserCc> listInfosEmailExternalUser = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_HISTORY, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.setInt( 2, nIdTask );

            daoUtil.executeQuery( );

            int nIndex = 1;


            while ( daoUtil.next( ) )
            {
                nIndex = 1;
                infosEmailExternalUser = new TicketEmailExternalUserCc( );
                infosEmailExternalUser.setIdInfosHistory( daoUtil.getInt( nIndex++ ) );
                infosEmailExternalUser.setIdTask( daoUtil.getInt( nIndex++ ) );
                infosEmailExternalUser.setIdResourceHistory( daoUtil.getInt( nIndex++ ) );
                infosEmailExternalUser.setEmail( daoUtil.getString( nIndex++ ) );

                listInfosEmailExternalUser.add( infosEmailExternalUser );
            }
        }
        return listInfosEmailExternalUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void deleteByIdCc( int nIdCc )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdCc );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void deleteByIdHistory( int nIdHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.executeUpdate( );
        }
    }
}
