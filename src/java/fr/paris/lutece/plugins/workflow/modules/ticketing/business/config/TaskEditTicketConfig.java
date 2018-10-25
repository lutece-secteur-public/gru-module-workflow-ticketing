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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

/**
 *
 * This class represents a configuration for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskEditTicket}
 *
 */
public class TaskEditTicketConfig extends TaskConfig
{
    @NotNull
    @Min( 1 )
    private int              _nIdUserEditionAction;
    @NotNull
    @Min( 1 )
    private int              _nIdStateAfterEdition;
    private MessageDirection _messageDirection;

    /**
     * Gives the message direction
     * 
     * @return the message direction
     */
    public MessageDirection getMessageDirection( )
    {
        return _messageDirection;
    }

    /**
     * Sets the message direction
     * 
     * @param messageDirection
     *            the message direction to set
     */
    public void setMessageDirection( MessageDirection messageDirection )
    {
        this._messageDirection = messageDirection;
    }

    /**
     * Set the id of the user edition action
     * 
     * @param nIdUserEditionAction
     *            the id of the user edition action
     */
    public void setIdUserEditionAction( int nIdUserEditionAction )
    {
        _nIdUserEditionAction = nIdUserEditionAction;
    }

    /**
     * Get the id of the user edition action
     * 
     * @return the id of the user edition action
     */
    public int getIdUserEditionAction( )
    {
        return _nIdUserEditionAction;
    }
}
