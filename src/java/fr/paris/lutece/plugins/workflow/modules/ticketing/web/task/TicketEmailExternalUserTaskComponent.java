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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc.ITicketEmailExternalUserCcDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc.TicketEmailExternalUserCc;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config.MessageDirectionExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config.TaskTicketEmailExternalUserConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.ITicketEmailExternalUserHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.TicketEmailExternalUserHistory;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.TicketEmailExternalUserMessage;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.recipient.ITicketEmailExternalUserRecipientDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.recipient.TicketEmailExternalUserRecipient;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.IExternalUserDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskTicketEmailExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.WorkflowTicketingUtils;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.web.task.TaskComponent;
import fr.paris.lutece.portal.business.user.attribute.IAttribute;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.user.attribute.AttributeService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Class for the TicketEmailExternalUser task
 */
/**
 * @author a120274
 *
 */
public class TicketEmailExternalUserTaskComponent extends TaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_TICKET_CONFIG = "admin/plugins/workflow/modules/ticketing/external_user/task_ticket_email_external_user_config.html";
    private static final String TEMPLATE_TASK_TICKET_FORM = "admin/plugins/workflow/modules/ticketing/external_user/task_ticket_email_external_user_form.html";
    private static final String TEMPLATE_TASK_TICKET_INFORMATION = "admin/plugins/workflow/modules/ticketing/external_user/task_ticket_email_external_user_informations.html";

    // Marks
    private static final String MARK_CONFIG = "config";
    private static final String MARK_CONFIG_FOLLOW_ACTION_ID = "following_action_id";
    private static final String MARK_TICKETING_MESSAGE = "external_user_message";
    private static final String MARK_TICKETING_EMAIL_INFO_CC = "email_infos_cc";
    private static final String MARK_TICKETING_LIST_EMAIL_INFOS = "list_email_infos";
    private static final String MARK_MESSAGE_DIRECTIONS_LIST = "message_directions_list";
    private static final String MARK_MESSAGE_DIRECTION = "message_direction";
    private static final String MARK_CONFIG_CONTACT_ATTRIBUTE = "contact_attribute_id";
    private static final String MARK_CONFIG_LABEL_ATTRIBUTE = "label_contact_attribute";

    // Parameters config
    private static final String PARAMETER_MESSAGE_DIRECTION = "message_direction";
    private static final String PARAMETER_FOLLOW_ACTION_ID = "following_action_id";
    private static final String PARAMETER_CONTACT_ATTRIBUTE = "contact_attribute_id";

    // Error message
    public static final String MESSAGE_EMPTY_EMAIL = "module.workflow.ticketing.task_ticket_email_external_user.error.email.empty";
    public static final String MESSAGE_INVALID_EMAIL_OR_NOT_AUTHORIZED = "module.workflow.ticketing.task_ticket_email_external_user.error.email.invalid.not_authorized";
    public static final String MESSAGE_INVALID_EMAIL = "module.workflow.ticketing.task_ticket_email_external_user.error.email.invalid";
    private static final String MESSAGE_ALREADY_ANSWER = "module.workflow.ticketing.externalUserResponse.message.already_answer";
    private static final String MESSAGE_EMPTY_FIELD = "module.workflow.ticketing.task_ticket_email_external_user.error.field.empty";

    // Constant
    private static final String DISPLAY_SEMICOLON = " ; ";

    private static final AttributeService _attributeService = AttributeService.getInstance( );

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
    @Named( IExternalUserDAO.BEAN_SERVICE )
    private IExternalUserDAO _ExternalUserDAO;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        TicketEmailExternalUserHistory emailExternalUserHistory = _ticketEmailExternalUserHistoryDAO.loadByIdHistory( nIdHistory );
        TicketEmailExternalUserMessage mailExternalUserMessage = _ticketEmailExternalUserMessageDAO.loadByIdMessageExternalUser( emailExternalUserHistory
                .getIdMessageExternalUser( ) );
        TaskTicketEmailExternalUserConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        Map<String, Object> model = new HashMap<String, Object>( );

        if ( config.getMessageDirectionExternalUser( ) == MessageDirectionExternalUser.AGENT_TO_EXTERNAL_USER )
        {
            model.put( MARK_TICKETING_MESSAGE, mailExternalUserMessage.getMessageQuestion( ) );
            List<TicketEmailExternalUserRecipient> listRecipientEmailExternalUser = _ticketEmailExternalUserRecipientDAO.loadByIdHistory( nIdHistory,
                    task.getId( ) );
            List<TicketEmailExternalUserCc> listCcEmailExternalUser = _ticketEmailExternalUserCcDAO.loadByIdHistory( nIdHistory, task.getId( ) );

            StringBuilder sbInfosCc = new StringBuilder( );

            for ( TicketEmailExternalUserCc emailExternalUserCc : listCcEmailExternalUser )
            {
                sbInfosCc.append( emailExternalUserCc.getEmail( ) ).append( DISPLAY_SEMICOLON );
            }

            if ( sbInfosCc != null && sbInfosCc.length( ) > 0 )
            {
                sbInfosCc.setLength( sbInfosCc.length( ) - 3 );
            }

            model.put( MARK_TICKETING_LIST_EMAIL_INFOS, listRecipientEmailExternalUser );
            model.put( MARK_TICKETING_EMAIL_INFO_CC, sbInfosCc.toString( ) );
        }
        else
            if ( config.getMessageDirectionExternalUser( ) == MessageDirectionExternalUser.RE_AGENT_TO_EXTERNAL_USER )
            {
                model.put( MARK_TICKETING_MESSAGE, mailExternalUserMessage.getMessageQuestion( ) );
            }
            else
            {
                model.put( MARK_TICKETING_MESSAGE, mailExternalUserMessage.getMessageResponse( ) );
            }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_TICKET_INFORMATION, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        TaskTicketEmailExternalUserConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_CONFIG, config );

        String strLabelContactAttribute = StringUtils.EMPTY;
        if ( config.getIdContactAttribute( ) != null )
        {
            IAttribute attribute = _attributeService.getAttributeWithFields( config.getIdContactAttribute( ), locale );
            if ( attribute != null )
            {
                strLabelContactAttribute = attribute.getTitle( );
            }
        }

        model.put( MARK_CONFIG_LABEL_ATTRIBUTE, strLabelContactAttribute );

        ModelUtils.storeRichText( request, model );
        ModelUtils.storeUserSignature( request, model );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_TICKET_FORM, locale, model );

        return template.getHtml( );
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
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        TaskTicketEmailExternalUserConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        String strNextActionId = String.valueOf( config.getIdFollowingAction( ) );
        String strEmailRecipients = request.getParameter( TaskTicketEmailExternalUser.PARAMETER_EMAIL_RECIPIENTS + TaskTicketEmailExternalUser.UNDERSCORE
                + task.getId( ) );
        String strEmailRecipientsCc = request.getParameter( TaskTicketEmailExternalUser.PARAMETER_EMAIL_RECIPIENTS_CC + TaskTicketEmailExternalUser.UNDERSCORE
                + task.getId( ) );

        String strError = null;
        int nLevelError = -1;
        Object [ ] errorParams = new Object [ 1];

        if ( config.getMessageDirectionExternalUser( ) == MessageDirectionExternalUser.AGENT_TO_EXTERNAL_USER )
        {
            if ( StringUtils.isEmpty( strEmailRecipients ) )
            {
                strError = MESSAGE_EMPTY_EMAIL;
                nLevelError = AdminMessage.TYPE_STOP;
            }
            else
            {
                List<String> listErrorRecipients = WorkflowTicketingUtils.validEmailList( strEmailRecipients, _ExternalUserDAO, strNextActionId );
                if ( !listErrorRecipients.isEmpty( ) )
                {
                    strError = listErrorRecipients.get( 0 );
                    nLevelError = AdminMessage.TYPE_STOP;

                    if ( listErrorRecipients.size( ) > 1 )
                    {
                        errorParams = listErrorRecipients.subList( 1, listErrorRecipients.size( ) ).toArray( );
                    }
                }
            }

            if ( strError == null && StringUtils.isNotEmpty( strEmailRecipientsCc ) )
            {
                List<String> listErrorRecipientsCc = WorkflowTicketingUtils.validEmailList( strEmailRecipientsCc, null, null );
                if ( !listErrorRecipientsCc.isEmpty( ) )
                {
                    strError = listErrorRecipientsCc.get( 0 );
                    nLevelError = AdminMessage.TYPE_STOP;
                    if ( listErrorRecipientsCc.size( ) > 1 )
                    {
                        errorParams = listErrorRecipientsCc.subList( 1, listErrorRecipientsCc.size( ) ).toArray( );
                    }
                }
            }
        }

        if ( ( config.getMessageDirectionExternalUser( ) == MessageDirectionExternalUser.EXTERNAL_USER_TO_AGENT ) )
        {
            TicketEmailExternalUserMessage lastExternalUserMessage = _ticketEmailExternalUserMessageDAO.loadLastByIdTicket( nIdResource );

            if ( ( lastExternalUserMessage == null ) || ( lastExternalUserMessage.getIsAnswered( ) )
                    || ( !_ticketEmailExternalUserMessageDAO.isLastQuestion( nIdResource, lastExternalUserMessage.getIdMessageExternalUser( ) ) ) )
            {
                strError = MESSAGE_ALREADY_ANSWER;
                nLevelError = AdminMessage.TYPE_WARNING;
            }
        }

        if ( ( strError != null ) && ( nLevelError >= 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, strError, errorParams, nLevelError );
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        TaskTicketEmailExternalUserConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        ReferenceList listMessageDirections = MessageDirectionExternalUser.getReferenceList( locale );

        model.put( MARK_MESSAGE_DIRECTIONS_LIST, listMessageDirections );
        model.put( MARK_CONFIG_FOLLOW_ACTION_ID, StringUtils.EMPTY );
        model.put( MARK_CONFIG_CONTACT_ATTRIBUTE, StringUtils.EMPTY );

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

        TaskTicketEmailExternalUserConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        Boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskTicketEmailExternalUserConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setMessageDirectionExternalUser( MessageDirectionExternalUser.valueOf( nMessageDirectionId ) );
        config.setIdFollowingAction( nIdFollowingAction );
        config.setIdContactAttribute( nIdContactAttribute );

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
     * 
     * @param strParameter
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
