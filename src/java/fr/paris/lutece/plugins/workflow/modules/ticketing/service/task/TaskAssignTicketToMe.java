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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class represents a task to assign to me
 *
 */
public class TaskAssignTicketToMe extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_ASSIGN_TICKET_TO_ME = "module.workflow.ticketing.task_assign_ticket_to_me.labelAssignTicketToMe";
    private static final String MESSAGE_ASSIGN_TICKET_TO_ME_INFORMATION = "module.workflow.ticketing.task_assign_ticket_to_me.information";
    private static final String MESSAGE_ASSIGN_TICKET_TO_ME_NO_CURRENT_USER = "module.workflow.ticketing.task_assign_ticket_to_me.no_current_user";

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        boolean bBypassAssignToMe = ( request != null )
                && BooleanUtils.isTrue( (Boolean) request.getAttribute( TicketingConstants.ATTRIBUTE_BYPASS_ASSSIGN_TO_ME ) );

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ( ticket != null ) && !bBypassAssignToMe )
        {
            AssigneeUser assigneeUser = ticket.getAssigneeUser( );
            String strCurrentUser = null;

            if ( assigneeUser == null )
            {
                assigneeUser = new AssigneeUser( );
                strCurrentUser = I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_ME_NO_CURRENT_USER, Locale.FRENCH );
            }
            else
            {
                strCurrentUser = assigneeUser.getFirstname( ) + " " + assigneeUser.getLastname( );
            }

            if ( request != null )
            {
                AdminUser user = AdminUserService.getAdminUser( request );

                if ( ( user != null ) && ( user.getUserId( ) != assigneeUser.getAdminUserId( ) ) )
                {
                    assigneeUser.setAdminUserId( user.getUserId( ) );
                    assigneeUser.setEmail( user.getEmail( ) );
                    assigneeUser.setFirstname( user.getFirstName( ) );
                    assigneeUser.setLastname( user.getLastName( ) );
                    ticket.setAssigneeUser( assigneeUser );

                    List<Unit> unitsList = UnitHome.findByIdUser( user.getUserId( ) );

                    if ( ( unitsList != null ) && ( !unitsList.isEmpty( ) ) )
                    {
                        AssigneeUnit assigneeUnit = new AssigneeUnit( unitsList.get( 0 ) );
                        ticket.setAssigneeUnit( assigneeUnit );
                    }

                    TicketHome.update( ticket );

                    strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_ME_INFORMATION, Locale.FRENCH ),
                            strCurrentUser, assigneeUser.getFirstname( ) + " " + assigneeUser.getLastname( ) );
                }
            }
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_ME, locale );
    }
}
