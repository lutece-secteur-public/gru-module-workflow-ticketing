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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.MessageDirection;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskEditTicketConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicket;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicketField;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.authentication.EditTicketRequestAuthenticationService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.ticket.IEditableTicketService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.TaskEditTicketConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.web.EditTicketXPage;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class represents a task to edit a ticket
 *
 */
public class TaskEditTicket extends AbstractTicketingTask
{
    // Parameters
    private static final String    PARAMETER_MESSAGE                                          = "message";
    private static final String    PARAMETER_IDS_ENTRY                                        = "ids_entry";

    // Messages
    private static final String    MESSAGE_EDIT_TICKET                                        = "module.workflow.ticketing.task_edit_ticket.labelEditTicket";
    private static final String    MESSAGE_EDIT_TICKET_INFORMATION_VIEW_AGENT                 = "module.workflow.ticketing.task_edit_ticket.information.view.agent";
    private static final String    MESSAGE_EDIT_TICKET_INFORMATION_VIEW_AGENT_NO_FIELD_EDITED = "module.workflow.ticketing.task_edit_ticket.information.view.agent.noFieldEdited";
    private static final String    MESSAGE_EDIT_TICKET_INFORMATION_VIEW_USER                  = "module.workflow.ticketing.task_edit_ticket.information.view.user";
    private static final String    MESSAGE_EDIT_TICKET_INFORMATION_VIEW_USER_NO_FIELD_EDITED  = "module.workflow.ticketing.task_edit_ticket.information.view.user.noFieldEdited";
    private static final String    MESSAGE_EDIT_TICKET_INFORMATION_NO_MESSAGE                 = "module.workflow.ticketing.task_edit_ticket.information.noMessage";

    // Parameters
    private static final String    PARAMETER_USER_MESSAGE                                     = "user_message";

    // Beans
    private static final String    BEAN_EDIT_TICKET_CONFIG_SERVICE                            = "workflow-ticketing.taskEditTicketConfigService";

    // Other constants
    private static final String    UNDERSCORE                                                 = "_";
    private static final String    SEPARATOR                                                  = "; ";

