/*
 * Copyright (c) 2002-2024, City of Paris
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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class represents a task to assign a user
 *
 */
public class TaskAssignTicketToUser extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_ASSIGN_TICKET_TO_USER = "module.workflow.ticketing.task_assign_ticket_to_user.labelAssignTicketToUser";
    private static final String MESSAGE_ASSIGN_TICKET_TO_USER_INFORMATION = "module.workflow.ticketing.task_assign_ticket_to_user.information";
    private static final String MESSAGE_ASSIGN_TICKET_TO_USER_INFORMATION_UNASSIGN_TICKET = "module.workflow.ticketing.task_assign_ticket_to_user.information.unassign_ticket";
    private static final String MESSAGE_ASSIGN_TICKET_TO_USER_NO_CURRENT_USER = "module.workflow.ticketing.task_assign_ticket_to_user.no_current_user";

    // PARAMETERS
    public static final String PARAMETER_ASSIGNEE_USER = "id_user";

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        String strUserId = request.getParameter( PARAMETER_ASSIGNEE_USER );

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            AssigneeUser assigneeUser = ticket.getAssigneeUser( );
            String strCurrentUser = null;

            if ( assigneeUser == null )
            {
                assigneeUser = new AssigneeUser( );
                strCurrentUser = I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_USER_NO_CURRENT_USER, Locale.FRENCH );
            }
            else
            {
                strCurrentUser = assigneeUser.getFirstname( ) + " " + assigneeUser.getLastname( );
            }

            AdminUser user = null;

            if ( strUserId != null )
            {
                user = AdminUserHome.findByPrimaryKey( Integer.parseInt( strUserId ) );
            }

            if ( user != null )
            {
                if ( user.getUserId( ) != assigneeUser.getAdminUserId( ) )
                {
                    assigneeUser.setAdminUserId( user.getUserId( ) );
                    assigneeUser.setEmail( user.getEmail( ) );
                    assigneeUser.setFirstname( user.getFirstName( ) );
                    assigneeUser.setLastname( user.getLastName( ) );
                    ticket.setAssigneeUser( assigneeUser );
                    TicketHome.update( ticket );

                    strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_USER_INFORMATION, Locale.FRENCH ),
                            strCurrentUser, assigneeUser.getFirstname( ) + " " + assigneeUser.getLastname( ) );
                }
            }
            else
            {
                // Unassign ticket
                ticket.setAssigneeUser( null );
                TicketHome.update( ticket );

                if ( assigneeUser.getAdminUserId( ) != 0 )
                {
                    strTaskInformation = MessageFormat.format(
                            I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_USER_INFORMATION_UNASSIGN_TICKET, Locale.FRENCH ), strCurrentUser );
                }
            }
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_USER, locale );
    }
}
