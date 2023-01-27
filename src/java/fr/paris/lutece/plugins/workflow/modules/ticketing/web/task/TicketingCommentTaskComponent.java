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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.comment.business.CommentValue;
import fr.paris.lutece.plugins.workflow.modules.comment.business.TaskCommentConfig;
import fr.paris.lutece.plugins.workflow.modules.comment.service.CommentResourceIdService;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ICommentValueService;
import fr.paris.lutece.plugins.workflow.modules.comment.web.CommentTaskComponent;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.content.ContentPostProcessor;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class TicketingCommentTaskComponent extends CommentTaskComponent
{
    // beans
    private static final String TICKETING_TICKET_REFERENCE_SERVICE = "ticketing.ticketReferenceService";
    private static final String WORKFLOW_COMMENT_VALUE_SERVICE = "workflow.commentValueService";

    TicketingTaskComponent taskComponent = new TicketingTaskComponent( );

    @Inject
    @Named( TICKETING_TICKET_REFERENCE_SERVICE )
    private ContentPostProcessor _contentPostProcessor;

    @Inject
    @Named( WORKFLOW_COMMENT_VALUE_SERVICE )
    private ICommentValueService _commentValueService;
    // TEMPLATES
    private static final String TEMPLATE_TASK_COMMENT_FORM = "admin/plugins/workflow/modules/comment/task_comment_form.html";
    private static final String TEMPLATE_TASK_COMMENT_INFORMATION = "admin/plugins/workflow/modules/comment/task_comment_information.html";

    // MARKS
    private static final String MARK_ID_HISTORY = "id_history";
    private static final String MARK_TASK = "task";
    private static final String MARK_CONFIG = "config";
    private static final String MARK_COMMENT_VALUE = "comment_value";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_HAS_PERMISSION_DELETE = "has_permission_delete";
    private static final String MARK_IS_OWNER = "is_owner";
    private static final String MARK_LIST_ID_TICKETS = "list_id_tickets";

    // PARAMETERS
    private static final String PARAMETER_COMMENT_VALUE = "comment_value";

    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = taskComponent.getModel( taskComponent.getTicket( nIdResource, strResourceType ) );

        TaskCommentConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        String strComment = request.getParameter( PARAMETER_COMMENT_VALUE + "_" + task.getId( ) );
        model.put( MARK_CONFIG, config );
        model.put( MARK_COMMENT_VALUE, strComment );

        if ( config.isRichText( ) )
        {
            model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
            model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        }

        model.put( MARK_LIST_ID_TICKETS, request.getParameterValues( TicketingConstants.PARAMETER_ID_TICKET ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        CommentValue commentValue = _commentValueService.findByPrimaryKey( nIdHistory, task.getId( ), WorkflowUtils.getPlugin( ) );

        if ( ( commentValue != null ) && StringUtils.isNotBlank( commentValue.getValue( ) ) && ( _contentPostProcessor != null ) )
        {
            String strComment = commentValue.getValue( );
            strComment = _contentPostProcessor.process( request, strComment );
            commentValue.setValue( strComment );
        }

        Map<String, Object> model = new HashMap<>( );
        TaskCommentConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        AdminUser userConnected = AdminUserService.getAdminUser( request );

        model.put( MARK_ID_HISTORY, nIdHistory );
        model.put( MARK_TASK, task );
        model.put( MARK_CONFIG, config );
        model.put( MARK_COMMENT_VALUE, commentValue );
        model.put( MARK_HAS_PERMISSION_DELETE, RBACService.isAuthorized( commentValue, CommentResourceIdService.PERMISSION_DELETE, ( User ) userConnected ) );
        model.put( MARK_IS_OWNER, _commentValueService.isOwner( nIdHistory, userConnected ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_INFORMATION, locale, model );

        return template.getHtml( );
    }
}
