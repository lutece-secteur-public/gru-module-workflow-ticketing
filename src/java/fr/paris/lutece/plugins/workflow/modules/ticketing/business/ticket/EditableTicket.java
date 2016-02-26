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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket;

import java.util.List;


/**
 *
 * This class represents an editable ticket
 *
 */
public class EditableTicket
{
    private int _nIdHistory;
    private int _nIdTask;
    private int _nIdTicket;
    private String _strMessage;
    private boolean _bIsEdited;
    private List<EditableTicketField> _listEditableTicketFields;

    /**
     * Set the id editable ticket
     * @param nIdHistory the id of the editable ticket
     */
    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    /**
     * Get the id of the editable ticket
     * @return the id of the editable ticket
     */
    public int getIdHistory(  )
    {
        return _nIdHistory;
    }

    /**
     * Get the id task
     * @return the task id
     */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
     * Set the task id
     * @param nIdTask the task id
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * Get the id task
     * @return the ticket id
     */
    public int getIdTicket(  )
    {
        return _nIdTicket;
    }

    /**
     * Set the ticket id
     * @param nIdTicket the ticket id
     */
    public void setIdTicket( int nIdTicket )
    {
        _nIdTicket = nIdTicket;
    }

    /**
     * Set the message
     * @param strMessage the message
     */
    public void setMessage( String strMessage )
    {
        _strMessage = strMessage;
    }

    /**
     * Get the message
     * @return the message
     */
    public String getMessage(  )
    {
        return _strMessage;
    }

    /**
     * Set the list of editable ticket field
     * @param listEditableTicketField of editable ticket field
     */
    public void setListEditableTicketFields( List<EditableTicketField> listEditableTicketField )
    {
        _listEditableTicketFields = listEditableTicketField;
    }

    /**
     * Get the list of editable ticket field
     * @return the list of editable ticket field
     */
    public List<EditableTicketField> getListEditableTicketFields(  )
    {
        return _listEditableTicketFields;
    }

    /**
     * Set {@code true} if the ticket is edited, {@code false} otherwise
     * @param bIsEdited {@code true} if the ticket is edited, {@code false} otherwise
     */
    public void setIsEdited( boolean bIsEdited )
    {
        _bIsEdited = bIsEdited;
    }

    /**
     * Check if the ticket is edited
     * @return {@code true} if the ticket is edited, {@code false} otherwise
     */
    public boolean isEdited(  )
    {
        return _bIsEdited;
    }
}
