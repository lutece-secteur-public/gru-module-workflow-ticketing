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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskMarkAsUnreadConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class represents a task to mark a ticket as unread
 *
 */
public class TaskMarkAsUnread extends AbstractTicketingTask
{
    private static final String MESSAGE_TASK_TITLE = "module.workflow.ticketing.task_mark_as_unread.title";

    private static final String BEAN_MARK_AS_UNREAD_CONFIG_SERVICE = "workflow-ticketing.taskMarkAsUnreadConfigService";

    @Inject
    @Named( BEAN_MARK_AS_UNREAD_CONFIG_SERVICE )
    private ITaskConfigService _taskConfigService;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );
        TaskMarkAsUnreadConfig config = _taskConfigService.findByPrimaryKey( this.getId( ) );

        if ( ticket != null && config != null )
        {
            TicketHome.setTicketMarkingId( ticket.getId( ), config.getIdMarking( ) );
        }

        // no information stored in the history
        return null;
    }
}
