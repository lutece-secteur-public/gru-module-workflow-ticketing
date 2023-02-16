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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskModifyTicketCategoryConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.constants.Messages;

@Controller( controllerJsp = "ModifyTicketCategory.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class ModifyTicketCategoryJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = 1L;

    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_ticket_category";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String VIEW_TICKET_FORM = "ticketForm";

    // Beans
    private static final String BEAN_MODIFY_TICKET_CATEGORY_CONFIG_SERVICE = "workflow-ticketing.taskModifyTicketCategoryConfigService";
    private final TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );
    private final ITaskConfigService _taskModifyTicketCategoryConfigService = SpringContextService.getBean( BEAN_MODIFY_TICKET_CATEGORY_CONFIG_SERVICE );

    /**
     * returns form linked to the selected category
     *
     * @param request
     *            http request with id_ticket_category
     * @return ticket form
     */
    @View( VIEW_TICKET_FORM )
    public String getTicketForm( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_VIEW, ( User ) getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );

        if ( !StringUtils.isEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) && !StringUtils.isEmpty( strIdTask )
                && StringUtils.isNumeric( strIdTask ) )
        {
            int nIdCategory = Integer.parseInt( strIdCategory );
            int nIdTask = Integer.parseInt( strIdTask );
            TaskModifyTicketCategoryConfig config = _taskModifyTicketCategoryConfigService.findByPrimaryKey( nIdTask );

            return _ticketFormService.getHtmlFormInputs( getLocale( ), false, nIdCategory, config.getSelectedEntries( ), request );
        }

        return StringUtils.EMPTY;
    }
}
