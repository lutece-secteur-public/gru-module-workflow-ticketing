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

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCriticality;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketPriority;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.web.task.SimpleTaskComponent;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.ticketing.service.workflow.task.TaskQualifyTicket}
 *
 */
public class QualifyTicketTaskComponent extends SimpleTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_QUALIFY_TICKET_FORM = "admin/plugins/workflow/modules/ticketing/task_qualify_ticket_form.html";

    // MARKS
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_TICKET_PRIORITIES_LIST = "ticket_priorities_list";
    private static final String MARK_TICKET_PRIORITY_DEFAULT = "ticket_priority_default";
    private static final String MARK_TICKET_CRITICALITIES_LIST = "ticket_criticalities_list";
    private static final String MARK_TICKET_CRITICALITY_DEFAULT = "ticket_criticality_default";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( ( strResourceType != null ) && Ticket.TICKET_RESOURCE_TYPE.equals( strResourceType ) )
        {
            Ticket ticket = TicketHome.findByPrimaryKey( nIdResource );

            if ( ticket != null )
            {
                model.put( MARK_TICKET, ticket );
            }
        }

        ReferenceList listPriorities = TicketPriority.getReferenceList( locale );
        model.put( MARK_TICKET_PRIORITIES_LIST, listPriorities );
        model.put( MARK_TICKET_PRIORITY_DEFAULT, TicketPriority.LOW );

        ReferenceList listCriticalities = TicketCriticality.getReferenceList( locale );
        model.put( MARK_TICKET_CRITICALITIES_LIST, listCriticalities );
        model.put( MARK_TICKET_CRITICALITY_DEFAULT, TicketCriticality.LOW );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_QUALIFY_TICKET_FORM, locale, model );

        return template.getHtml(  );
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        return null;
    }
}
