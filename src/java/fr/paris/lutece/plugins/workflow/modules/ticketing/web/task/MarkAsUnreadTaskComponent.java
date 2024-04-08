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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.marking.Marking;
import fr.paris.lutece.plugins.ticketing.business.marking.MarkingHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskMarkAsUnreadConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class MarkAsUnreadTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_MARK_AS_UNREAD_CONFIG = "admin/plugins/workflow/modules/ticketing/task_mark_as_unread_config.html";

    // Marks
    private static final String MARK_CONFIG = "config";
    private static final String MARK_MARKINGS = "list_markings";

    // Parameter
    private static final String PARAMETER_MARKING_ID = "marking_id";

    // Errors
    private static final String MESSAGE_EMPTY_MARKING = "module.workflow.ticketing.task_mark_as_unread_config.error.marking";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        TaskMarkAsUnreadConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        List<Marking> listMarkings = MarkingHome.getMarkingsList( );

        model.put( MARK_CONFIG, config );
        model.put( MARK_MARKINGS, listMarkings );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MARK_AS_UNREAD_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {

        String strMarkingId = request.getParameter( PARAMETER_MARKING_ID );
        if ( StringUtils.isEmpty( strMarkingId ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EMPTY_MARKING, AdminMessage.TYPE_ERROR );
        }

        int nMarkingId = Integer.parseInt( strMarkingId );

        TaskMarkAsUnreadConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskMarkAsUnreadConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setIdMarking( nMarkingId );

        if ( bConfigToCreate )
        {
            getTaskConfigService( ).create( config );
        }
        else
        {
            getTaskConfigService( ).update( config );
        }

        return null;
    }
}
