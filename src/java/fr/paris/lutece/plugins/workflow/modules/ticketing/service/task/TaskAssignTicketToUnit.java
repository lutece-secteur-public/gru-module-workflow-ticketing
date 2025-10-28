/*
 * Copyright (c) 2002-2025, City of Paris
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
 * This class represents a task to assign a unit
 *
 */
public class TaskAssignTicketToUnit extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_ASSIGN_TICKET_TO_UNIT = "module.workflow.ticketing.task_assign_ticket_to_unit.labelAssignTicketToUnit";
    private static final String MESSAGE_ASSIGN_TICKET_TO_UNIT_INFORMATION = "module.workflow.ticketing.task_assign_ticket_to_unit.information";
    private static final String MESSAGE_ASSIGN_TICKET_TO_UNIT_NO_CURRENT_UNIT = "module.workflow.ticketing.task_assign_ticket_to_unit.no_current_unit";

    // PARAMETERS
    public static final String PARAMETER_ASSIGNEE_UNIT = "id_unit";

    // Services
    @Inject
    protected IResourceHistoryService _resourceHistoryServiceTicketing;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        String strUnitId = request.getParameter( PARAMETER_ASSIGNEE_UNIT );

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            AssigneeUnit assigneeUnit = ticket.getAssigneeUnit( );
            String strCurrentUnit = null;

            if ( assigneeUnit == null )
            {
                assigneeUnit = new AssigneeUnit( );
                strCurrentUnit = I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_UNIT_NO_CURRENT_UNIT, Locale.FRENCH );
            }
            else
            {
                strCurrentUnit = assigneeUnit.getName( );
            }

            Unit unit = null;

            if ( strUnitId != null )
            {
                unit = UnitHome.findByPrimaryKey( Integer.parseInt( strUnitId ) );
            }

            if ( ( unit != null ) && ( unit.getIdUnit( ) != assigneeUnit.getUnitId( ) ) )
            {
                int oldUnit = assigneeUnit.getUnitId( );
                assigneeUnit.setUnitId( unit.getIdUnit( ) );
                assigneeUnit.setName( unit.getLabel( ) );

                TicketHome.updateAssignAll( null, assigneeUnit, null, null, ticket.getId( ) );

                request.setAttribute( TicketingConstants.ATTRIBUTE_IS_UNIT_CHANGED, true );
                request.setAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION, REDIRECT_TO_LIST );

                strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_UNIT_INFORMATION, Locale.FRENCH ),
                        strCurrentUnit, assigneeUnit.getName( ) );

                // insert in workflow_resource_history_ticketing
                ResourceHistory resourceHistory = new ResourceHistory( );
                resourceHistory.setIdHistory( nIdResourceHistory );
                resourceHistory.setIdChannel( ticket.getChannel( ).getId( ) );
                resourceHistory.setIdUnitOld( oldUnit );
                resourceHistory.setIdUnitNew( unit.getIdUnit( ) );
                _resourceHistoryServiceTicketing.create( resourceHistory, WorkflowUtils.getPlugin( ) );
            }

        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_UNIT, locale );
    }
}
