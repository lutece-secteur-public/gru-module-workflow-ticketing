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

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.resourcehistory.IResourceWorkflowHistoryDAO;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketReassignUnitResourceService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexerActionUtil;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.IResourceHistoryService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.ResourceHistory;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class represents a task to assign a unit
 *
 */
public class TaskReassignTicketToUnitForAUserNotAvailable extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_ASSIGN_TICKET_TO_UNIT = "module.workflow.ticketing.task_assign_ticket_to_unit.labelAssignTicketToUnit";
    private static final String       MESSAGE_ASSIGN_TICKET_TO_UNIT_INFORMATION = "module.workflow.ticketing.task_reassign_ticket_to_unit.information";
    private static final String       MESSAGE_REASSIGN_TICKET_NO_MODIFICATIONS_INFORMATION = "module.workflow.ticketing.task_reassign_ticket.no_modifications_information";


    // PARAMETERS
    public static final String PARAMETER_ASSIGNEE_UNIT = "id_unit";

    private TicketReassignUnitResourceService _ticketReassignUnitResourceService                   = SpringContextService.getBean( TicketReassignUnitResourceService.BEAN_NAME );

    IResourceWorkflowHistoryDAO                         dao                                                  = SpringContextService.getBean( "ticketing.resourceWorkflowHistoryDAO" );

    // Services
    @Inject
    protected IResourceHistoryService _resourceHistoryServiceTicketing;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        String strUserId = request.getParameter( "id_user" );
        int nUserId = Integer.parseInt( strUserId );

        AdminUser user = AdminUserHome.findByPrimaryKey( nUserId );

        AdminUser adminUser = AdminAuthenticationService.getInstance( ).getRegisteredUser( request );

        if ( null != user )
        {
            // unite pour le tranfsert des tickets
            Unit unitToTransfer = null;

            // cas 1 : le user appatient a une unite
            boolean isUserInUnit = _ticketReassignUnitResourceService.checkUserInUnit( nUserId );

            Ticket ticket = TicketHome.findByPrimaryKey( dao.getidRessourceFromResourceWorkflowHistory( nIdResourceHistory ) );

            if ( ticket != null )
            {
                if ( isUserInUnit )
                {
                    // cas 1 : il appartient a une entite
                    List<Unit> unitsOfUser = UnitHome.findByIdUser( nUserId );
                    unitToTransfer = unitsOfUser.get( 0 );
                }
                else
                {
                    // cas 2 : il n'appartient pas a une entite
                    unitToTransfer = _ticketReassignUnitResourceService.findUnitToTransfer( ticket, nUserId );
                }

                if ( ( unitToTransfer != null ) )
                {
                    AssigneeUnit assigneeOldUnit = null;
                    if ( null != ticket.getAssigneeUnit( ) )
                    {
                        assigneeOldUnit = ticket.getAssigneeUnit( );
                    }

                    AssigneeUnit assigneeNewUnit = new AssigneeUnit( unitToTransfer );
                    AssigneeUser assigner = new AssigneeUser( adminUser );

                    ticket.setAssignerUser( assigner );
                    ticket.setAssignerUnit( assigneeOldUnit );
                    assigneeNewUnit.setUnitId( unitToTransfer.getIdUnit( ) );
                    assigneeNewUnit.setName( unitToTransfer.getLabel( ) );
                    ticket.setAssigneeUnit( assigneeNewUnit );
                    ticket.setAssigneeUser( null );

                    TicketHome.update( ticket );

                    immediateTicketIndexingWaiting( ticket );

                    request.setAttribute( TicketingConstants.ATTRIBUTE_IS_UNIT_CHANGED, ( assigneeNewUnit != assigneeOldUnit ) );
                    request.setAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION, REDIRECT_TO_LIST );

                    strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_UNIT_INFORMATION, Locale.FRENCH ), user.getFirstName( ), user.getLastName( ),
                            unitToTransfer.getLabel( ) );

                    // insert in workflow_resource_history_ticketing
                    ResourceHistory resourceHistory = new ResourceHistory( );
                    resourceHistory.setIdHistory( nIdResourceHistory );
                    resourceHistory.setIdChannel( ticket.getChannel( ).getId( ) );
                    if ( assigneeOldUnit == null )
                    {
                        resourceHistory.setIdUnitOld( -1 );
                    }
                    else
                    {
                        resourceHistory.setIdUnitOld( assigneeOldUnit.getUnitId( ) );
                    }
                    resourceHistory.setIdUnitNew( unitToTransfer.getIdUnit( ) );
                    _resourceHistoryServiceTicketing.create( resourceHistory, WorkflowUtils.getPlugin( ) );
                }
            }
            // In the case when there are no modifications
            if ( strTaskInformation.equals( StringUtils.EMPTY ) )
            {
                strTaskInformation = I18nService.getLocalizedString( MESSAGE_REASSIGN_TICKET_NO_MODIFICATIONS_INFORMATION, locale );
            }
        }
        return strTaskInformation;
    }


    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_UNIT, locale );
    }

    /**
     * Immediate indexation of a Ticket for the Backoffice
     *
     * @param idTicket
     *            the id of the Ticket to index
     */
    protected void immediateTicketIndexingWaiting( Ticket ticket )
    {
        IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
    }
}
