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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicket;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicketField;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.IEditableTicketDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.WorkflowTicketingUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;

/**
 *
 * This class provides methods to manage {@link fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicket}
 *
 */
public class EditableTicketService implements IEditableTicketService
{
    public static final String BEAN_NAME = "workflow-ticketing.editableTicketService";

    // SERVICES
    @Inject
    private IEditableTicketFieldService _editableTicketFieldService;
    @Inject
    private ITaskService _taskService;
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    @Inject
    private IActionService _actionService;

    // DAO
    @Inject
    private IEditableTicketDAO _editableTicketDAO;

    // CRUD

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    public void create( EditableTicket editableTicket )
    {
        if ( editableTicket != null )
        {
            _editableTicketDAO.insert( editableTicket );

            for ( EditableTicketField editableTicketField : editableTicket.getListEditableTicketFields( ) )
            {
                editableTicketField.setIdHistory( editableTicket.getIdHistory( ) );
                _editableTicketFieldService.create( editableTicketField );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    public void update( EditableTicket editableTicket )
    {
        if ( editableTicket != null )
        {
            _editableTicketDAO.store( editableTicket );
            // Remove its editable ticket fields first
            _editableTicketFieldService.remove( editableTicket.getIdHistory( ) );

            for ( EditableTicketField editableTicketField : editableTicket.getListEditableTicketFields( ) )
            {
                editableTicketField.setIdHistory( editableTicket.getIdHistory( ) );
                _editableTicketFieldService.create( editableTicketField );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditableTicket find( int nIdHistory, int nIdTask )
    {
        EditableTicket editableTicket = _editableTicketDAO.load( nIdHistory, nIdTask );

        if ( editableTicket != null )
        {
            editableTicket.setListEditableTicketFields( _editableTicketFieldService.find( editableTicket.getIdHistory( ) ) );
        }

        return editableTicket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditableTicket findByIdTicket( int nIdTicket )
    {
        EditableTicket editableTicket = _editableTicketDAO.loadByIdTicket( nIdTicket );

        if ( editableTicket != null )
        {
            editableTicket.setListEditableTicketFields( _editableTicketFieldService.find( editableTicket.getIdHistory( ) ) );
        }

        return editableTicket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EditableTicket> findByIdTask( int nIdTask )
    {
        return _editableTicketDAO.loadByIdTask( nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    public void removeByIdHistory( int nIdHistory, int nIdTask )
    {
        EditableTicket editableTicket = find( nIdHistory, nIdTask );

        if ( editableTicket != null )
        {
            _editableTicketFieldService.remove( editableTicket.getIdHistory( ) );
            _editableTicketDAO.deleteByIdHistory( nIdHistory, nIdTask );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    public void removeByIdTask( int nIdTask )
    {
        for ( EditableTicket editableTicket : findByIdTask( nIdTask ) )
        {
            _editableTicketFieldService.remove( editableTicket.getIdHistory( ) );
        }

        _editableTicketDAO.deleteByIdTask( nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Entry> buildListEntriesToEdit( HttpServletRequest request, List<EditableTicketField> listEditableTicketFields )
    {
        List<Entry> listEntries = new ArrayList<>( );

        for ( EditableTicketField editableTicketField : listEditableTicketFields )
        {
            Entry entry = EntryHome.findByPrimaryKey( editableTicketField.getIdEntry( ) );

            if ( !entry.getEntryType( ).getComment( ) )
            {
                listEntries.add( entry );
            }
        }

        return listEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> buildListIdEntriesToEdit( HttpServletRequest request, List<EditableTicketField> listEditableTicketFields )
    {
        List<Integer> listIdEntries = new ArrayList<>( );

        for ( EditableTicketField editableTicketField : listEditableTicketFields )
        {
            Entry entry = EntryHome.findByPrimaryKey( editableTicketField.getIdEntry( ) );

            if ( !entry.getEntryType( ).getComment( ) )
            {
                listIdEntries.add( entry.getIdEntry( ) );
            }
        }

        return listIdEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStateValid( EditableTicket editableTicket, Locale locale )
    {
        boolean bIsValid = false;

        ITask task = _taskService.findByPrimaryKey( editableTicket.getIdTask( ), locale );

        if ( task != null )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( ( action != null ) && ( action.getStateAfter( ) != null ) )
            {
                Ticket ticket = WorkflowTicketingUtils.findTicketByIdHistory( editableTicket.getIdHistory( ) );

                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE,
                        action.getWorkflow( ).getId( ) );

                if ( ( resourceWorkflow != null ) && ( resourceWorkflow.getState( ) != null )
                        && ( resourceWorkflow.getState( ).getId( ) == action.getStateAfter( ).getId( ) ) )
                {
                    bIsValid = true;
                }
            }
        }

        return bIsValid;
    }
}
