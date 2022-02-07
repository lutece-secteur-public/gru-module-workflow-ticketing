/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config;

import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

/**
 * Configuration for the task email to external user
 */
public class TaskTicketEmailExternalUserConfig extends TaskConfig
{
    @NotNull
    private MessageDirectionExternalUser _messageDirectionExternalUser;

    private Integer _nIdFollowingAction;

    private Integer _nIdContactAttribute;

    private String _strDefaultSubject;

    /**
     * Gives the message direction
     *
     * @return the message direction
     */
    public MessageDirectionExternalUser getMessageDirectionExternalUser( )
    {
        return _messageDirectionExternalUser;
    }

    /**
     * Sets the message direction
     *
     * @param messageDirectionExternalUser
     *            the message direction to set
     */
    public void setMessageDirectionExternalUser( MessageDirectionExternalUser messageDirectionExternalUser )
    {
        _messageDirectionExternalUser = messageDirectionExternalUser;
    }

    /**
     * @return the idFollowingAction
     */
    public Integer getIdFollowingAction( )
    {
        return _nIdFollowingAction;
    }

    /**
     * @param nIdFollowingAction
     *            the idFollowingAction to set
     */
    public void setIdFollowingAction( Integer nIdFollowingAction )
    {
        _nIdFollowingAction = nIdFollowingAction;
    }

    /**
     * @return true if the direction is agent to external user
     */
    public boolean isMessageToExternalUser( )
    {
        return MessageDirectionExternalUser.AGENT_TO_EXTERNAL_USER.equals( _messageDirectionExternalUser );
    }

    /**
     * @return the idContactAttribute
     */
    public Integer getIdContactAttribute( )
    {
        return _nIdContactAttribute;
    }

    /**
     * @param nIdContactAttribute
     *            Id Contact Attribute
     */
    public void setIdContactAttribute( Integer nIdContactAttribute )
    {
        _nIdContactAttribute = nIdContactAttribute;
    }

    public String getDefaultSubject( )
    {
        return _strDefaultSubject;
    }

    public void setDefaultSubject( String defaultSubject )
    {
        _strDefaultSubject = defaultSubject;
    }

}
