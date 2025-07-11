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
 * Implementation of {@link IEditableTicketFieldDAO}
 *
 */
public class EditableTicketFieldDAO implements IEditableTicketFieldDAO
{
    private static final String IDS_TO_REPLACE   = "%IDS%";
    private static final String SQL_QUERY_SELECT = " SELECT id_history, id_entry "
            + " FROM workflow_task_ticketing_editable_ticket_field WHERE id_history = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_editable_ticket_field (id_history, id_entry ) " + " VALUES ( ?,? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_ticketing_editable_ticket_field WHERE id_history = ? ";
    private static final String SQL_QUERY_DELETE_BY_HISTORY_LIST = "DELETE FROM workflow_task_ticketing_editable_ticket_field WHERE id_history IN (" + IDS_TO_REPLACE + ")";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( EditableTicketField editableTicketField )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, editableTicketField.getIdHistory( ) );
            daoUtil.setInt( nIndex++, editableTicketField.getIdEntry( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EditableTicketField> load( int nIdHistory )
    {
        List<EditableTicketField> listEditableTicketFields = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nIndex = 1;

                EditableTicketField editableTicketField = new EditableTicketField( );
                editableTicketField.setIdHistory( daoUtil.getInt( nIndex++ ) );
                editableTicketField.setIdEntry( daoUtil.getInt( nIndex++ ) );

                listEditableTicketFields.add( editableTicketField );
            }
        }
        return listEditableTicketFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) ) )
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
