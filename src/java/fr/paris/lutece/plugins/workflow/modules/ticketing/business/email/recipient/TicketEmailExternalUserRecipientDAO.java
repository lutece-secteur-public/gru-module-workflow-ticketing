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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.recipient;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implementation of the TicketEmailExternalUserRecipient DAO
 */
public class TicketEmailExternalUserRecipientDAO implements ITicketEmailExternalUserRecipientDAO
{
    private static final String IDS_TO_REPLACE                   = "%IDS%";
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_recipient ) FROM ticket_email_external_user_recipient";
    private static final String SQL_QUERY_FIND_BY_ID_HISTORY = " SELECT  id_recipient, id_task, id_history, email, field, name, firstname FROM ticket_email_external_user_recipient "
            + " WHERE id_history = ? AND id_task = ? ORDER BY id_recipient ASC";
    private static final String SQL_QUERY_INSERT = " INSERT INTO ticket_email_external_user_recipient ( id_recipient, id_task, id_history, email, field, name, firstname  ) "
            + " VALUES ( ?,?,?,?,?,?,? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM ticket_email_external_user_recipient WHERE id_recipient = ?";
    private static final String SQL_QUERY_DELETE_BY_HISTORY = " DELETE FROM ticket_email_external_user_recipient WHERE id_history = ?";
    private static final String SQL_QUERY_DELETE_BY_HISTORY_LIST = " DELETE FROM ticket_email_external_user_recipient WHERE id_history IN (" + IDS_TO_REPLACE + ")";

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
    public synchronized void insert( TicketEmailExternalUserRecipient infosEmailExternalUser )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            infosEmailExternalUser.setIdRecipient( newPrimaryKey( ) );

            int nIndex = 1;

            daoUtil.setInt( nIndex++, infosEmailExternalUser.getIdRecipient( ) );
            daoUtil.setInt( nIndex++, infosEmailExternalUser.getIdTask( ) );
            daoUtil.setInt( nIndex++, infosEmailExternalUser.getIdResourceHistory( ) );
            daoUtil.setString( nIndex++, infosEmailExternalUser.getEmail( ) );
            daoUtil.setString( nIndex++, infosEmailExternalUser.getField( ) );
            daoUtil.setString( nIndex++, infosEmailExternalUser.getName( ) );
            daoUtil.setString( nIndex++, infosEmailExternalUser.getFirstName( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TicketEmailExternalUserRecipient> loadByIdHistory( int nIdHistory, int nIdTask )
    {
        TicketEmailExternalUserRecipient infosEmailExternalUser;
        List<TicketEmailExternalUserRecipient> listInfosEmailExternalUser = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_HISTORY, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.setInt( 2, nIdTask );

            daoUtil.executeQuery( );

            int nIndex = 1;

            while ( daoUtil.next( ) )
            {
                nIndex = 1;
                infosEmailExternalUser = new TicketEmailExternalUserRecipient( );
                infosEmailExternalUser.setIdRecipient( daoUtil.getInt( nIndex++ ) );
                infosEmailExternalUser.setIdTask( daoUtil.getInt( nIndex++ ) );
                infosEmailExternalUser.setIdResourceHistory( daoUtil.getInt( nIndex++ ) );
                infosEmailExternalUser.setEmail( daoUtil.getString( nIndex++ ) );
                infosEmailExternalUser.setField( daoUtil.getString( nIndex++ ) );
                infosEmailExternalUser.setName( daoUtil.getString( nIndex++ ) );
                infosEmailExternalUser.setFirstName( daoUtil.getString( nIndex++ ) );

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
    public void deleteByIdRecipient( int nIdRecipient )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdRecipient );
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
