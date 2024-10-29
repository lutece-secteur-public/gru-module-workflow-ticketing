/*
 * Copyright (c) 2002-2024, City of Paris
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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.state.service.IChooseStateTaskService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskChooseNextStateConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskVspTicket}
 *
 */
public class ChooseNextStateTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String     TEMPLATE_TASK_CHOOSE_NEXT_STATE_FORM = "admin/plugins/workflow/modules/ticketing/task_choose_next_state_config.html";

    // MARKS
    private static final String     MARK_CONFIG                          = "config";
    private static final String     MARK_LIST_STATES                     = "list_states";

    // Errors
    private static final String     MESSAGE_STATE_EMPTY_OR_EQUALS        = "module.workflow.ticketing.task_choose_next_state_config.error.not.valid";

    private static final String     PARAMETER_STATE_OK                   = "id_state_ok";

    private static final String     PARAMETER_STATE_KO                   = "id_state_ko";

    // Services
    @Inject
    private IChooseStateTaskService _chooseStateTaskService;


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );

        TaskChooseNextStateConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        model.put( MARK_CONFIG, config );
        model.put( MARK_LIST_STATES, _chooseStateTaskService.getListStates( task.getAction( ).getId( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CHOOSE_NEXT_STATE_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {

        String strStateOK = request.getParameter( PARAMETER_STATE_OK );
        String strStateKO = request.getParameter( PARAMETER_STATE_KO );

        if ( StringUtils.isEmpty( strStateOK ) || StringUtils.isEmpty( strStateKO ) || strStateOK.equals( strStateKO ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STATE_EMPTY_OR_EQUALS, AdminMessage.TYPE_ERROR );
        }

        int nStateOK = Integer.parseInt( strStateOK );
        int nStateKO = Integer.parseInt( strStateKO );

        TaskChooseNextStateConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskChooseNextStateConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setIdStateOK( nStateOK );
        config.setIdStateKO( nStateKO );

        if ( bConfigToCreate )
        {
            getTaskConfigService( ).create( config );
        } else
        {
            getTaskConfigService( ).update( config );
        }

        return null;
    }
}
