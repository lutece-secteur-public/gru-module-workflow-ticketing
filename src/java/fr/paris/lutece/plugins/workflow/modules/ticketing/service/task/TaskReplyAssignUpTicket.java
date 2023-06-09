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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a task to respond to assigner after assigning up
 *
 */
public class TaskReplyAssignUpTicket extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_REPLY_ASSIGN_UP_TICKET = "module.workflow.ticketing.task_reply_assign_up_ticket.labelReplyAssignUpTicket";
    private static final String MESSAGE_REPLY_ASSIGN_UP_TICKET_INFORMATION = "module.workflow.ticketing.task_reply_assign_up_ticket.information";
    private static final String MESSAGE_REPLY_ASSIGN_TICKET_NO_CURRENT_USER = "module.workflow.ticketing.task_reply_assign_up_ticket.no_current_user";
    private static final String MESSAGE_REPLY_ASSIGN_TICKET_NO_USER_FOUND = "module.workflow.ticketing.task_reply_assign_up_ticket.no_user_found";
    private static final String PROPERTY_ASSIGN_UP_ACTION_ID = "workflow-ticketing.workflow.action.id.assignUp";
    private static final String PROPERTY_ASSIGN_TO_UNIT_ACTION_ID = "workflow-ticketing.workflow.action.id.assignToUnit";
    private static final int ASSIGN_UP_ACTION_ID = AppPropertiesService.getPropertyInt( PROPERTY_ASSIGN_UP_ACTION_ID, 304 );
    private static final int ASSIGN_TO_UNIT_ACTION_ID = AppPropertiesService.getPropertyInt( PROPERTY_ASSIGN_TO_UNIT_ACTION_ID, 305 );

    // Services
    @Inject
    protected IResourceHistoryService _resourceHistoryServiceTicketing;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            AssigneeUnit assigneeUnit = ticket.getAssigneeUnit( );
            AssigneeUser assigneeUser = ticket.getAssigneeUser( );
            String strCurrentUnit = null;

            if ( assigneeUnit != null )
            {
                strCurrentUnit = assigneeUnit.getName( );
            }

            if ( assigneeUser == null )
            {
                assigneeUser = new AssigneeUser( );
            }

            AdminUser user = getAssigner( nIdResourceHistory );
            Unit unit = getAssignerUnitBeforeRelance( nIdResourceHistory );


            if ( ( user != null ) && ( user.getUserId( ) != assigneeUser.getAdminUserId( ) ) )
            {
                assigneeUser.setAdminUserId( user.getUserId( ) );
                assigneeUser.setEmail( user.getEmail( ) );
                assigneeUser.setFirstname( user.getFirstName( ) );
                assigneeUser.setLastname( user.getLastName( ) );
                ticket.setAssigneeUser( assigneeUser );

                if ( ticket.getAssignerUnit( ).getUnitId( ) != ticket.getAssigneeUnit( ).getUnitId( ) )
                {
                    request.setAttribute( TicketingConstants.ATTRIBUTE_IS_UNIT_CHANGED, true );
                }

                if ( unit != null )
                {
                    AssigneeUnit assigneeUnitOld = new AssigneeUnit( unit );
                    ticket.setAssigneeUnit( assigneeUnitOld );

                }
                else
                {
                    List<Unit> unitsList = UnitHome.findByIdUser( user.getUserId( ) );

                    if ( ( unitsList != null ) && ( !unitsList.isEmpty( ) ) )
                    {
                        assigneeUnit = new AssigneeUnit( unitsList.get( 0 ) );
                        ticket.setAssigneeUnit( assigneeUnit );
                    }
                }

                ticket.setAssignerUser( null );
                ticket.setAssignerUnit( null );

                TicketHome.update( ticket );

                strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_REPLY_ASSIGN_UP_TICKET_INFORMATION, Locale.FRENCH ),
                        ( strCurrentUnit != null ) ? strCurrentUnit : StringUtils.EMPTY,
                                ( ticket.getAssigneeUser( ) != null ) ? ( ticket.getAssigneeUser( ).getFirstname( ) + " " + ticket.getAssigneeUser( ).getLastname( ) )
                                        : I18nService.getLocalizedString( MESSAGE_REPLY_ASSIGN_TICKET_NO_CURRENT_USER, Locale.FRENCH ),
                                        ( ticket.getAssigneeUnit( ) != null ) ? ticket.getAssigneeUnit( ).getName( ) : StringUtils.EMPTY );
            }
            else
            {
                strTaskInformation = I18nService.getLocalizedString( MESSAGE_REPLY_ASSIGN_TICKET_NO_USER_FOUND, Locale.FRENCH );
            }
        }

        if ( request != null )
        {
            request.setAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION, REDIRECT_TO_LIST );
        }

        return strTaskInformation;
    }

    /**
     * Get the user assigning up the ticket corresponding to the resource of the resourceHistory id
     *
     * @param nIdResourceHistory
     *            the resourceHistory id
     * @return the user assigning up the ticket corresponding to the resource of the resourceHistory id , {@code null} otherwise
     */
    protected AdminUser getAssigner( int nIdResourceHistory )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

        List<Integer> listIdResource = new ArrayList<>( );
        listIdResource.add( resourceHistory.getIdResource( ) );

        List<Integer> listIdHistory = _resourceHistoryService.getListHistoryIdByListIdResourceId( listIdResource, resourceHistory.getResourceType( ),
                resourceHistory.getWorkflow( ).getId( ) );

        boolean isAssignUpActionFound = false;
        ListIterator<Integer> iterator = listIdHistory.listIterator( listIdHistory.size( ) );

        while ( !isAssignUpActionFound && iterator.hasPrevious( ) )
        {
            resourceHistory = _resourceHistoryService.findByPrimaryKey( iterator.previous( ) );

            if ( ( resourceHistory.getAction( ).getId( ) == ASSIGN_UP_ACTION_ID ) || ( resourceHistory.getAction( ).getId( ) == ASSIGN_TO_UNIT_ACTION_ID ) )
            {
                isAssignUpActionFound = true;
            }
        }

        return ( isAssignUpActionFound ? AdminUserHome.findUserByLogin( resourceHistory.getUserAccessCode( ) ) : null );

    }

    /**
     * Get the unit assigning up the ticket corresponding to the resource of the resourceHistory id
     *
     * @param nIdResourceHistory
     *            the resourceHistory id
     * @return the unit assigning up the ticket corresponding to the resource of the resourceHistory id , {@code null} otherwise
     */
    protected Unit getAssignerUnitBeforeRelance( int nIdResourceHistory )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

        List<Integer> listIdResource = new ArrayList<>( );
        listIdResource.add( resourceHistory.getIdResource( ) );

        List<Integer> listIdHistory = _resourceHistoryService.getListHistoryIdByListIdResourceId( listIdResource, resourceHistory.getResourceType( ), resourceHistory.getWorkflow( ).getId( ) );

        boolean isAssignUpActionFound = false;
        ListIterator<Integer> iterator = listIdHistory.listIterator( listIdHistory.size( ) );
        fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.ResourceHistory resourceHistoryTicketing = null;

        while ( !isAssignUpActionFound && iterator.hasPrevious( ) )
        {
            resourceHistory = _resourceHistoryService.findByPrimaryKey( iterator.previous( ) );

            if ( ( resourceHistory.getAction( ).getId( ) == ASSIGN_UP_ACTION_ID ) || ( resourceHistory.getAction( ).getId( ) == ASSIGN_TO_UNIT_ACTION_ID ) )
            {
                isAssignUpActionFound = true;
                resourceHistoryTicketing = _resourceHistoryServiceTicketing.findOldUnitByPrimaryKey( resourceHistory.getId( ), WorkflowUtils.getPlugin( ) );
            }
        }

        return ( resourceHistoryTicketing != null ? UnitHome.findByPrimaryKey( resourceHistoryTicketing.getIdUnitOld( ) ) : null );

    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_REPLY_ASSIGN_UP_TICKET, locale );
    }
}
