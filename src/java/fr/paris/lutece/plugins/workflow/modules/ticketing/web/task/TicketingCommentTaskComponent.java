package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.comment.business.TaskCommentConfig;
import fr.paris.lutece.plugins.workflow.modules.comment.web.CommentTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class TicketingCommentTaskComponent extends CommentTaskComponent
{
    TicketingTaskComponent taskComponent = new TicketingTaskComponent( );

    // TEMPLATES
    private static final String TEMPLATE_TASK_COMMENT_FORM = "admin/plugins/workflow/modules/comment/task_comment_form.html";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_COMMENT_VALUE = "comment_value";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_LIST_ID_TICKETS = "list_id_tickets";

    // PARAMETERS
    private static final String PARAMETER_COMMENT_VALUE = "comment_value";

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

}
