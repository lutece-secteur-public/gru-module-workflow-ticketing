package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

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
    private static final String  TICKETING_TICKET_REFERENCE_SERVICE = "ticketing.ticketReferenceService";
    private static final String  WORKFLOW_COMMENT_VALUE_SERVICE     = "workflow.commentValueService";

    TicketingTaskComponent       taskComponent                      = new TicketingTaskComponent( );

    @Inject
    @Named( TICKETING_TICKET_REFERENCE_SERVICE )
    private ContentPostProcessor _contentPostProcessor;

    @Inject
    @Named( WORKFLOW_COMMENT_VALUE_SERVICE )
    private ICommentValueService _commentValueService;
    // TEMPLATES
    private static final String  TEMPLATE_TASK_COMMENT_FORM         = "admin/plugins/workflow/modules/comment/task_comment_form.html";
    private static final String  TEMPLATE_TASK_COMMENT_INFORMATION  = "admin/plugins/workflow/modules/comment/task_comment_information.html";

    // MARKS
    private static final String  MARK_ID_HISTORY                    = "id_history";
    private static final String  MARK_TASK                          = "task";
    private static final String  MARK_CONFIG                        = "config";
    private static final String  MARK_COMMENT_VALUE                 = "comment_value";
    private static final String  MARK_WEBAPP_URL                    = "webapp_url";
    private static final String  MARK_LOCALE                        = "locale";
    private static final String  MARK_HAS_PERMISSION_DELETE         = "has_permission_delete";
    private static final String  MARK_IS_OWNER                      = "is_owner";
    private static final String  MARK_LIST_ID_TICKETS               = "list_id_tickets";

    // PARAMETERS
    private static final String  PARAMETER_COMMENT_VALUE            = "comment_value";

    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = taskComponent.getModel( taskComponent.getTicket( nIdResource, strResourceType ) );

        TaskCommentConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
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

        if ( commentValue != null && StringUtils.isNotBlank( commentValue.getValue( ) ) )
        {
            if ( _contentPostProcessor != null )
            {
                String strComment = commentValue.getValue( );
                strComment = _contentPostProcessor.process( request, strComment );
                commentValue.setValue( strComment );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        TaskCommentConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        AdminUser userConnected = AdminUserService.getAdminUser( request );

        model.put( MARK_ID_HISTORY, nIdHistory );
        model.put( MARK_TASK, task );
        model.put( MARK_CONFIG, config );
        model.put( MARK_COMMENT_VALUE, commentValue );
        model.put( MARK_HAS_PERMISSION_DELETE, RBACService.isAuthorized( commentValue, CommentResourceIdService.PERMISSION_DELETE, userConnected ) );
        model.put( MARK_IS_OWNER, _commentValueService.isOwner( nIdHistory, userConnected ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_INFORMATION, locale, model );

        return template.getHtml( );
    }
}
