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

import fr.paris.lutece.plugins.ticketing.web.TicketHelper;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.user.UserPreferencesJspBean;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.MessageDirection;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskReplyConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.prefs.AdminUserPreferencesService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskReply}
 *
 */
public class ReplyTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_REPLY_FORM = "admin/plugins/workflow/modules/ticketing/task_reply_form.html";
    private static final String TEMPLATE_TASK_REPLY_CONFIG = "admin/plugins/workflow/modules/ticketing/task_reply_config.html";

    // Markers
    private static final String MARK_USER_SIGNATURE = "user_signature";
    private static final String MARK_AGENT_VIEW = "agent_view";
    private static final String MARK_MESSAGE_DIRECTIONS_LIST = "message_directions_list";
    private static final String MARK_MESSAGE_DIRECTION = "message_direction";

    // Parameters
    private static final String PARAMETER_MESSAGE_DIRECTION = "message_direction";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskReplyConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );

        ReferenceList listMessageDirections = MessageDirection.getReferenceList( locale );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_MESSAGE_DIRECTIONS_LIST, listMessageDirections );

        if ( config != null )
        {
            model.put( MARK_MESSAGE_DIRECTION, config.getMessageDirection(  ).ordinal(  ) );
        }
        else
        {
            model.put( MARK_MESSAGE_DIRECTION, MessageDirection.AGENT_TO_USER );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_REPLY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        int nMessageDirectionId = Integer.parseInt( request.getParameter( PARAMETER_MESSAGE_DIRECTION ) );

        TaskReplyConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        Boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskReplyConfig(  );
            config.setIdTask( task.getId(  ) );
            bConfigToCreate = true;
        }

        config.setMessageDirection( MessageDirection.valueOf( nMessageDirectionId ) );

        if ( bConfigToCreate )
        {
            this.getTaskConfigService(  ).create( config );
        }
        else
        {
            this.getTaskConfigService(  ).update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Map<String, Object> model = getModel( getTicket( nIdResource, strResourceType ) );
        TaskReplyConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        String strUserSignature = StringUtils.EMPTY;
        boolean bIsAgentView = false;

        if ( config.getMessageDirection(  ) == MessageDirection.AGENT_TO_USER )
        {
            bIsAgentView = true;

            strUserSignature = AdminUserPreferencesService.instance(  )
                                                          .get( String.valueOf( 
                        AdminUserService.getAdminUser( request ).getUserId(  ) ),
                    TicketingConstants.USER_PREFERENCE_SIGNATURE, StringUtils.EMPTY );
        }

        model.put( MARK_AGENT_VIEW, bIsAgentView );
        model.put( MARK_USER_SIGNATURE, strUserSignature );

        TicketHelper.storeRichTextMarksIntoModel( request, model );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_REPLY_FORM, locale, model );

        return template.getHtml(  );
    }
}
