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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskAssignTicketToUnit}
 *
 */
public class AssignTicketToUnitTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_ASSIGN_TICKET_TO_UNIT_FORM = "admin/plugins/workflow/modules/ticketing/task_assign_ticket_to_unit_form.html";

    // MESSAGE
    private static final String MESSAGE_NO_UNIT_FOUND = "module.workflow.ticketing.task_assign_ticket_to_unit.labelNoUnitFound";

    private static final String MESSAGE_DEFAULT_LABEL_ENTITY_TASK_FORM = "module.workflow.ticketing.task_assign_up_ticket.default.label.entity";

    // MARKS
    private static final String MARK_UNITS_LIST = "units_list";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );
        Map<String, Object> model = getModel( ticket );
        ReferenceList unitsList = null;

        if ( ticket != null )
        {
            AdminUser user = AdminUserService.getAdminUser( request );
            unitsList = getUnitsList( user );

            if ( ( unitsList == null ) || ( unitsList.size( ) == 0 ) )
            {
                request.setAttribute( ATTRIBUTE_HIDE_NEXT_STEP_BUTTON, Boolean.TRUE );
                addError( I18nService.getLocalizedString( MESSAGE_NO_UNIT_FOUND, locale ) );
            }
            else
            {
                model.put( MARK_UNITS_LIST, unitsList );
            }
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGN_TICKET_TO_UNIT_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * Load the data of all the unit objects allowed for assignment and returns them in form of a collection
     * 
     * @param user
     *            connected admin user
     * @return the list which contains the data of all the unit objects
     */
    protected static ReferenceList getUnitsList( AdminUser user )
    {
        List<Unit> lstUnits = UnitHome.findAll( );
        ReferenceList lstRef = new ReferenceList( lstUnits.size( ) );

        ReferenceItem emptyReferenceItem = new ReferenceItem( );
        emptyReferenceItem.setCode( StringUtils.EMPTY );
        emptyReferenceItem.setName( I18nService.getLocalizedString( MESSAGE_DEFAULT_LABEL_ENTITY_TASK_FORM, Locale.FRANCE ) );
        emptyReferenceItem.setChecked( true );
        lstRef.add( emptyReferenceItem );

        for ( Unit unit : lstUnits )
        {
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );

            if ( RBACService.isAuthorized( assigneeUnit, AssigneeUnit.PERMISSION_ASSIGN, user ) )
            {
                lstRef.addItem( unit.getIdUnit( ), unit.getLabel( ) );
            }
        }

        return lstRef;
    }
}
