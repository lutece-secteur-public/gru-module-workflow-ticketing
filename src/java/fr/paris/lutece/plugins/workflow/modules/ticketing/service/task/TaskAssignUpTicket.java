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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import java.text.MessageFormat;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.IResourceHistoryService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.ResourceHistory;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;

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

    // Services
    @Inject
    protected IResourceHistoryService _resourceHistoryServiceTicketing;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ASSIGN_UP_TICKET, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        String strTargetUnitId = request != null ? request.getParameter( PARAMETER_TICKET_UP_ASSIGNEE_UNIT_ID ) : "";

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            AssigneeUnit assigneeUnit = new AssigneeUnit( );
            Unit unit;
            if ( request == null )
            {
                unit = UnitHome.findByPrimaryKey( ticket.getAssigneeUnit( ).getUnitId( ) );
            }
            else
            {
                unit = UnitHome.findByPrimaryKey( Integer.parseInt( strTargetUnitId ) );
            }

            if ( unit != null )
            {
                if ( ( ticket.getAssigneeUnit( ).getUnitId( ) != unit.getIdUnit( ) ) && ( request != null ) )
                {
                    request.setAttribute( TicketingConstants.ATTRIBUTE_IS_UNIT_CHANGED, true );
                }

                int oldUnit = ticket.getAssigneeUnit( ).getUnitId( );
                if ( oldUnit == 0 )
                {
                    // cas id=0 Mairie de Paris
                    oldUnit = -100;
                }
                AssigneeUser assigneeUser = ticket.getAssigneeUser( );

                String strFormerUserInfos = ( assigneeUser == null )
                        ? I18nService.getLocalizedString( MESSAGE_ASSIGN_UP_TICKET_UNKNOWN_FORMER_USER, Locale.FRENCH )
                                : ( assigneeUser.getFirstname( ) + " " + assigneeUser.getLastname( ) );
                        strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_ASSIGN_UP_TICKET_INFORMATION, Locale.FRENCH ),
                                strFormerUserInfos, unit.getLabel( ) );

                        ticket.setAssignerUser( ticket.getAssigneeUser( ) );
                        ticket.setAssignerUnit( ticket.getAssigneeUnit( ) );
                        ticket.setAssigneeUser( null );
                        assigneeUnit.setUnitId( unit.getIdUnit( ) );
                        assigneeUnit.setName( unit.getLabel( ) );
                        ticket.setAssigneeUnit( assigneeUnit );
                        ticket.setAssigneeUser( null );

                        TicketHome.update( ticket );

                        // insert in workflow_resource_history_ticketing
                        ResourceHistory resourceHistory = new ResourceHistory( );
                        resourceHistory.setIdHistory( nIdResourceHistory );
                        resourceHistory.setIdChannel( ticket.getChannel( ).getId( ) );
                        resourceHistory.setIdUnitOld( oldUnit );
                        resourceHistory.setIdUnitNew( unit.getIdUnit( ) );
                        _resourceHistoryServiceTicketing.create( resourceHistory, WorkflowUtils.getPlugin( ) );
            }
        }


        if ( request != null )
        {
            request.setAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION, REDIRECT_TO_LIST );
        }
        return strTaskInformation;
    }
}
