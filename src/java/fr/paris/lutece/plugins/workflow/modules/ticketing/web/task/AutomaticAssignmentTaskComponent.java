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

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.assignment.UserAutomaticAssignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.information.TaskInformation;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.assignment.IAutomaticAssignmentService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.information.ITaskInformationService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskEditTicket}
 *
 */
public class AutomaticAssignmentTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_AUTOMATIC_ASSIGNMENT_CONFIG = "admin/plugins/workflow/modules/ticketing/task_automatic_assignment_config.html";
    private static final String URL_DISPLAY_CONFIG_FORM = "ModifyTask.jsp?id_task=";

    // Marks
    private static final String MARK_USER_ASSIGNMENT_LIST = "list_user_assignment";

    // Parameters
    private static final String PARAMETER_USER_ACCESS_CODE = "user_access_code";
    private static final String PARAMETER_ACTION = "action";

    //action type
    private static final String ACTION_REMOVE_ASSIGNMENT = "remove_assignment";

    // Other constants
    private static final String SEPARATOR = "<hr>";

    // SERVICES
    @Inject
    private IAutomaticAssignmentService _automaticAssignmentService;

    // SERVICES
    @Inject
    private ITaskInformationService _taskInformationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        List<UserAutomaticAssignmentConfig> listAdminUserAssigmentConfig = _automaticAssignmentService.getAllAutoAssignementConf( task.getId(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_USER_ASSIGNMENT_LIST, listAdminUserAssigmentConfig );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_AUTOMATIC_ASSIGNMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * unassign all assignment for given user
     * @param task task
     * @param strUserAccessCode user access code
     */
    private void deleteUserAssignement( ITask task, String strUserAccessCode )
    {
        _automaticAssignmentService.unassignByUser( task.getId(  ), strUserAccessCode );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strAction = request.getParameter( PARAMETER_ACTION );

        if ( StringUtils.isNotBlank( strAction ) )
        {
            if ( strAction.equals( ACTION_REMOVE_ASSIGNMENT ) )
            {
                deleteUserAssignement( task, request.getParameter( PARAMETER_USER_ACCESS_CODE ) );

                return getDisplayConfigFormUrl( task );
            }
        }

        return null;
    }

    /**
     * build and return display Config Form Url
     * @param task task
     * @return display Config Form Url
     */
    private static String getDisplayConfigFormUrl( ITask task )
    {
        return URL_DISPLAY_CONFIG_FORM + task.getId(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        String strTaskInformation = StringUtils.EMPTY;

        TaskInformation taskInformation = _taskInformationService.findByPrimaryKey( nIdHistory, task.getId(  ),
                WorkflowUtils.getPlugin(  ) );

        if ( taskInformation != null )
        {
            strTaskInformation = taskInformation.getValue(  ) + SEPARATOR;
        }

        return strTaskInformation;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
