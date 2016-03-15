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

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCriticality;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketPriority;
import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents a task to qualify the ticket
 *
 */
public class TaskQualifyTicket extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_QUALIFY_TICKET = "module.workflow.ticketing.task_qualify_ticket.labelQualifyTicket";
    private static final String MESSAGE_QUALIFY_TICKET_INFORMATION_PRIORITY = "module.workflow.ticketing.task_qualify_ticket.information.priority";
    private static final String MESSAGE_QUALIFY_TICKET_INFORMATION_CRITICALITY = "module.workflow.ticketing.task_qualify_ticket.information.criticality";

    // PARAMETERS
    public static final String PARAMETER_TICKET_PRIORITY = "ticket_priority";
    public static final String PARAMETER_TICKET_CRITICALITY = "ticket_criticality";

    // Other constants
    private static final String NEW_LINE = "<br/>";

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        StringBuilder sb = new StringBuilder(  );

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            boolean bPriorityChanged = false;

            String strPriority = request.getParameter( PARAMETER_TICKET_PRIORITY );
            int nPriority = Integer.parseInt( strPriority );
            TicketPriority priorityBefore = TicketPriority.valueOf( ticket.getPriority(  ) );
            TicketPriority priorityAfter = TicketPriority.valueOf( nPriority );
            ticket.setPriority( nPriority );

            String strCriticality = request.getParameter( PARAMETER_TICKET_CRITICALITY );
            int nCriticality = Integer.parseInt( strCriticality );
            TicketCriticality criticalityBefore = TicketCriticality.valueOf( ticket.getCriticality(  ) );
            TicketCriticality criticalityAfter = TicketCriticality.valueOf( nCriticality );
            ticket.setCriticality( nCriticality );

            TicketHome.update( ticket );

            if ( priorityBefore != priorityAfter )
            {
                sb.append( MessageFormat.format( I18nService.getLocalizedString( 
                            MESSAGE_QUALIFY_TICKET_INFORMATION_PRIORITY, Locale.FRENCH ),
                        priorityBefore.getLocalizedMessage( Locale.FRENCH ),
                        priorityAfter.getLocalizedMessage( Locale.FRENCH ) ) );

                bPriorityChanged = true;
            }

            if ( criticalityBefore != criticalityAfter )
            {
                if ( bPriorityChanged )
                {
                    sb.append( NEW_LINE );
                }

                sb.append( MessageFormat.format( I18nService.getLocalizedString( 
                            MESSAGE_QUALIFY_TICKET_INFORMATION_CRITICALITY, Locale.FRENCH ),
                        criticalityBefore.getLocalizedMessage( Locale.FRENCH ),
                        criticalityAfter.getLocalizedMessage( Locale.FRENCH ) ) );
            }

            strTaskInformation = sb.toString(  );
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_QUALIFY_TICKET, locale );
    }
}
