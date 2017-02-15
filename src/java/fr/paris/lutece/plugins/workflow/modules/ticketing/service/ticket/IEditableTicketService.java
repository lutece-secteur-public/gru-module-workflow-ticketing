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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.ticket;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicket;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicketField;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * This class provides methods to manage {@link fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicket}
 *
 */
public interface IEditableTicketService
{
    // CRUD

    /**
     * Create an editable ticket
     * 
     * @param editableTicket
     *            the editable ticket
     */
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    void create( EditableTicket editableTicket );

    /**
     * Update an editable ticket
     * 
     * @param editableTicket
     *            the editable ticket
     */
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    void update( EditableTicket editableTicket );

    /**
     * Find an editable ticket
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the id task
     * @return the editable ticket
     */
    EditableTicket find( int nIdHistory, int nIdTask );

    /**
     * Get the non edited editable ticket from a given id ticket
     * 
     * @param nIdTicket
     *            the id ticket
     * @return the editable ticket
     */
    EditableTicket findByIdTicket( int nIdTicket );

    /**
     * Find editable tickets by a given id task
     * 
     * @param nIdTask
     *            the id task
     * @return the list of editable tickets
     */
    List<EditableTicket> findByIdTask( int nIdTask );

    /**
     * Remove an editable ticket
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the id task
     */
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    void removeByIdHistory( int nIdHistory, int nIdTask );

    /**
     * Remove an editable ticket by id task
     * 
     * @param nIdTask
     *            the id task
     */
    @Transactional( WorkflowTicketingPlugin.BEAN_TRANSACTION_MANAGER )
    void removeByIdTask( int nIdTask );

    /**
     * Get the list of entries to edit
     * 
     * @param request
     *            the HTTP request
     * @param listEditableTicketFields
     *            the list of editable ticket fields
     * @return a list of entries
     */
    List<Entry> buildListEntriesToEdit( HttpServletRequest request, List<EditableTicketField> listEditableTicketFields );

    /**
     * Get the id list of entries to edit
     * 
     * @param request
     *            the HTTP request
     * @param listIdEditableTicketFields
     *            the id list of editable ticket fields
     * @return a list of id entries
     */
    List<Integer> buildListIdEntriesToEdit( HttpServletRequest request, List<EditableTicketField> listEditableTicketFields );

    /**
     * Check if the ticket has the same state before executing the action
     * 
     * @param editableTicket
     *            the editable ticket
     * @param locale
     *            the locale
     * @return {@code true} if the ticket has a valid state, {@code false} otherwise
     */
    boolean isStateValid( EditableTicket editableTicket, Locale locale );
}
