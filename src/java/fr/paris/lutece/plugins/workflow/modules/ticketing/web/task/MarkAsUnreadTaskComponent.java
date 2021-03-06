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
        Map<String, Object> model = new HashMap<String, Object>( );
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
