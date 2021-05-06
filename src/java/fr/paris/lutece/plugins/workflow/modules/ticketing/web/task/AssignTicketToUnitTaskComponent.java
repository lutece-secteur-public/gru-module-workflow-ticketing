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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.CollectionUtils;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskAssignTicketToUnitConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskAssignTicketToUnit}
 *
 */
public class AssignTicketToUnitTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_ASSIGN_TICKET_TO_UNIT_FORM   = "admin/plugins/workflow/modules/ticketing/task_assign_ticket_to_unit_form.html";

    private static final String TEMPLATE_TASK_ASSIGN_TICKET_TO_UNIT_CONFIG = "admin/plugins/workflow/modules/ticketing/task_assign_ticket_to_unit_config.html";

    // MESSAGE
    private static final String MESSAGE_NO_UNIT_FOUND                      = "module.workflow.ticketing.task_assign_ticket_to_unit.labelNoUnitFound";

    // MARKS
    private static final String MARK_UNITS_LIST                            = "units_list";
    private static final String MARK_LEVEL                                 = "level_selected";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        TaskAssignTicketToUnitConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if ( config != null )
        {
            model.put( MARK_LEVEL, config.getIdLevel( ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGN_TICKET_TO_UNIT_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskAssignTicketToUnitConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        int level = Integer.parseInt( request.getParameter( MARK_LEVEL ));

        if ( config == null )
        {
            config = new TaskAssignTicketToUnitConfig( );
            config.setIdTask( task.getId( ) );
            config.setIdLevel(level );
            getTaskConfigService( ).create( config );
        } else {
            config.setIdLevel(level );
            getTaskConfigService( ).update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );
        Map<String, Object> model = getModel( ticket );
        ReferenceList unitsList = null;
        TaskAssignTicketToUnitConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        // Default assignement level = 2
        int level = config != null ? config.getIdLevel( ) : 2;

        if ( ticket != null )
        {
            AdminUser user = AdminUserService.getAdminUser( request );
            unitsList = getUnitsList( user, level );

            if ( CollectionUtils.isEmpty( unitsList ) )
            {
                request.setAttribute( ATTRIBUTE_HIDE_NEXT_STEP_BUTTON, Boolean.TRUE );
                addError( I18nService.getLocalizedString( MESSAGE_NO_UNIT_FOUND, locale ) );
            } else
            {
                model.put( MARK_UNITS_LIST, unitsList );
            }
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGN_TICKET_TO_UNIT_FORM, locale, model );

        return template.getHtml( );
    }
}
