/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskModifyTicketNomenclature}
 *
 */
public class ModifyTicketNomenclatureTaskComponent extends TicketingTaskComponent
{
    // template
    private static final String TEMPLATE_TASK_MODIFY_TICKET_NOMENCLATURE_FORM = "admin/plugins/workflow/modules/ticketing/task_modify_ticket_nomenclature_form.html";

    // list
    private static final String MARK_TICKET_NOMENCLATURE                      = "ticket_nomenclature";

    // Parameter
    private static final String PARAMETER_TICKET_NOMENCLATURE                 = "nomenclature";

    // Message reply
    private static final String MESSAGE_MODIFY_TICKET_NOMENCLATURE_ERROR      = "module.workflow.ticketing.task_modify_ticket_nomenclature.error";

    // Property
    private static final String PROPERTY_NOMENCLATURE_REGEXP                  = "workflow-ticketing.workflow.nomenclature.regexp";

    @Override
    public String getDisplayTaskForm( int nIdHistory, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdHistory, strResourceType );
        String strNomenclature = ticket.getNomenclature( );

        Map<String, String> model = new HashMap<String, String>( );

        model.put( MARK_TICKET_NOMENCLATURE, strNomenclature );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MODIFY_TICKET_NOMENCLATURE_FORM, locale, model );

        return template.getHtml( );
    }

    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        String strNewNomenclature = request.getParameter( PARAMETER_TICKET_NOMENCLATURE );

        if ( StringUtils.isNotEmpty( strNewNomenclature ) && !strNewNomenclature.matches( AppPropertiesService.getProperty( PROPERTY_NOMENCLATURE_REGEXP ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MODIFY_TICKET_NOMENCLATURE_ERROR, AdminMessage.TYPE_STOP );
        }

        return null;
    }
}
