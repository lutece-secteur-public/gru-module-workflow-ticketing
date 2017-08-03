package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.marking.Marking;
import fr.paris.lutece.plugins.ticketing.business.marking.MarkingHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskMarkAsUnreadConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
    	Map<String, Object> model = new HashMap<String, Object>( );
        TaskMarkAsUnreadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        List<Marking> listMarkings = new ArrayList<Marking>( );
    	listMarkings = MarkingHome.getMarkingsList( ); 
    	
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
        int nMarkingId = Integer.parseInt( request.getParameter( PARAMETER_MARKING_ID ) );

        TaskMarkAsUnreadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        Boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskMarkAsUnreadConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setIdMarking( nMarkingId );

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
}
