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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskAssignTicketToUser}
 *
 */
public class AssignTicketToUserTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_ASSIGN_TICKET_TO_USER_FORM = "admin/plugins/workflow/modules/ticketing/task_assign_ticket_to_user_form.html";

    // MESSAGE
    private static final String MESSAGE_NO_USER_FOUND = "module.workflow.ticketing.task_assign_ticket_to_user.labelNoUserFound";

    // MARKS
    private static final String MARK_USERS_LIST = "users_list";
    private static final String MARK_CURRENT_USER = "current_user";

    // Constantes
    private static final String EMPTY_CHOICE_IN_LIST = "-1";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );
        Map<String, Object> model = getModel( ticket );
        ReferenceList usersList = null;
        String strCurrentUserId = null;

        if ( ticket != null )
        {
            if ( ticket.getAssigneeUnit( ) != null )
            {
                usersList = getUsersList( ticket.getAssigneeUnit( ).getUnitId( ) );
                strCurrentUserId = ( ticket.getAssigneeUser( ) == null ) ? EMPTY_CHOICE_IN_LIST : ( String
                        .valueOf( ticket.getAssigneeUser( ).getAdminUserId( ) ) );

                if ( usersList.toMap( ).containsKey( strCurrentUserId ) )
                {
                    model.put( MARK_CURRENT_USER, strCurrentUserId );
                }
            }

            if ( ( usersList == null ) || ( usersList.size( ) <= 1 ) )
            {
                request.setAttribute( ATTRIBUTE_HIDE_NEXT_STEP_BUTTON, Boolean.TRUE );
                addError( I18nService.getLocalizedString( MESSAGE_NO_USER_FOUND, locale ) );
            }
            else
            {
                model.put( MARK_USERS_LIST, usersList );
            }
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGN_TICKET_TO_USER_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * Load the data of the user objects linked to the unit passed in parameter and returns them in form of a collection
     *
     * @param idUnit
     *            id unit
     * @return the list which contains the data of the user objects
     */
    protected static ReferenceList getUsersList( int idUnit )
    {
        List<Integer> lstIdUsers = UnitHome.findIdsUser( idUnit );
        ReferenceList lstRef = new ReferenceList( lstIdUsers.size( ) );
        lstRef.addItem( EMPTY_CHOICE_IN_LIST, StringUtils.EMPTY );

        for ( int idUser : lstIdUsers )
        {
            AdminUser user = AdminUserHome.findByPrimaryKey( idUser );
            if ( user != null )
            {
                lstRef.addItem( idUser, user.getFirstName( ) + " " + user.getLastName( ) );
            }
        }

        return lstRef;
    }
}
