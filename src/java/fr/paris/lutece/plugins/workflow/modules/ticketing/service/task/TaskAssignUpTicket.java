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

import fr.paris.lutece.plugins.ticketing.business.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents a task to assign up the ticket
 *
 */
public class TaskAssignUpTicket extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_ASSIGN_UP_TICKET = "module.workflow.ticketing.task_assign_up_ticket.labelAssignUpTicket";
    private static final String MESSAGE_ASSIGN_UP_TICKET_INFORMATION = "module.workflow.ticketing.task_assign_up_ticket.information";
    private static final String MESSAGE_ASSIGN_UP_TICKET_UNKNOWN_FORMER_USER = "module.workflow.ticketing.task_assign_up_ticket.unknownFormerUser";

    // PARAMETERS
    public static final String PARAMETER_TICKET_UP_ASSIGNEE_UNIT_ID = "ticket_up_unit_id";

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ASSIGN_UP_TICKET, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        String strTargetUnitId = request.getParameter( PARAMETER_TICKET_UP_ASSIGNEE_UNIT_ID );

        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

        if ( ( resourceHistory != null ) && Ticket.TICKET_RESOURCE_TYPE.equals( resourceHistory.getResourceType(  ) ) )
        {
            // We get the ticket to modify
            Ticket ticket = TicketHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );

            if ( ticket != null )
            {
                AssigneeUnit assigneeUnit = ticket.getAssigneeUnit(  );
                Unit unit = UnitHome.findByPrimaryKey( Integer.parseInt( strTargetUnitId ) );

                if ( unit != null )
                {
                    AssigneeUser assigneeUser = ticket.getAssigneeUser(  );
                    assigneeUnit.setUnitId( unit.getIdUnit(  ) );
                    assigneeUnit.setName( unit.getLabel(  ) );
                    ticket.setAssigneeUnit( assigneeUnit );
                    ticket.setAssigneeUser( null );
                    TicketHome.update( ticket );

                    String strFormerUserInfos = ( assigneeUser == null )
                        ? I18nService.getLocalizedString( MESSAGE_ASSIGN_UP_TICKET_UNKNOWN_FORMER_USER, Locale.FRENCH )
                        : ( assigneeUser.getFirstname(  ) + " " + assigneeUser.getLastname(  ) );
                    strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( 
                                MESSAGE_ASSIGN_UP_TICKET_INFORMATION, Locale.FRENCH ), strFormerUserInfos,
                            assigneeUnit.getName(  ) );
                }
            }
        }

        return strTaskInformation;
    }
}
