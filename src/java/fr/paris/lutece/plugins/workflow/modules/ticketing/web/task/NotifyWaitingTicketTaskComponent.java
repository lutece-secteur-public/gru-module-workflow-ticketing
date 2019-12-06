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

import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.TaskNotifyGruConfigService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskNotifyWaitingTicketConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc.ITicketEmailExternalUserCcDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config.MessageDirectionExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.ITicketEmailExternalUserHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.TicketEmailExternalUserMessage;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.provider.TicketEmailExternalUserProviderManager;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.recipient.ITicketEmailExternalUserRecipientDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.ExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.IExternalUserDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskTicketEmailExternalUser;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import java.util.*;

public class NotifyWaitingTicketTaskComponent  extends TicketingTaskComponent
{


    // Marks




    private static final String MARK_TICKETING_EMAIL_INFOS_CC = "email_infos_cc";

    @Inject
    @Named( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserMessageDAO _ticketEmailExternalUserDemandDAO;

    @Inject
    @Named( IExternalUserDAO.BEAN_SERVICE )
    private IExternalUserDAO _externalUserDAO;




    // XXX //
    // TEMPLATES
    private static final String TEMPLATE_TASK_TICKET_CONFIG = "admin/plugins/workflow/modules/ticketing/external_user/task_ticket_email_external_user_config.html";
    private static final String TEMPLATE_TASK_TICKET_INFORMATION = "admin/plugins/workflow/modules/ticketing/external_user/task_ticket_email_external_user_informations.html";

    // Marks
    private static final String MARK_CONFIG = "config";
    private static final String MARK_CONFIG_FOLLOW_ACTION_ID = "following_action_id";
    private static final String MARK_TICKETING_MESSAGE = "external_user_message";
    private static final String MARK_TICKETING_LIST_EMAIL_INFOS = "list_email_infos";
    private static final String MARK_MESSAGE_DIRECTIONS_LIST = "message_directions_list";
    private static final String MARK_MESSAGE_DIRECTION = "message_direction";
    private static final String MARK_CONFIG_CONTACT_ATTRIBUTE = "contact_attribute_id";
    private static final String MARK_CONFIG_DEFAULT_SUBJECT = "default_subject";

    // Parameters config
    private static final String PARAMETER_MESSAGE_DIRECTION = "message_direction";
    private static final String PARAMETER_FOLLOW_ACTION_ID = "following_action_id";
    private static final String PARAMETER_CONTACT_ATTRIBUTE = "contact_attribute_id";
    private static final String PARAMETER_DEFAULT_SUBJECT = "default_subject";

    // Error message
    public static final String MESSAGE_EMPTY_EMAIL = "module.workflow.ticketing.task_ticket_email_external_user.error.email.empty";
    public static final String MESSAGE_INVALID_EMAIL_OR_NOT_AUTHORIZED = "module.workflow.ticketing.task_ticket_email_external_user.error.email.invalid.not_authorized";
    public static final String MESSAGE_INVALID_EMAIL = "module.workflow.ticketing.task_ticket_email_external_user.error.email.invalid";
    private static final String MESSAGE_EMPTY_FIELD = "module.workflow.ticketing.task_ticket_email_external_user.error.field.empty";

    // Constant

    @Inject
    @Named( TaskTicketEmailExternalUser.BEAN_TICKET_CONFIG_SERVICE )
    private ITaskConfigService _taskTicketConfigService;

    @Inject
    @Named( ITicketEmailExternalUserHistoryDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserHistoryDAO _ticketEmailExternalUserHistoryDAO;

    @Inject
    @Named( ITicketEmailExternalUserRecipientDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserRecipientDAO _ticketEmailExternalUserRecipientDAO;

    @Inject
    @Named( ITicketEmailExternalUserCcDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserCcDAO _ticketEmailExternalUserCcDAO;

    @Inject
    @Named( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserMessageDAO _ticketEmailExternalUserMessageDAO;

    @Inject
    @Named( ActionService.BEAN_SERVICE )
    private ActionService _actionService;


    @Inject
    @Named( TaskService.BEAN_SERVICE )
    private ITaskService _taskService;

    @Inject
    @Named( TaskNotifyGruConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskNotifyGruConfigService;

    @Inject
    @Named( "workflow-ticketing.externaluser.provider-manager" )
    private TicketEmailExternalUserProviderManager _ticketEmailExternalUserProviderManager;

    /** The _resource history service. */
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    // Constant


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        TaskNotifyWaitingTicketConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        ReferenceList listMessageDirections = MessageDirectionExternalUser.getReferenceList( locale );

        model.put( MARK_MESSAGE_DIRECTIONS_LIST, listMessageDirections );
        model.put( MARK_CONFIG_FOLLOW_ACTION_ID, StringUtils.EMPTY );
        model.put( MARK_CONFIG_CONTACT_ATTRIBUTE, StringUtils.EMPTY );
        model.put( MARK_CONFIG_DEFAULT_SUBJECT, StringUtils.EMPTY );

        if ( config != null )
        {
            model.put( MARK_MESSAGE_DIRECTION, config.getMessageDirectionExternalUser( ).ordinal( ) );

            if ( config.getIdFollowingAction( ) != null )
            {
                model.put( MARK_CONFIG_FOLLOW_ACTION_ID, config.getIdFollowingAction( ) );
            }

            if ( config.getIdContactAttribute( ) != null )
            {
                model.put( MARK_CONFIG_CONTACT_ATTRIBUTE, config.getIdContactAttribute( ) );
            }

            if ( config.getDefaultSubject( ) != null )
            {
                model.put( MARK_CONFIG_DEFAULT_SUBJECT, config.getDefaultSubject( ) );
            }

        }
        else
        {
            model.put( MARK_MESSAGE_DIRECTION, MessageDirectionExternalUser.AGENT_TO_EXTERNAL_USER );
        }

        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_TICKET_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {

        Integer nMessageDirectionId = getParameterAsInteger( request.getParameter( PARAMETER_MESSAGE_DIRECTION ) );
        Integer nIdFollowingAction = getParameterAsInteger( request.getParameter( PARAMETER_FOLLOW_ACTION_ID ) );
        Integer nIdContactAttribute = getParameterAsInteger( request.getParameter( PARAMETER_CONTACT_ATTRIBUTE ) );
        String strDefaultSubject = request.getParameter( PARAMETER_DEFAULT_SUBJECT );

        TaskNotifyWaitingTicketConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskNotifyWaitingTicketConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setMessageDirectionExternalUser( MessageDirectionExternalUser.valueOf( nMessageDirectionId ) );
        config.setIdFollowingAction( nIdFollowingAction );
        config.setIdContactAttribute( nIdContactAttribute );
        config.setDefaultSubject( strDefaultSubject );

        String strJspError = this.validateConfig( config, request );

        if ( StringUtils.isNotBlank( strJspError ) )
        {
            return strJspError;
        }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        Set<ConstraintViolation<ITaskConfig>> setConstraintErrors = BeanValidationUtil.validate( config );

        if ( !setConstraintErrors.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EMPTY_FIELD, AdminMessage.TYPE_ERROR );
        }

        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        int idTicket = resourceHistory.getIdResource( );

        TicketEmailExternalUserMessage lastEmailsAgentDemand = _ticketEmailExternalUserDemandDAO.loadLastByIdTicket( idTicket );
        String strEmailRecipients = lastEmailsAgentDemand.getEmailRecipients( );
        List<ExternalUser> listUsers = getExternalUsers( strEmailRecipients );

        String strEmailRecipientsCc = lastEmailsAgentDemand.getEmailRecipientsCc( );


        Map<String, Object> model = new HashMap<>( );

        String messageQuestion = lastEmailsAgentDemand.getMessageQuestion();

        model.put( MARK_TICKETING_MESSAGE, TicketingConstants.MESSAGE_MARK + messageQuestion );
        model.put( MARK_TICKETING_LIST_EMAIL_INFOS, listUsers );
        model.put( MARK_TICKETING_EMAIL_INFOS_CC, strEmailRecipientsCc );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_TICKET_INFORMATION, locale, model );

        return template.getHtml( );
    }

    private List<ExternalUser> getExternalUsers( String strEmailRecipients ) {
        List<ExternalUser> listExternalUsers = new ArrayList<>(  );

        if (strEmailRecipients!=null)
        {
            for ( String email : strEmailRecipients.split( ";" ) )
            {
                List<ExternalUser> listUsers = _externalUserDAO.findExternalUser( null, email, null, null, null );
                if ( listUsers != null && !listUsers.isEmpty() )
                {
                    listExternalUsers.add( listUsers.get( 0 ) );
                }
            }
        }

        return listExternalUsers;
    }



    /**
     *
     * @param strParameter parameter
     * @return the parameter value parsed as Integer
     */
    private Integer getParameterAsInteger( String strParameter )
    {
        if ( StringUtils.isNotBlank( strParameter ) && StringUtils.isNumeric( strParameter ) )
        {
            return Integer.parseInt( strParameter );
        }
        return null;
    }
}