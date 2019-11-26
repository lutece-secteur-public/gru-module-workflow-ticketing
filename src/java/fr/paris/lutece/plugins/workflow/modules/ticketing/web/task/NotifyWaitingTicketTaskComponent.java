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

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskNotifyWaitingTicketConfig;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class NotifyWaitingTicketTaskComponent  extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_TICKET_CONFIG = "admin/plugins/workflow/modules/ticketing/task_notify_waiting_ticket_config.html";

    // Marks
    private static final String MARK_CONFIG = "config";
    private static final String MARK_CONFIG_MESSAGE = "message";
    private static final String MARK_CONFIG_SENDER = "sender";
    private static final String MARK_CONFIG_SUBJECT = "subject";

    // Parameters config
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SENDER = "sender";
    private static final String PARAMETER_SUBJECT = "subject";

    // Error message
    private static final String MESSAGE_EMPTY_FIELD = "module.workflow.ticketing.task_notify_waiting_ticket.error.field.empty";


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );

        TaskNotifyWaitingTicketConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if (config != null)
        {
            // put value if not null or empty if null
            model.put( MARK_CONFIG_SENDER, config.getSenderName()==null?StringUtils.EMPTY:config.getSenderName() );
            model.put( MARK_CONFIG_SUBJECT, config.getSubject()==null?StringUtils.EMPTY:config.getSubject() );
            model.put( MARK_CONFIG_MESSAGE, config.getMessage()==null?StringUtils.EMPTY:config.getMessage() );
        }
        else {
            model.put( MARK_CONFIG_SENDER, StringUtils.EMPTY );
            model.put( MARK_CONFIG_SUBJECT, StringUtils.EMPTY );
            model.put( MARK_CONFIG_MESSAGE, StringUtils.EMPTY );
        }

        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_TICKET_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strSender = request.getParameter( PARAMETER_SENDER );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );

        TaskNotifyWaitingTicketConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskNotifyWaitingTicketConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setSenderName( strSender );
        config.setSubject( strSubject );
        config.setMessage( strMessage );

        String strJspError = this.validateConfig( config, request );

        if ( StringUtils.isNotBlank( strJspError ) )
        {
            return strJspError;
        }

        if ( bConfigToCreate )
        {
            this.getTaskConfigService( ).create( config );
        }
        else
        {
            this.getTaskConfigService( ).update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        Set<ConstraintViolation<ITaskConfig>> setConstraintErrors = BeanValidationUtil.validate( config );

        if ( !setConstraintErrors.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EMPTY_FIELD, AdminMessage.TYPE_ERROR );
        }

        return null;

    }
}
