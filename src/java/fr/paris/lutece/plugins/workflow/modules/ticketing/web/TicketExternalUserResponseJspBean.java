/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.user.UserFactory;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config.TaskTicketEmailExternalUserConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.ITicketEmailExternalUserHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.TicketEmailExternalUserHistory;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.TicketEmailExternalUserMessage;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.provider.TicketEmailExternalUserConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.RequestAuthenticationService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskTicketEmailExternalUser;
import fr.paris.lutece.plugins.workflow.modules.upload.business.file.UploadFile;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryDOA;
import fr.paris.lutece.plugins.workflow.modules.upload.services.download.DownloadFileService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;
import java.sql.Timestamp;

/**
 * TicketExternalUserResponse JSP Bean abstract class for JSP Bean
 */
@Controller( controllerJsp = "TicketExternalUserResponse.jsp", controllerPath = TicketEmailExternalUserConstants.ADMIN_EXTERNAL_USER_CONTROLLLER_PATH, right = "TICKETING_EXTERNAL_USER" )
public class TicketExternalUserResponseJspBean extends WorkflowCapableJspBean
{
    // Right
    public static final String RIGHT_EXTERNAL_USER = "TICKETING_EXTERNAL_USER";
    private static final long serialVersionUID = 1L;

    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_EXTERNAL_USER_RESPONSE = "admin/plugins/workflow/modules/ticketing/external_user/ticket_external_user_response.html";
    private static final String TEMPLATE_EXTERNAL_USER_MESSAGE = "admin/plugins/workflow/modules/ticketing/external_user/ticket_external_user_message.html";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_EXTERNAL_USER_RESPONSE = "module.workflow.ticketing.externalUserResponse.pageTitle";
    private static final String PROPERTY_EXTERNAL_USER_MESSAGE_OK = "module.workflow.ticketing.externalUserResponse.message.ok";
    private static final String PROPERTY_EXTERNAL_USER_MESSAGE_ALREADY_ANSWER = "module.workflow.ticketing.externalUserResponse.message.already_answer";
    private static final String PROPERTY_EXTERNAL_USER_MESSAGE_NOT_DONE = "module.workflow.ticketing.externalUserResponse.message.not_respond";
    private static final String PROPERTY_TICKET_DELETED = "module.workflow.ticketing.externalUserResponse.message.ticket_closed";
    private static final String PROPERTY_EXTERNAL_USER_MESSAGE_TIMEOUT = "module.workflow.ticketing.externalUserResponse.message.ticket_timeout";

    // Markers
    private static final String MARK_REFERENCE = "reference";
    private static final String MARK_LIST_EXTERNAL_USER_MESSAGE = "list_external_user_message";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_ID_TICKET = "id_ticket";
    private static final String MARK_ID_MESSAGE_EXTERNAL_USER = "id_message_external_user";
    private static final String MARK_LIST_FILE_UPLOAD = "list_file_uploaded";
    private static final String MARK_MAP_FILE_URL = "list_url";
    private static final String MARK_USER_FACTORY = "user_factory";
    private static final String MARK_USER_ADMIN = "user_admin";
    private static final String MARK_TASK_TICKET_EMAIL_EXTERNAL_USER_FORM = "task_ticket_email_external_user_form";
    private static final String MARK_KEY_MESSAGE = "key_message";
    private static final String MARK_TYPE_MESSAGE = "type_message";
    private static final String MARK_SIGNATURE = "signature";
    private static final String MARK_TIMESTAMP = "timestamp";

    // Views
    private static final String VIEW_TICKET_EXTERNAL_USER_RESPONSE = "externalUserReponse";

    // Other constants
    private boolean _bAvatarAvailable;

    /**
     * DAO beans and service
     */
    private ITicketEmailExternalUserHistoryDAO _ticketEmailExternalUserHistoryDAO;
    private ITicketEmailExternalUserMessageDAO _ticketEmailExternalUserMessageDAO;
    private ITaskConfigService _taskTicketExternalUserConfigService;
    private IResourceHistoryService _resourceHistoryService;