    // Services
    @Inject
    private IEditableTicketService _editableTicketService;
    @Inject
    @Named( BEAN_EDIT_TICKET_CONFIG_SERVICE )
    private ITaskConfigService     _taskEditableTicketConfigService;
    @Inject
    private TicketFormService      _ticketFormService;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_EDIT_TICKET, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        TaskEditTicketConfig config = _taskEditableTicketConfigService.findByPrimaryKey( getId( ) );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, TaskEditTicketConstants.MESSAGE_NO_CONFIGURATION, AdminMessage.TYPE_STOP );
        }

        MessageDirection messageDirection = config.getMessageDirection( );

        if ( messageDirection == MessageDirection.AGENT_TO_USER )
        {
            strTaskInformation = processAgentTask( nIdResourceHistory, request, locale, config );
        } else
        {
            strTaskInformation = processUserTask( nIdResourceHistory, request, locale, config );
        }

        return strTaskInformation;
    }

    /**
     * Process the task for agent side
     * 
     * @param nIdResourceHistory
     *            the ResourceHistory id
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @param config
     *            the task configuration
     * @return the information message to store
     */
    private String processAgentTask( int nIdResourceHistory, HttpServletRequest request, Locale locale, TaskEditTicketConfig config )
    {
        String strTaskInformation = StringUtils.EMPTY;

        String strAgentMessage = request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + getId( ) );
        String[] listIdsEntry = request.getParameterValues( PARAMETER_IDS_ENTRY + UNDERSCORE + getId( ) );

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        boolean bCreate = false;
        List<EditableTicketField> listEditableTicketFields = new ArrayList<EditableTicketField>( );

        EditableTicket editableTicket = _editableTicketService.find( nIdResourceHistory, getId( ) );

        if ( editableTicket == null )
        {
            editableTicket = new EditableTicket( );
            editableTicket.setIdHistory( nIdResourceHistory );
            editableTicket.setIdTask( getId( ) );
            editableTicket.setIdTicket( ticket.getId( ) );
            bCreate = true;
        }

        StringBuilder sbEntries = new StringBuilder( );

        if ( listIdsEntry != null )
        {
            for ( String strIdEntry : listIdsEntry )
            {
                if ( StringUtils.isNotBlank( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
                {
                    int nIdEntry = Integer.parseInt( strIdEntry );
                    EditableTicketField editableTicketField = new EditableTicketField( );
                    editableTicketField.setIdEntry( nIdEntry );

                    listEditableTicketFields.add( editableTicketField );

                    Entry entry = EntryHome.findByPrimaryKey( nIdEntry );
                    sbEntries.append( entry.getTitle( ) ).append( SEPARATOR );
                }
            }

            if ( sbEntries.length( ) != 0 )
            {
                sbEntries.delete( sbEntries.length( ) - SEPARATOR.length( ), sbEntries.length( ) );
            }
        }

        editableTicket.setMessage( StringUtils.isNotBlank( strAgentMessage ) ? strAgentMessage : StringUtils.EMPTY );
        editableTicket.setListEditableTicketFields( listEditableTicketFields );
        editableTicket.setIsEdited( false );

        if ( bCreate )
        {
            _editableTicketService.create( editableTicket );
        } else
        {
            _editableTicketService.update( editableTicket );
        }

        if ( ticket != null )
        {
            ticket.setUrl( buildEditUrl( request, nIdResourceHistory, getId( ), config.getIdUserEditionAction( ) ) );
            TicketHome.update( ticket );
        }

        if ( sbEntries.length( ) == 0 )
        {
            strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_EDIT_TICKET_INFORMATION_VIEW_AGENT_NO_FIELD_EDITED, Locale.FRENCH ), TicketingConstants.MESSAGE_MARK + strAgentMessage );
        } else
        {
            strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_EDIT_TICKET_INFORMATION_VIEW_AGENT, Locale.FRENCH ), sbEntries.toString( ), TicketingConstants.MESSAGE_MARK + strAgentMessage );
        }

        return strTaskInformation;
    }

    /**
     * Process the task for user side
     * 
     * @param nIdResourceHistory
     *            the ResourceHistory id
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @param config
     *            the task configuration
     * @return the information message to store
     */
    private String processUserTask( int nIdResourceHistory, HttpServletRequest request, Locale locale, TaskEditTicketConfig config )
    {
        String strTaskInformation = StringUtils.EMPTY;
        StringBuilder sbEntries = new StringBuilder( );

        String strUserMessage = request.getParameter( PARAMETER_USER_MESSAGE );

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        EditableTicket editableTicket = _editableTicketService.findByIdTicket( ticket.getId( ) );

        List<Entry> listEntriesToEdit = _editableTicketService.buildListEntriesToEdit( request, editableTicket.getListEditableTicketFields( ) );

        for ( Entry entry : listEntriesToEdit )
        {
            Iterator<Response> iterator = ticket.getListResponse( ).iterator( );

            while ( iterator.hasNext( ) )
            {
                Response response = iterator.next( );

                if ( response.getEntry( ).getIdEntry( ) == entry.getIdEntry( ) )
                {
                    iterator.remove( );
                }
            }

            _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), request.getLocale( ), ticket );
            sbEntries.append( entry.getTitle( ) ).append( SEPARATOR );
        }

        // remove and add generic attributes responses
        TicketHome.removeTicketResponse( ticket.getId( ) );

        if ( ( ticket.getListResponse( ) != null ) && !ticket.getListResponse( ).isEmpty( ) )
        {
            for ( Response response : ticket.getListResponse( ) )
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
            }
        }

        if ( sbEntries.length( ) != 0 )
        {
            sbEntries.delete( sbEntries.length( ) - SEPARATOR.length( ), sbEntries.length( ) );
        }

        ticket.setUserMessage( strUserMessage );
        TicketHome.update( ticket );

        editableTicket.setIsEdited( true );
        _editableTicketService.update( editableTicket );

        if ( StringUtils.isEmpty( strUserMessage ) )
        {
            strUserMessage = I18nService.getLocalizedString( MESSAGE_EDIT_TICKET_INFORMATION_NO_MESSAGE, Locale.FRENCH );
        }

        if ( sbEntries.length( ) == 0 )
        {
            strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_EDIT_TICKET_INFORMATION_VIEW_USER_NO_FIELD_EDITED, Locale.FRENCH ), TicketingConstants.MESSAGE_MARK + strUserMessage );
        } else
        {
            strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_EDIT_TICKET_INFORMATION_VIEW_USER, Locale.FRENCH ), sbEntries.toString( ), TicketingConstants.MESSAGE_MARK + strUserMessage );
        }

        return strTaskInformation;
    }

    /**
     * Builds the URL to permit to the user to edit the ticket
     * 
     * @param request
     *            the request
     * @param nIdHistory
     *            the history id
     * @param nIdTask
     *            the task id
     * @param nIdAction
     *            the action id
     * @return the URL
     */
    private String buildEditUrl( HttpServletRequest request, int nIdHistory, int nIdTask, int nIdAction )
    {
        String strInfo = StringUtils.EMPTY;
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );

        if ( resourceHistory != null )
        {
            List<String> listElements = new ArrayList<String>( );
            listElements.add( Integer.toString( nIdHistory ) );
            listElements.add( Integer.toString( nIdTask ) );
            listElements.add( Integer.toString( nIdAction ) );

            String strTimestamp = Long.toString( new Date( ).getTime( ) );
            String strSignature = EditTicketRequestAuthenticationService.getRequestAuthenticator( ).buildSignature( listElements, strTimestamp );
            StringBuilder sbUrl = new StringBuilder( AppPathService.getProdUrl( request ) );

            UrlItem url = new UrlItem( sbUrl.toString( ) + AppPathService.getPortalUrl( ) );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP, EditTicketXPage.XPAGE );
            url.addParameter( TaskEditTicketConstants.PARAMETER_ID_HISTORY, nIdHistory );
            url.addParameter( TaskEditTicketConstants.PARAMETER_ID_TASK, nIdTask );
            url.addParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION, nIdAction );
            url.addParameter( TicketingConstants.PARAMETER_SIGNATURE, strSignature );
            url.addParameter( TicketingConstants.PARAMETER_TIMESTAMP, strTimestamp );

            strInfo = url.getUrl( );
        }

        return strInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        super.doRemoveTaskInformation( nIdHistory );
        _editableTicketService.removeByIdHistory( nIdHistory, getId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        super.doRemoveConfig( );
        _editableTicketService.removeByIdTask( getId( ) );
        _taskEditableTicketConfigService.remove( getId( ) );
    }
}
