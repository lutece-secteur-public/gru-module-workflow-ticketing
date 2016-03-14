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

import fr.paris.lutece.plugins.ticketing.business.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.web.TicketHelper;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.user.UserPreferencesJspBean;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.MessageDirection;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.prefs.AdminUserPreferencesService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskAssignTicketToUser}
 *
 */
public class SelectChannelTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_SELECT_CHANNEL_FORM = "admin/plugins/workflow/modules/ticketing/task_select_channel_form.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );
        Map<String, Object> model = getModel( ticket );

        boolean bIsAgentView = false;
        AdminUser user = AdminUserService.getAdminUser( request );
        AdminUser userFront = AdminUserHome.findByPrimaryKey( AppPropertiesService.getPropertyInt( 
                    TicketingConstants.PROPERTY_ADMINUSER_FRONT_ID, -1 ) );

        if ( ( user != null ) && ( user.getUserId(  ) != userFront.getUserId(  ) ) )
        {
            bIsAgentView = true;
        }

        model.put( TicketingConstants.MARK_AGENT_VIEW, bIsAgentView );

        model.put( TicketingConstants.MARK_CHANNELS_LIST, ChannelHome.getReferenceList(  ) );

        String strIdChannelList = AdminUserPreferencesService.instance(  )
                                                             .get( String.valueOf( 
                    AdminUserService.getAdminUser( request ).getUserId(  ) ),
                TicketingConstants.USER_PREFERENCE_CHANNELS_LIST, StringUtils.EMPTY );
        List<String> idChannelList = TicketHelper.getIdChannelList( strIdChannelList );
        model.put( TicketingConstants.MARK_SELECTABLE_ID_CHANNEL_LIST, idChannelList );

        String strPreferredIdChannel = AdminUserPreferencesService.instance(  )
                                                                  .get( String.valueOf( 
                    AdminUserService.getAdminUser( request ).getUserId(  ) ),
                TicketingConstants.USER_PREFERENCE_PREFERRED_CHANNEL, StringUtils.EMPTY );
        model.put( TicketingConstants.MARK_PREFERRED_ID_CHANNEL, strPreferredIdChannel );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_SELECT_CHANNEL_FORM, locale, model );

        return template.getHtml(  );
    }
}