    /**
     *
     */
    public TicketExternalUserResponseJspBean( )
    {
        super( );
        _ticketEmailExternalUserHistoryDAO = SpringContextService.getBean( ITicketEmailExternalUserHistoryDAO.BEAN_SERVICE );
        _ticketEmailExternalUserMessageDAO = SpringContextService.getBean( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE );
        _taskTicketExternalUserConfigService = SpringContextService.getBean( TaskTicketEmailExternalUser.BEAN_TICKET_CONFIG_SERVICE );
        _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );
        _bAvatarAvailable = ( PluginService.getPlugin( TicketingConstants.PLUGIN_AVATAR ) != null );
    }

    public String getTimeout( HttpServletRequest request )
    {
        return getMessagePage( PROPERTY_EXTERNAL_USER_MESSAGE_TIMEOUT, SiteMessage.TYPE_WARNING );
    }

    /**
     * Returns the form using by field a to answer to agent
     *
     * @param request
     *            The Http request
     * @return the html code of the form
     */
    @View( value = VIEW_TICKET_EXTERNAL_USER_RESPONSE, defaultView = true )
    public String getExternalUserResponseView( HttpServletRequest request )
    {
        // Check sign request
        if ( !RequestAuthenticationService.getRequestAuthenticator( ).isRequestAuthenticated( request ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        // Retrieve objects
        String strIdEmailExternalUser = request.getParameter( TicketEmailExternalUserConstants.PARAMETER_ID_MESSAGE_EXTERNAL_USER );
        List<TicketEmailExternalUserMessageDisplay> listEmailExternalUserMessageDisplay = new ArrayList<TicketEmailExternalUserMessageDisplay>( );
        TicketEmailExternalUserMessage requiredEmailExternalUserMessage = null;
        TicketEmailExternalUserHistory externalUserHistory = null;
        TaskTicketEmailExternalUserConfig externalUserConfig = null;
        List<UploadFile> listFileUpload = new ArrayList<UploadFile>( );
        Map<String, Object> mapFileUrl = new HashMap<String, Object>( );
        Map<String, Timestamp> mapAllMessageQuestion = new HashMap<>(  );
        AdminUser userAdmin = null;

        try
        {
            requiredEmailExternalUserMessage = _ticketEmailExternalUserMessageDAO.loadByIdMessageExternalUser( Integer.parseInt( strIdEmailExternalUser ) );

            if ( requiredEmailExternalUserMessage == null )
            {
                return getMessagePage( PROPERTY_TICKET_DELETED, SiteMessage.TYPE_WARNING );
            }

            // ticket status
            if ( requiredEmailExternalUserMessage.getIsAnswered( ) )
            {
                return getMessagePage( PROPERTY_EXTERNAL_USER_MESSAGE_ALREADY_ANSWER, SiteMessage.TYPE_WARNING );
            }

            for ( TicketEmailExternalUserMessage emailExternalUserMessage : _ticketEmailExternalUserMessageDAO
                    .loadByIdTicketNotClosed( requiredEmailExternalUserMessage.getIdTicket( ) ) )
            {
                TicketEmailExternalUserMessageDisplay emailExternalUserMessageDisplay = new TicketEmailExternalUserMessageDisplay( );
                emailExternalUserMessageDisplay.setMessageQuestion( emailExternalUserMessage.getMessageQuestion( ) );

                List<TicketEmailExternalUserHistory> lstEmailExternalUserHistory = _ticketEmailExternalUserHistoryDAO
                        .loadByIdMessageExternalUser( emailExternalUserMessage.getIdMessageExternalUser( ) );

                // The size has to be at 1 because if the action of answer a question, so we should only have the history line of the question for the
                // nIdEmailExternalUser
                if ( lstEmailExternalUserHistory.size( ) == 1 )
                {
                    externalUserHistory = lstEmailExternalUserHistory.get( 0 );
                }

                // if the size is not 1, facilFamilesHistory is null, so NPE will be throw
                if ( externalUserConfig == null )
                {
                    externalUserConfig = _taskTicketExternalUserConfigService.findByPrimaryKey( externalUserHistory.getIdTask( ) );
                }

                List<UploadFile> listFileUploadTemp = FactoryDOA.getUploadFileDAO( ).load( externalUserHistory.getIdResourceHistory( ),
                        WorkflowUtils.getPlugin( ) );

                emailExternalUserMessageDisplay.setUploadedFiles( listFileUploadTemp );
                listFileUpload.addAll( listFileUploadTemp );

                ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( externalUserHistory.getIdResourceHistory( ) );

                userAdmin = AdminUserHome.findUserByLogin( resourceHistory.getUserAccessCode( ) );
                emailExternalUserMessageDisplay.setAdminUser( userAdmin );

                emailExternalUserMessageDisplay.setDateCreate( resourceHistory.getCreationDate( ) );

                if ( !mapAllMessageQuestion.containsKey( emailExternalUserMessageDisplay.getMessageQuestion() ) )
                {
                    listEmailExternalUserMessageDisplay.add( emailExternalUserMessageDisplay );
                    mapAllMessageQuestion.put( emailExternalUserMessageDisplay.getMessageQuestion(), emailExternalUserMessageDisplay.getDateCreate() );
                }
            }

            if ( !listFileUpload.isEmpty( ) )
            {
                String strBaseUrl = AppPathService.getBaseUrl( request );

                for ( int i = 0; i < listFileUpload.size( ); i++ )
                {
                    mapFileUrl.put( Integer.toString( listFileUpload.get( i ).getIdUploadFile( ) ),
                            DownloadFileService.getUrlDownloadFile( listFileUpload.get( i ).getIdFile( ), strBaseUrl ) );
                }
            }

        }
        catch( NumberFormatException | NullPointerException e )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MESSAGE_INVALID_ENTRY, AdminMessage.TYPE_STOP ) );
        }

        Ticket ticket = TicketHome.findByPrimaryKey( requiredEmailExternalUserMessage.getIdTicket( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_REFERENCE, ticket.getReference( ) );
        model.put( MARK_LIST_EXTERNAL_USER_MESSAGE, listEmailExternalUserMessageDisplay );
        model.put( MARK_LIST_FILE_UPLOAD, listFileUpload );
        model.put( MARK_MAP_FILE_URL, mapFileUrl );
        model.put( MARK_USER_FACTORY, UserFactory.getInstance( ) );
        model.put( TicketingConstants.MARK_AVATAR_AVAILABLE, _bAvatarAvailable );
        model.put( MARK_USER_ADMIN, userAdmin );
        model.put(
                MARK_TASK_TICKET_EMAIL_EXTERNAL_USER_FORM,
                WorkflowService.getInstance( ).getDisplayTasksForm( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, externalUserConfig.getIdFollowingAction( ),
                        request, getLocale( ) ) );
        model.put( TicketingConstants.MARK_FORM_ACTION, getActionUrl( TicketingConstants.ACTION_DO_PROCESS_WORKFLOW_ACTION, request ) );
        model.put( MARK_ID_ACTION, externalUserConfig.getIdFollowingAction( ) );
        model.put( MARK_ID_TICKET, ticket.getId( ) );
        model.put( MARK_ID_MESSAGE_EXTERNAL_USER, strIdEmailExternalUser );

        return getPage( PROPERTY_PAGE_TITLE_EXTERNAL_USER_RESPONSE, TEMPLATE_EXTERNAL_USER_RESPONSE, model );
    }

    protected String getActionUrl( String strAction, HttpServletRequest request )
    {
        UrlItem url = new UrlItem( getControllerPath( ) + getControllerJsp( ) );
        url.addParameter( MVCUtils.PARAMETER_ACTION, strAction );
        url.addParameter( MARK_SIGNATURE, request.getParameter( MARK_SIGNATURE ) );
        url.addParameter( MARK_TIMESTAMP, request.getParameter( MARK_TIMESTAMP ) );
        url.addParameter( MARK_ID_MESSAGE_EXTERNAL_USER, request.getParameter( MARK_ID_MESSAGE_EXTERNAL_USER ) );
        return url.getUrl( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectAfterWorkflowAction( HttpServletRequest request )
    {
        try
        {
            if ( !_ticketEmailExternalUserMessageDAO.loadByIdMessageExternalUser(
                    Integer.parseInt( request.getParameter( TicketEmailExternalUserConstants.PARAMETER_ID_MESSAGE_EXTERNAL_USER ) ) ).getIsAnswered( ) )
            {
                return getMessagePage( PROPERTY_EXTERNAL_USER_MESSAGE_NOT_DONE, SiteMessage.TYPE_WARNING );
            }
            else
            {
                return getMessagePage( PROPERTY_EXTERNAL_USER_MESSAGE_OK, SiteMessage.TYPE_INFO );
            }
        }
        catch( Exception e )
        {
            return getMessagePage( PROPERTY_EXTERNAL_USER_MESSAGE_NOT_DONE, SiteMessage.TYPE_WARNING );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectWorkflowActionCancelled( HttpServletRequest request )
    {
        Map<String, String> parameters = new HashMap<>( );
        parameters.put( MARK_SIGNATURE, request.getParameter( MARK_SIGNATURE ) );
        parameters.put( MARK_TIMESTAMP, request.getParameter( MARK_TIMESTAMP ) );
        parameters.put( MARK_ID_MESSAGE_EXTERNAL_USER, request.getParameter( MARK_ID_MESSAGE_EXTERNAL_USER ) );

        return redirect( request, VIEW_TICKET_EXTERNAL_USER_RESPONSE, parameters );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String defaultRedirectWorkflowAction( HttpServletRequest request )
    {
        return redirectView( request, VIEW_TICKET_EXTERNAL_USER_RESPONSE );
    }

    /**
     * call TEMPLATE_EXTERNAL_USER_MESSAGE template for the given message key
     *
     * @param strKeyMessage
     *            the key of the message
     * @param nMessageType
     *            the message type
     * @return the content of the page
     */
    private String getMessagePage( String strKeyMessage, int nMessageType )
    {
        Map<String, Object> model = getModel( );
        model.put( MARK_KEY_MESSAGE, strKeyMessage );
        model.put( MARK_TYPE_MESSAGE, nMessageType );

        return getPage( PROPERTY_PAGE_TITLE_EXTERNAL_USER_RESPONSE, TEMPLATE_EXTERNAL_USER_MESSAGE, model );
    }

    @Override
    protected boolean checkAccessToTicket( Ticket ticket )
    {
        return true;
    }
}
