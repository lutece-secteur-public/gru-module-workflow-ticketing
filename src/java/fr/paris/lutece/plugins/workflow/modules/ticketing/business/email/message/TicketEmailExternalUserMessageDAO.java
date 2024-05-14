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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implementation of the TicketEmailExternalUserMessage DAO
 */
public class TicketEmailExternalUserMessageDAO implements ITicketEmailExternalUserMessageDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_message_external_user ) FROM workflow_ticketing_email_external_user ";
    private static final String SQL_QUERY_LAST_QUESTION = " SELECT max( id_message_external_user ) FROM workflow_ticketing_email_external_user WHERE id_ticket = ? AND is_answered = 0";
    private static final String SQL_QUERY_SELECT = " SELECT id_message_external_user, id_ticket, email_recipients, email_recipients_cc, message_question, message_response, is_answered, email_subject FROM workflow_ticketing_email_external_user ";
    private static final String SQL_QUERY_FIND_BY_ID_MESSAGE = SQL_QUERY_SELECT + " WHERE id_message_external_user = ? ";
    private static final String SQL_QUERY_INSERT_QUESTION = " INSERT INTO workflow_ticketing_email_external_user ( id_message_external_user, id_ticket, email_recipients, email_recipients_cc, message_question, email_subject ) "
            + " VALUES ( ?,?,?,?,?,? ) ";
    private static final String SQL_QUERY_ADD_ANSWER = " UPDATE workflow_ticketing_email_external_user SET message_response = ?, is_answered = 1 WHERE id_message_external_user = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_ticketing_email_external_user WHERE id_message_external_user = ? ";
    private static final String SQL_QUERY_FIND_BY_ID_TICKET_NOT_CLOSED = SQL_QUERY_SELECT
            + " WHERE id_ticket = ? AND is_answered = 0 ORDER BY id_message_external_user ASC ";
    private static final String SQL_QUERY_CLOSE_BY_ID_TICKET = " UPDATE workflow_ticketing_email_external_user SET is_answered = 1 WHERE id_ticket = ? ";
    private static final String SQL_QUERY_FIRST_MESSAGE = " SELECT min(id_message_external_user), id_ticket, email_recipients, email_recipients_cc, message_question, message_response, is_answered, email_subject FROM workflow_ticketing_email_external_user "
            + " WHERE id_ticket = ? AND is_answered = 0 ";
    private static final String SQL_QUERY_LAST_MESSAGE = SQL_QUERY_SELECT + " WHERE id_ticket = ? ORDER BY id_message_external_user DESC LIMIT 1";
    private static final String SQL_QUERY_ALL_MESSAGE_ORDER_DESC = SQL_QUERY_SELECT + " WHERE id_ticket = ? ORDER BY id_message_external_user DESC";
    private static final String SQL_QUERY_SELECT_ID_MESSAGE_EXTERNAL_USER = "SELECT id_message_external_user FROM workflow_ticketing_email_external_user wteeu WHERE id_ticket = ?";
    private static final String SQL_QUERY_SELECT_MESSAGE_EXTERNAL_USER = "SELECT message_question, message_response FROM workflow_ticketing_email_external_user wteeu WHERE id_message_external_user = ?";
    private static final String SQL_QUERY_UPDATE_MESSAGE = "UPDATE workflow_ticketing_email_external_user SET email_recipients = NULL, email_recipients_cc = NULL, message_question = ?, message_response = ? WHERE id_message_external_user = ?";

    /**
     * Generates a new primary key
     *
     * @return The new primary key
     */
    private int nextPrimaryKey( )
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
     * Retrieve the last demande with no response for a given ticket id
     *
     * @param nIdTicket
     *            the ticket id
     * @return the id of TicketingEmailExternalUserMessage
     */
    private int getLastQuestion( int nIdTicket )
    {
        int nKey = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LAST_QUESTION, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTicket );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                // if the table is not empty
                nKey = daoUtil.getInt( 1 );
            }
        }

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public synchronized void createQuestion( TicketEmailExternalUserMessage emailExternalUserMessage )
    {
        emailExternalUserMessage.setIdMessageExternalUser( nextPrimaryKey( ) );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_QUESTION, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, emailExternalUserMessage.getIdMessageExternalUser( ) );
            daoUtil.setInt( nIndex++, emailExternalUserMessage.getIdTicket( ) );
            daoUtil.setString( nIndex++, emailExternalUserMessage.getEmailRecipients( ) );
            daoUtil.setString( nIndex++, emailExternalUserMessage.getEmailRecipientsCc( ) );
            daoUtil.setString( nIndex++, emailExternalUserMessage.getMessageQuestion( ) );
            daoUtil.setString( nIndex++, emailExternalUserMessage.getEmailSubject( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLastQuestion( int nIdTicket, int nIdMessageAgent )
    {
        int nLastIdMessageAgent = getLastQuestion( nIdTicket );

        return ( ( nLastIdMessageAgent > 0 ) && ( nIdMessageAgent == nLastIdMessageAgent ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public synchronized int addAnswer( int nIdTicket, String strReponse )
    {
        int nIdMessageAgent = getLastQuestion( nIdTicket );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ADD_ANSWER, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, strReponse );
            daoUtil.setInt( nIndex++, nIdMessageAgent );

            daoUtil.executeUpdate( );
        }
        return nIdMessageAgent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void closeMessagesByIdTicket( int nIdTicket )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CLOSE_BY_ID_TICKET, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTicket );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketEmailExternalUserMessage loadByIdMessageExternalUser( int nIdMessageExternalUser )
    {
        TicketEmailExternalUserMessage emailExternalUserMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_MESSAGE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdMessageExternalUser );

            daoUtil.executeQuery( );

            int nIndex = 1;

            if ( daoUtil.next( ) )
            {
                emailExternalUserMessage = new TicketEmailExternalUserMessage( );
                emailExternalUserMessage.setIdMessageExternalUser( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setIdTicket( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipients( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipientsCc( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageQuestion( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageResponse( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setIsAnswered( daoUtil.getBoolean( nIndex++ ) );
                emailExternalUserMessage.setEmailSubject( daoUtil.getString( nIndex++ ) );
            }
        }
        return emailExternalUserMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketEmailExternalUserMessage loadFirstByIdTicket( int nIdTicket )
    {
        TicketEmailExternalUserMessage emailExternalUserMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIRST_MESSAGE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTicket );

            daoUtil.executeQuery( );

            int nIndex = 1;

            if ( daoUtil.next( ) )
            {
                emailExternalUserMessage = new TicketEmailExternalUserMessage( );
                emailExternalUserMessage.setIdMessageExternalUser( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setIdTicket( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipients( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipientsCc( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageQuestion( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageResponse( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setIsAnswered( daoUtil.getBoolean( nIndex++ ) );
                emailExternalUserMessage.setEmailSubject( daoUtil.getString( nIndex++ ) );
            }
        }
        return emailExternalUserMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketEmailExternalUserMessage loadLastByIdTicket( int nIdTicket )
    {
        TicketEmailExternalUserMessage emailExternalUserMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LAST_MESSAGE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTicket );

            daoUtil.executeQuery( );

            int nIndex = 1;

            if ( daoUtil.next( ) )
            {
                emailExternalUserMessage = new TicketEmailExternalUserMessage( );
                emailExternalUserMessage.setIdMessageExternalUser( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setIdTicket( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipients( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipientsCc( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageQuestion( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageResponse( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setIsAnswered( daoUtil.getBoolean( nIndex++ ) );
                emailExternalUserMessage.setEmailSubject( daoUtil.getString( nIndex++ ) );
            }
        }
        return emailExternalUserMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TicketEmailExternalUserMessage> loadByIdTicketNotClosed( int nIdTicket )
    {
        List<TicketEmailExternalUserMessage> listEmailExternalUserMessage = new ArrayList<>( );
        TicketEmailExternalUserMessage emailExternalUserMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_TICKET_NOT_CLOSED, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTicket );

            daoUtil.executeQuery( );

            int nIndex = 1;

            while ( daoUtil.next( ) )
            {
                nIndex = 1;
                emailExternalUserMessage = new TicketEmailExternalUserMessage( );
                emailExternalUserMessage.setIdMessageExternalUser( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setIdTicket( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipients( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipientsCc( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageQuestion( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageResponse( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setIsAnswered( daoUtil.getBoolean( nIndex++ ) );
                emailExternalUserMessage.setEmailSubject( daoUtil.getString( nIndex++ ) );
                listEmailExternalUserMessage.add( emailExternalUserMessage );
            }

        }

        return listEmailExternalUserMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TicketEmailExternalUserMessage> loadByIdTicketNotClosedOrderDesc( int nIdTicket )
    {
        List<TicketEmailExternalUserMessage> listEmailExternalUserMessage = new ArrayList<>( );
        TicketEmailExternalUserMessage emailExternalUserMessage;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ALL_MESSAGE_ORDER_DESC, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTicket );

            daoUtil.executeQuery( );

            int nIndex;

            while ( daoUtil.next( ) )
            {
                nIndex = 1;
                emailExternalUserMessage = new TicketEmailExternalUserMessage( );
                emailExternalUserMessage.setIdMessageExternalUser( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setIdTicket( daoUtil.getInt( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipients( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setEmailRecipientsCc( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageQuestion( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setMessageResponse( daoUtil.getString( nIndex++ ) );
                emailExternalUserMessage.setIsAnswered( daoUtil.getBoolean( nIndex++ ) );
                emailExternalUserMessage.setEmailSubject( daoUtil.getString( nIndex ) );
                listEmailExternalUserMessage.add( emailExternalUserMessage );
            }
        }
        return listEmailExternalUserMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void deleteByIdMessageExternalUser( int nIdMessageExternalUser )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdMessageExternalUser );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void update( Map<String, String> data, int id, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_MESSAGE, plugin ) )
        {
            int nIndex = 1;
            for ( Entry<String, String> entry : data.entrySet( ) )
            {
                daoUtil.setString( nIndex, entry.getValue( ) );
                nIndex++;
            }
            daoUtil.setInt( nIndex, id );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public Map<String, String> getHistoryEmailToAnonymize( int idMessage, Plugin plugin )
    {
        Map<String, String> map = new HashMap<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MESSAGE_EXTERNAL_USER, plugin ) )
        {
            daoUtil.setInt( 1, idMessage );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                map.put( "message_question", daoUtil.getString( 1 ) );
                map.put( "message_reponse", daoUtil.getString( 2 ) );
            }
        }
        return map;
    }

    @Override
    public List<Integer> getListIDMessageExternalUser( int idTicket, Plugin plugin )
    {
        List<Integer> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_MESSAGE_EXTERNAL_USER, plugin ) )
        {
            daoUtil.setInt( 1, idTicket );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.add( daoUtil.getInt( 1 ) );
            }
        }
        return list;
    }
}
