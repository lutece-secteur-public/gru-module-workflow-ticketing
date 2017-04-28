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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
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

/**
 * Class for the EmailExternalUser task
 */
public class TaskTicketEmailExternalUser extends SimpleTask
{
    // Beans
    public static final String BEAN_TICKET_CONFIG_SERVICE = "workflow-ticketing.taskTicketEmailExternalUserConfigService";

    // Parameters
    public static final String PARAMETER_MESSAGE = "message";
    public static final String PARAMETER_EMAIL_RECIPIENTS = "email_recipients";
    public static final String PARAMETER_EMAIL_RECIPIENTS_CC = "email_recipients_cc";

    // Other constants
    public static final String UNDERSCORE = "_";
    public static final String SEMICOLON = ";";

    // Messages
    private static final String MESSAGE_TICKET = "module.workflow.ticketing.task_ticket_email_external_user.label";

    @Inject
    @Named( BEAN_TICKET_CONFIG_SERVICE )
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
    private ITicketEmailExternalUserMessageDAO _ticketEmailExternalUserDemandDAO;

    private IExternalUserDAO _externalUserDAO = SpringContextService.getBean( IExternalUserDAO.BEAN_SERVICE );

    /** The _resource history service. */
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Ticket ticket = TicketHome.findByPrimaryKey( resourceHistory.getIdResource( ) );

        TaskTicketEmailExternalUserConfig config = _taskTicketConfigService.findByPrimaryKey( getId( ) );

        if ( config != null )
        {
            MessageDirectionExternalUser messageDirectionExternalUser = config.getMessageDirectionExternalUser( );

            if ( messageDirectionExternalUser == MessageDirectionExternalUser.AGENT_TO_EXTERNAL_USER )
            {
                processAgentTask( nIdResourceHistory, ticket, request, locale, config );
            }
            else
                if ( messageDirectionExternalUser == MessageDirectionExternalUser.EXTERNAL_USER_TO_AGENT )
                {
                    processExternalUserTask( nIdResourceHistory, ticket, request, locale, config );
                }
                else
                {
                    processAgentRecontactTask( nIdResourceHistory, ticket, request, locale, config );
                }
        }
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
     * @param locale
     *            current Locale
     * @param config
     *            configuration of the current task
     */
    private void processAgentTask( int nIdResourceHistory, Ticket ticket, HttpServletRequest request, Locale locale, TaskTicketEmailExternalUserConfig config )
    {
        String strAgentMessage = request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + getId( ) );
        String strEmailRecipients = request.getParameter( PARAMETER_EMAIL_RECIPIENTS + UNDERSCORE + getId( ) );
        String strEmailRecipientsCc = request.getParameter( PARAMETER_EMAIL_RECIPIENTS_CC + UNDERSCORE + getId( ) );

        // Create message item
        TicketEmailExternalUserMessage emailExternalUserMessage = new TicketEmailExternalUserMessage( );
        emailExternalUserMessage.setIdTicket( ticket.getId( ) );
        emailExternalUserMessage.setMessageQuestion( strAgentMessage );
        emailExternalUserMessage.setEmailRecipients( strEmailRecipients );
        emailExternalUserMessage.setEmailRecipientsCc( strEmailRecipientsCc );
        _ticketEmailExternalUserDemandDAO.createQuestion( emailExternalUserMessage );

        // Create resource item
        TicketEmailExternalUserHistory emailExternalUserHistory = new TicketEmailExternalUserHistory( );
        emailExternalUserHistory.setIdResourceHistory( nIdResourceHistory );
        emailExternalUserHistory.setIdTask( getId( ) );
        emailExternalUserHistory.setIdMessageExternalUser( emailExternalUserMessage.getIdMessageExternalUser( ) );
        _ticketEmailExternalUserHistoryDAO.insert( emailExternalUserHistory );

        // Create resource infos item
        String [ ] emailRecipients = null;
        if ( strEmailRecipients != null && !strEmailRecipients.isEmpty( ) )
        {
            emailRecipients = strEmailRecipients.split( SEMICOLON );

            for ( int i = 0; i < emailRecipients.length; i++ )
            {
                AdminUser user = AdminUserHome.findUserByLogin( AdminUserHome.findUserByEmail( emailRecipients [i] ) );

                TicketEmailExternalUserRecipient infosEmailExternalUser = new TicketEmailExternalUserRecipient( );
                infosEmailExternalUser.setIdResourceHistory( nIdResourceHistory );
                infosEmailExternalUser.setIdTask( getId( ) );
                infosEmailExternalUser.setEmail( user.getEmail( ) );

                List<ExternalUser> listUsers = _externalUserDAO.findExternalUser( user.getLastName( ), user.getEmail( ), null );

                if ( listUsers != null && listUsers.size( ) > 0 )
                {
                    infosEmailExternalUser.setField( listUsers.iterator( ).next( ).getEntite( ) );
                }

                infosEmailExternalUser.setName( user.getLastName( ) );
                infosEmailExternalUser.setFirstName( user.getFirstName( ) );
                _ticketEmailExternalUserRecipientDAO.insert( infosEmailExternalUser );
            }
        }

        String [ ] emailRecipientsCc = null;
        if ( strEmailRecipientsCc != null && !strEmailRecipientsCc.isEmpty( ) )
        {
            emailRecipientsCc = strEmailRecipientsCc.split( SEMICOLON );
            for ( int i = 0; i < emailRecipientsCc.length; i++ )
            {
                TicketEmailExternalUserCc infosEmailExternalUser = new TicketEmailExternalUserCc( );
                infosEmailExternalUser.setIdResourceHistory( nIdResourceHistory );
                infosEmailExternalUser.setIdTask( getId( ) );
                infosEmailExternalUser.setEmail( emailRecipientsCc [i] );
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
     * @param locale
     *            current Locale
     * @param config
     *            configuration of the current task
     */
    private void processAgentRecontactTask( int nIdResourceHistory, Ticket ticket, HttpServletRequest request, Locale locale,
            TaskTicketEmailExternalUserConfig config )
    {
        String strAgentMessage = request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + getId( ) );
        TicketEmailExternalUserMessage firstEmailsAgentDemand = _ticketEmailExternalUserDemandDAO.loadFirstByIdTicket( ticket.getId( ) );

        String strEmailRecipients = firstEmailsAgentDemand.getEmailRecipients( );
        String strEmailRecipientsCc = firstEmailsAgentDemand.getEmailRecipientsCc( );

        // Create message item
        TicketEmailExternalUserMessage emailExternalUser = new TicketEmailExternalUserMessage( );
        emailExternalUser.setIdTicket( ticket.getId( ) );
        emailExternalUser.setMessageQuestion( strAgentMessage );
        emailExternalUser.setEmailRecipients( strEmailRecipients );
        emailExternalUser.setEmailRecipientsCc( strEmailRecipientsCc );
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
     * @param locale
     *            current Locale
     * @param config
     *            configuration of the current task
     */
    private void processExternalUserTask( int nIdResourceHistory, Ticket ticket, HttpServletRequest request, Locale locale,
            TaskTicketEmailExternalUserConfig config )
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
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TICKET, locale );
    }
}
