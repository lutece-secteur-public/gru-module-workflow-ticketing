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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 * Implementation of {@link IEditableTicketDAO}
 *
 */
public class EditableTicketDAO implements IEditableTicketDAO
{
    private static final String IDS_TO_REPLACE                 = "%IDS%";
    private static final String SQL_SELECT = " SELECT id_history, id_task, id_ticket, message, is_edited ";
    private static final String SQL_QUERY_SELECT = SQL_SELECT + " FROM workflow_task_ticketing_editable_ticket WHERE id_history = ? AND id_task = ? ";
    private static final String SQL_QUERY_SELECT_BY_ID_TASK = SQL_SELECT + " FROM workflow_task_ticketing_editable_ticket WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_editable_ticket ( id_history, id_task, id_ticket, message, is_edited ) "
            + " VALUES ( ?,?,?,?,? ) ";
    private static final String SQL_QUERY_SELECT_BY_ID_TICKET = SQL_SELECT
            + " FROM workflow_task_ticketing_editable_ticket WHERE id_ticket = ? AND is_edited = 0 ";
    private static final String SQL_QUERY_DELETE_BY_ID_HISTORY_LIST = " DELETE FROM workflow_task_ticketing_editable_ticket WHERE id_history IN (" + IDS_TO_REPLACE + ")";
    private static final String SQL_QUERY_DELETE_BY_TASK = " DELETE FROM workflow_task_ticketing_editable_ticket WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE workflow_task_ticketing_editable_ticket SET message = ?, is_edited = ? WHERE id_history = ? AND id_task = ? ";
    private static final String SQL_QUERY_SELECT_BY_ID_HISTORY      = " SELECT message FROM workflow_task_ticketing_editable_ticket WHERE id_history id_history = ? AND id_task = ?";
    private static final Plugin PLUGIN                              = PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME );

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( EditableTicket ediableTicket )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, PLUGIN ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, ediableTicket.getIdHistory( ) );
            daoUtil.setInt( nIndex++, ediableTicket.getIdTask( ) );
            daoUtil.setInt( nIndex++, ediableTicket.getIdTicket( ) );
            daoUtil.setString( nIndex++, ediableTicket.getMessage( ) );
            daoUtil.setBoolean( nIndex++, ediableTicket.isEdited( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( EditableTicket editableTicket )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, PLUGIN ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, editableTicket.getMessage( ) );
            daoUtil.setBoolean( nIndex++, editableTicket.isEdited( ) );

            daoUtil.setInt( nIndex++, editableTicket.getIdHistory( ) );
            daoUtil.setInt( nIndex++, editableTicket.getIdTask( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditableTicket load( int nIdHistory, int nIdTask )
    {
        EditableTicket editableTicket = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, PLUGIN ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdHistory );
            daoUtil.setInt( nIndex++, nIdTask );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nIndex = 1;

                editableTicket = new EditableTicket( );
                editableTicket.setIdHistory( daoUtil.getInt( nIndex++ ) );
                editableTicket.setIdTask( daoUtil.getInt( nIndex++ ) );
                editableTicket.setIdTicket( daoUtil.getInt( nIndex++ ) );
                editableTicket.setMessage( daoUtil.getString( nIndex++ ) );
                editableTicket.setIsEdited( daoUtil.getBoolean( nIndex++ ) );
            }
        }
        return editableTicket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditableTicket loadByIdTicket( int nIdTicket )
    {
        EditableTicket editableTicket = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_TICKET, PLUGIN ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdTicket );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nIndex = 1;

                editableTicket = new EditableTicket( );
                editableTicket.setIdHistory( daoUtil.getInt( nIndex++ ) );
                editableTicket.setIdTask( daoUtil.getInt( nIndex++ ) );
                editableTicket.setIdTicket( daoUtil.getInt( nIndex++ ) );
                editableTicket.setMessage( daoUtil.getString( nIndex++ ) );
                editableTicket.setIsEdited( daoUtil.getBoolean( nIndex++ ) );
            }
        }
        return editableTicket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String loadByIdHistory( int idHistory )
    {
        String message = "";
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_HISTORY, PLUGIN ) )
        {
            daoUtil.setInt( 1, idHistory );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                message = daoUtil.getString( 1 );
            }
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EditableTicket> loadByIdTask( int nIdTask )
    {
        List<EditableTicket> listEditableTickets = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_TASK, PLUGIN ) )
        {
            daoUtil.setInt( 1, nIdTask );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nIndex = 1;

                EditableTicket editableTicket = new EditableTicket( );
                editableTicket.setIdHistory( daoUtil.getInt( nIndex++ ) );
                editableTicket.setIdTask( daoUtil.getInt( nIndex++ ) );
                editableTicket.setIdTicket( daoUtil.getInt( nIndex++ ) );
                editableTicket.setMessage( daoUtil.getString( nIndex++ ) );
                editableTicket.setIsEdited( daoUtil.getBoolean( nIndex++ ) );
                listEditableTickets.add( editableTicket );
            }
        }
        return listEditableTickets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdHistory( int nIdHistory, int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_HISTORY, PLUGIN ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdHistory );
            daoUtil.setInt( nIndex++, nIdTask );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdTask( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, PLUGIN ) )
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
    public void deleteByIdHistoryList( List<Integer> idHistoryList )
    {
        final String sql = StringUtils.replace( SQL_QUERY_DELETE_BY_ID_HISTORY_LIST, IDS_TO_REPLACE, StringUtils.join( idHistoryList, "," ) );
        try ( DAOUtil daoUtil = new DAOUtil( sql, PLUGIN ) )
        {
            daoUtil.executeUpdate( );
        }
    }
}
