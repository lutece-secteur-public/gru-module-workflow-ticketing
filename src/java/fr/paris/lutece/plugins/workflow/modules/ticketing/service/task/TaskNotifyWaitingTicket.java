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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.NotifyGruMarker;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskNotifyWaitingTicketConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.TicketEmailExternalUserMessage;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.provider.TicketEmailExternalUserProviderManager;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;

public class TaskNotifyWaitingTicket extends AbstractTicketingTask
{
    // Beans
    private static final String BEAN_TICKET_CONFIG_SERVICE = "workflow-ticketing.taskNotifyWaitingTicketConfigService";

    // Messages
    private static final String MESSAGE_NOTIFY_WAITING_TICKET_LABEL = "module.workflow.ticketing.task_notify_waiting_ticket.labelNotifyWaitingTicket";
    private static final String MESSAGE_MAILING_ADDRESS = "module.workflow.ticketing.task_notify_waiting_ticket.mailing.address";
    private static final String MESSAGE_MAILING_CONTENT = "module.workflow.ticketing.task_notify_waiting_ticket.mailing.content";
    private static final String MESSAGE_MAILING_ADDRESS_LABEL = "module.workflow.ticketing.task_notify_waiting_ticket.mailing.email_recipient";

    @Inject
    @Named( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE )
    private ITicketEmailExternalUserMessageDAO _ticketEmailExternalUserDemandDAO;


    @Inject
    @Named( BEAN_TICKET_CONFIG_SERVICE )
    private ITaskConfigService _taskTicketConfigService;

    @Inject
    @Named( "workflow-ticketing.externaluser.provider-manager" )
    private TicketEmailExternalUserProviderManager _ticketEmailExternalUserProviderManager;



    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {


        String strTaskInformation = StringUtils.EMPTY;
        Ticket ticket = getTicket( nIdResourceHistory );


        // get configuration
        TaskNotifyWaitingTicketConfig config = _taskTicketConfigService.findByPrimaryKey( getId( ) );

        if ( ticket != null && config != null )
        {
            String strProviderId = "ticketEmailExternalUserProviderManager";
            ResourceHistory resourceHistory = new ResourceHistory( );
            resourceHistory.setIdResource( ticket.getId() );
            IProvider provider = _ticketEmailExternalUserProviderManager.createProvider( strProviderId, resourceHistory, request );
            Collection<NotifyGruMarker> markerValues = provider.provideMarkerValues( );

            String subject = config.getSubject( );
            String message = config.getMessage( );

            for ( NotifyGruMarker marker : markerValues )
            {
                String markerKey = marker.getMarker( );
                if ( markerKey != null )
                {
                    String markerValue = marker.getValue( );
                    String value = markerValue != null ? markerValue : StringUtils.EMPTY;
                    subject = subject.replace( "${" + markerKey + "}", value );
                    subject = subject.replace( "${" + markerKey + "!}", !StringUtils.EMPTY.equals( value ) ? value : StringUtils.EMPTY );
                    message = message.replace( "${" + markerKey + "}", value );
                    message = message.replace( "${" + markerKey + "!}", !StringUtils.EMPTY.equals( value ) ? value : StringUtils.EMPTY );
                }
            }
            subject = subject.replaceAll( "\\$\\{.*\\}", StringUtils.EMPTY );
            message = message.replaceAll( "\\$\\{.*\\}", StringUtils.EMPTY );

            TicketEmailExternalUserMessage lastEmailsAgentDemand = _ticketEmailExternalUserDemandDAO.loadLastByIdTicket( ticket.getId( ) );
            String strEmailRecipients = lastEmailsAgentDemand.getEmailRecipients( );
            // envoi notification
            MailService.sendMailHtml( strEmailRecipients, config.getSenderName(), MailService.getNoReplyEmail(), subject, message );


            // gestion historique
            // adresse d'envoi
            strTaskInformation = MESSAGE_MAILING_ADDRESS_LABEL;
            strTaskInformation += MessageFormat.format(
                    I18nService.getLocalizedString( MESSAGE_MAILING_ADDRESS, locale ),
                    strEmailRecipients );
            // message initial
            strTaskInformation += MessageFormat.format(
                    I18nService.getLocalizedString( MESSAGE_MAILING_CONTENT, locale ),
                    message );
        }
        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_NOTIFY_WAITING_TICKET_LABEL, locale );
    }
}
