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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.reference.ITicketReferenceService;
import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.Locale;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a task to generate the ticket reference
 *
 */
public class TaskGenerateTicketReference extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_GENERATE_TICKET_REFERENCE = "module.workflow.ticketing.task_generate_ticket_reference.labelGenerateTicketReference";
    private static final String MESSAGE_GENERATE_TICKET_REFERENCE_INFORMATION = "module.workflow.ticketing.task_generate_ticket_reference.information";

    // Services
    @Inject
    private ITicketReferenceService _ticketReferenceService;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strReference = StringUtils.EMPTY;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            synchronized( _ticketReferenceService )
            {
                strReference = _ticketReferenceService.generateReference( ticket );
                ticket.setReference( strReference );
                TicketHome.update( ticket );
            }
        }

        return MessageFormat.format( I18nService.getLocalizedString( MESSAGE_GENERATE_TICKET_REFERENCE_INFORMATION, Locale.FRENCH ), strReference );
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_GENERATE_TICKET_REFERENCE, locale );
    }
}
