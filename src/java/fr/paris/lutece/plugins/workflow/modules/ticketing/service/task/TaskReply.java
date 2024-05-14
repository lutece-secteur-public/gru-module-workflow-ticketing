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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.MessageDirection;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskReplyConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a task to reply to a ticket
 *
 */
public class TaskReply extends AbstractTicketingTask
{
    private static final String MESSAGE_REPLY = "module.workflow.ticketing.task_reply.labelReply";
    private static final String MESSAGE_REPLY_INFORMATION_PREFIX = "module.workflow.ticketing.task_reply.information.";
    private static final String MESSAGE_REPLY_INFORMATION_NO_MESSAGE = "module.workflow.ticketing.task_reply.information.";

    // MARKERS
    private static final String MARK_USER_MESSAGE = "user_message";
    private static final String MARK_MAP_DOWNLOAD_URL = "map_download_url";

    // PARAMETERS
    public static final String PARAMETER_USER_MESSAGE = "user_message";
    private ITaskConfigService _taskConfigService;

    // TEMPLATES
    private static final String TEMPLATE_USER_MESSAGE = "admin/plugins/workflow/modules/ticketing/user_message.html";

    @Inject
    private TicketFormService _ticketFormService;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_REPLY, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            String strUserMessage = request.getParameter( PARAMETER_USER_MESSAGE );

            strUserMessage = fillWithDownloadUrls( strUserMessage, request );

            ticket.setUserMessage( strUserMessage );

            TaskReplyConfig config = _taskConfigService.findByPrimaryKey( getId( ) );

            if ( ( MessageDirection.AGENT_TO_USER == config.getMessageDirection( ) ) && config.isCloseTicket( ) )
            {
                ticket.setTicketStatus( TicketingConstants.TICKET_STATUS_CLOSED );
                ticket.setDateClose( new Timestamp( new java.util.Date( ).getTime( ) ) );
                request.setAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION, REDIRECT_TO_LIST );
            }

            TicketHome.update( ticket );

            if ( StringUtils.isEmpty( strUserMessage ) )
            {
                strUserMessage = I18nService.getLocalizedString( MESSAGE_REPLY_INFORMATION_NO_MESSAGE, Locale.FRENCH );
            }

            strTaskInformation = MessageFormat.format( I18nService
                    .getLocalizedString( MESSAGE_REPLY_INFORMATION_PREFIX + config.getMessageDirection( ).toString( ).toLowerCase( ), Locale.FRENCH ),
                    TicketingConstants.MESSAGE_MARK + strUserMessage );

        }

        return strTaskInformation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskConfigService.remove( getId( ) );
        super.doRemoveConfig( );
    }

    /**
     * Gives the task config service
     *
     * @return the task config service
     */
    public ITaskConfigService getTaskConfigService( )
    {
        return _taskConfigService;
    }

    /**
     * Sets the task config service
     *
     * @param taskConfigService
     *            the task config service
     */
    public void setTaskConfigService( ITaskConfigService taskConfigService )
    {
        _taskConfigService = taskConfigService;
    }

    /**
     * Fill the user message with the download url links ( Template computed with AppTemplateService )
     *
     * @param strUserMessage
     *            The user message
     * @param request
     *            The request
     * @return the completed user message
     */
    private String fillWithDownloadUrls( String strUserMessage, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_USER_MESSAGE, strUserMessage );

        // Save the files in core_phy_file
        Ticket ticket = new Ticket( );
        // Get the id entry for reply attments files from properties
        int nIdEntryReplyAttachedFiles = AppPropertiesService.getPropertyInt( TicketingConstants.PROPERTY_ENTRY_REPLY_ATTACHMENTS_ID,
                TicketingConstants.PROPERTY_UNSET_INT );

        Entry entry = EntryHome.findByPrimaryKey( nIdEntryReplyAttachedFiles );
        _ticketFormService.getResponseEntry( request, nIdEntryReplyAttachedFiles, request.getLocale( ), ticket );

        // Build the download URLs list
        Map<String, String> mapDownloadUrls = new HashMap<>( );
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
        if ( entryTypeService instanceof AbstractEntryTypeUpload )
        {
            for ( Response response : ticket.getListResponse( ) )
            {
                ResponseHome.create( response );
                mapDownloadUrls.put( response.getFile( ).getTitle( ),
                        ( (AbstractEntryTypeUpload) entryTypeService ).getUrlDownloadFile( response.getIdResponse( ), AppPathService.getProdUrl( request ) ) );
            }
        }

        model.put( MARK_MAP_DOWNLOAD_URL, mapDownloadUrls );
        return AppTemplateService.getTemplate( TEMPLATE_USER_MESSAGE, request.getLocale( ), model ).getHtml( );
    }
}
