/*
 * Copyright (c) 2002-2023, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskNotifyWaitingTicketConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc.ITicketEmailExternalUserCcDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc.TicketEmailExternalUserCc;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config.MessageDirectionExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.ITicketEmailExternalUserHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.history.TicketEmailExternalUserHistory;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.TicketEmailExternalUserMessage;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.recipient.ITicketEmailExternalUserRecipientDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.recipient.TicketEmailExternalUserRecipient;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.ExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.IExternalUserDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class TaskNotifyWaitingTicket extends SimpleTask
{
    // Beans
    private static final String BEAN_TICKET_CONFIG_SERVICE = "workflow-ticketing.taskNotifyWaitingTicketConfigService";

    // Messages
    private static final String MESSAGE_NOTIFY_WAITING_TICKET_LABEL = "module.workflow.ticketing.task_notify_waiting_ticket.labelNotifyWaitingTicket";

    // Parameters
    public static final String PARAMETER_MESSAGE = "message";
    public static final String PARAMETER_EMAIL_RECIPIENTS = "email_recipients";
    public static final String PARAMETER_EMAIL_SUBJECT = "email_subject";
    public static final String PARAMETER_EMAIL_RECIPIENTS_CC = "email_recipients_cc";
    public static final String PARAM_NEXT_ACTION_ID = "next_action_id";

    // Other constants
    public static final String UNDERSCORE = "_";
    public static final String SEMICOLON = ";";

    @Inject
    @Named( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserMessageDAO _ticketEmailExternalUserDemandDAO;

    @Inject
    @Named( BEAN_TICKET_CONFIG_SERVICE )
    private ITaskConfigService _taskTicketConfigService;

    /** The _resource history service. */
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    @Inject
    @Named( ITicketEmailExternalUserHistoryDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserHistoryDAO _ticketEmailExternalUserHistoryDAO;

    @Inject
    @Named( ITicketEmailExternalUserRecipientDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserRecipientDAO _ticketEmailExternalUserRecipientDAO;

    @Inject
    @Named( ITicketEmailExternalUserCcDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserCcDAO _ticketEmailExternalUserCcDAO;

    private IExternalUserDAO _externalUserDAO = SpringContextService.getBean( IExternalUserDAO.BEAN_SERVICE );

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Ticket ticket = TicketHome.findByPrimaryKey( resourceHistory.getIdResource( ) );

        TaskNotifyWaitingTicketConfig config = _taskTicketConfigService.findByPrimaryKey( getId( ) );

        if ( config != null )
        {
            MessageDirectionExternalUser messageDirectionExternalUser = config.getMessageDirectionExternalUser( );

            if ( messageDirectionExternalUser == MessageDirectionExternalUser.AGENT_TO_EXTERNAL_USER )
            {
                processAgentTask( nIdResourceHistory, ticket, request, config );
            }
            else
                if ( messageDirectionExternalUser == MessageDirectionExternalUser.EXTERNAL_USER_TO_AGENT )
                {
                    processExternalUserTask( nIdResourceHistory, ticket, request );
                }
                else
                {
                    processAgentRecontactTask( nIdResourceHistory, ticket, request );
                }
        }
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_NOTIFY_WAITING_TICKET_LABEL, locale );
    }

    /**
     * Process agent to external user task
     *
     * @param nIdResourceHistory
     *            resourceHistory ID
     * @param ticket
     *            the current ticket
     * @param request
     *            HttpRequest from doAction
     * @param config
     *            configuration of the current task
     */
    private void processAgentTask( int nIdResourceHistory, Ticket ticket, HttpServletRequest request, TaskNotifyWaitingTicketConfig config )
    {
        String strAgentMessage = StringUtils.EMPTY;
        String strEmailRecipients = StringUtils.EMPTY;
        String strEmailRecipientsCc = StringUtils.EMPTY;
        String strSubject = StringUtils.EMPTY;

        if ( request != null )
        {
            strAgentMessage = request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + getId( ) );
            strEmailRecipients = request.getParameter( PARAMETER_EMAIL_RECIPIENTS + UNDERSCORE + getId( ) );
            strEmailRecipientsCc = request.getParameter( PARAMETER_EMAIL_RECIPIENTS_CC + UNDERSCORE + getId( ) );
            strSubject = request.getParameter( PARAMETER_EMAIL_SUBJECT + UNDERSCORE + getId( ) );
        }
        else
        {
            // cas daemon
            throw new UnsupportedOperationException( );
        }

        // Create message item
        TicketEmailExternalUserMessage emailExternalUserMessage = new TicketEmailExternalUserMessage( );
        emailExternalUserMessage.setIdTicket( ticket.getId( ) );
        emailExternalUserMessage.setMessageQuestion( strAgentMessage );
        emailExternalUserMessage.setEmailRecipients( strEmailRecipients );
        emailExternalUserMessage.setEmailRecipientsCc( strEmailRecipientsCc );
        emailExternalUserMessage.setEmailSubject( strSubject );
        _ticketEmailExternalUserDemandDAO.createQuestion( emailExternalUserMessage );

        // Create resource item
        TicketEmailExternalUserHistory emailExternalUserHistory = new TicketEmailExternalUserHistory( );
        emailExternalUserHistory.setIdResourceHistory( nIdResourceHistory );
        emailExternalUserHistory.setIdTask( getId( ) );
        emailExternalUserHistory.setIdMessageExternalUser( emailExternalUserMessage.getIdMessageExternalUser( ) );
        _ticketEmailExternalUserHistoryDAO.insert( emailExternalUserHistory );

        // Create resource infos item
        String [ ] emailRecipients;
        if ( strEmailRecipients != null && !strEmailRecipients.isEmpty( ) )
        {
            emailRecipients = strEmailRecipients.split( SEMICOLON );

            for ( String emailRecipient : emailRecipients )
            {
                AdminUser user = AdminUserHome.findUserByLogin( AdminUserHome.findUserByEmail( emailRecipient ) );

                TicketEmailExternalUserRecipient infosEmailExternalUser = new TicketEmailExternalUserRecipient( );
                infosEmailExternalUser.setIdResourceHistory( nIdResourceHistory );
                infosEmailExternalUser.setIdTask( getId( ) );
                infosEmailExternalUser.setEmail( user.getEmail( ) );

                List<ExternalUser> listUsers = _externalUserDAO.findExternalUser( user.getLastName( ), user.getEmail( ),
                        String.valueOf( config.getIdContactAttribute( ) ), null, null );

                if ( listUsers != null && !listUsers.isEmpty( ) )
                {
                    infosEmailExternalUser.setField( listUsers.iterator( ).next( ).getAdditionalAttribute( ) );
                }

                infosEmailExternalUser.setName( user.getLastName( ) );
                infosEmailExternalUser.setFirstName( user.getFirstName( ) );
                _ticketEmailExternalUserRecipientDAO.insert( infosEmailExternalUser );
            }
        }

        String [ ] emailRecipientsCc;
        if ( strEmailRecipientsCc != null && !strEmailRecipientsCc.isEmpty( ) )
        {
            emailRecipientsCc = strEmailRecipientsCc.split( SEMICOLON );
            for ( String recipientCc : emailRecipientsCc )
            {
                TicketEmailExternalUserCc infosEmailExternalUser = new TicketEmailExternalUserCc( );
                infosEmailExternalUser.setIdResourceHistory( nIdResourceHistory );
                infosEmailExternalUser.setIdTask( getId( ) );
                infosEmailExternalUser.setEmail( recipientCc );
                _ticketEmailExternalUserCcDAO.insert( infosEmailExternalUser );
            }
        }
    }

    /**
     * Process agent to external user task (recontact)
     *
     * @param nIdResourceHistory
     *            resourceHistory ID
     * @param ticket
     *            the current ticket
     * @param request
     *            HttpRequest from doAction
     */
    private void processAgentRecontactTask( int nIdResourceHistory, Ticket ticket, HttpServletRequest request )
    {
        TicketEmailExternalUserMessage firstEmailsAgentDemand = _ticketEmailExternalUserDemandDAO.loadLastByIdTicket( ticket.getId( ) );

        String strAgentMessage = StringUtils.EMPTY;
        if ( request != null && request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + getId( ) ) != null )
        {
            strAgentMessage = request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + getId( ) );
        }

        if ( StringUtils.EMPTY.equals( strAgentMessage ) )
        {
            // cas daemon
            strAgentMessage = firstEmailsAgentDemand.getMessageQuestion( );
        }

        String strEmailRecipients = firstEmailsAgentDemand.getEmailRecipients( );
        String strEmailRecipientsCc = firstEmailsAgentDemand.getEmailRecipientsCc( );
        String strSubject = firstEmailsAgentDemand.getEmailSubject( );

        // Create message item
        TicketEmailExternalUserMessage emailExternalUser = new TicketEmailExternalUserMessage( );
        emailExternalUser.setIdTicket( ticket.getId( ) );
        emailExternalUser.setMessageQuestion( strAgentMessage );
        emailExternalUser.setEmailRecipients( strEmailRecipients );
        emailExternalUser.setEmailRecipientsCc( strEmailRecipientsCc );
        emailExternalUser.setEmailSubject( strSubject );
        _ticketEmailExternalUserDemandDAO.createQuestion( emailExternalUser );

        // Create resource item
        TicketEmailExternalUserHistory emailExternalUserHistory = new TicketEmailExternalUserHistory( );
        emailExternalUserHistory.setIdResourceHistory( nIdResourceHistory );
        emailExternalUserHistory.setIdTask( getId( ) );
        emailExternalUserHistory.setIdMessageExternalUser( emailExternalUser.getIdMessageExternalUser( ) );
        _ticketEmailExternalUserHistoryDAO.insert( emailExternalUserHistory );
    }

    /**
     * Process external user to agent task (response)
     *
     * @param nIdResourceHistory
     *            resourceHistory ID
     * @param ticket
     *            the current ticket
     * @param request
     *            HttpRequest from doAction
     */
    private void processExternalUserTask( int nIdResourceHistory, Ticket ticket, HttpServletRequest request )
    {
        String strAgentMessage = request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + getId( ) );

        // Create demand item
        int nIdMessageExternalUser = _ticketEmailExternalUserDemandDAO.addAnswer( ticket.getId( ), strAgentMessage );

        // Close all messages for this ticket
        _ticketEmailExternalUserDemandDAO.closeMessagesByIdTicket( ticket.getId( ) );

        // Create resource item
        TicketEmailExternalUserHistory emailExternalUserHistory = new TicketEmailExternalUserHistory( );
        emailExternalUserHistory.setIdResourceHistory( nIdResourceHistory );
        emailExternalUserHistory.setIdTask( getId( ) );
        emailExternalUserHistory.setIdMessageExternalUser( nIdMessageExternalUser );
        _ticketEmailExternalUserHistoryDAO.insert( emailExternalUserHistory );

        // No email for this process
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        super.doRemoveTaskInformation( nIdHistory );

        TicketEmailExternalUserHistory ticketEmailExternalUserHistory = _ticketEmailExternalUserHistoryDAO.loadByIdHistory( nIdHistory );
        if ( ticketEmailExternalUserHistory != null )
        {
            _ticketEmailExternalUserDemandDAO.deleteByIdMessageExternalUser( ticketEmailExternalUserHistory.getIdMessageExternalUser( ) );
        }
        _ticketEmailExternalUserHistoryDAO.deleteByHistory( nIdHistory );
        _ticketEmailExternalUserCcDAO.deleteByIdHistory( nIdHistory );
        _ticketEmailExternalUserRecipientDAO.deleteByIdHistory( nIdHistory );
    }
}
