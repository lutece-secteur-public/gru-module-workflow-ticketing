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

import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.MessageDirection;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskReplyConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskReply}
 *
 */
public class ReplyTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_REPLY_FORM     = "admin/plugins/workflow/modules/ticketing/task_reply_form.html";
    private static final String TEMPLATE_TASK_REPLY_CONFIG   = "admin/plugins/workflow/modules/ticketing/task_reply_config.html";

    // Markers
    private static final String MARK_AGENT_VIEW              = "agent_view";
    private static final String MARK_MESSAGE_DIRECTIONS_LIST = "message_directions_list";
    private static final String MARK_MESSAGE_DIRECTION       = "message_direction";
    private static final String MARK_CLOSE_TICKET            = "close_ticket";
    private static final String MARK_LIST_ID_TICKETS         = "list_id_tickets";

    // Parameters
    private static final String PARAMETER_MESSAGE_DIRECTION  = "message_direction";
    private static final String PARAMETER_CLOSE_TICKET       = "close_ticket";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskReplyConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        ReferenceList listMessageDirections = MessageDirection.getReferenceList( locale );

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_MESSAGE_DIRECTIONS_LIST, listMessageDirections );

        if ( config != null )
        {
            model.put( MARK_MESSAGE_DIRECTION, config.getMessageDirection( ).ordinal( ) );
            model.put( MARK_CLOSE_TICKET, config.isCloseTicket( ) );
        } else
        {
            model.put( MARK_CLOSE_TICKET, false );
            model.put( MARK_MESSAGE_DIRECTION, MessageDirection.AGENT_TO_USER );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_REPLY_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        int nMessageDirectionId = Integer.parseInt( request.getParameter( PARAMETER_MESSAGE_DIRECTION ) );
        boolean bCloseTicket = Boolean.parseBoolean( request.getParameter( PARAMETER_CLOSE_TICKET ) );

        TaskReplyConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        Boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskReplyConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setMessageDirection( MessageDirection.valueOf( nMessageDirectionId ) );
        config.setCloseTicket( bCloseTicket );

        if ( bConfigToCreate )
        {
            this.getTaskConfigService( ).create( config );
        } else
        {
            this.getTaskConfigService( ).update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = getModel( getTicket( nIdResource, strResourceType ) );
        TaskReplyConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        boolean bIsAgentView = false;

        if ( config.getMessageDirection( ) == MessageDirection.AGENT_TO_USER )
        {
            bIsAgentView = true;

            ModelUtils.storeUserSignature( request, model );
        }

        model.put( MARK_AGENT_VIEW, bIsAgentView );
        model.put( MARK_LIST_ID_TICKETS, request.getParameterValues( TicketingConstants.PARAMETER_ID_TICKET ) );

        ModelUtils.storeRichText( request, model );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_REPLY_FORM, locale, model );

        return template.getHtml( );
    }
}
