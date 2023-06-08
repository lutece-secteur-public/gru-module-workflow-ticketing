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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.MessageDirection;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskEditTicketConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicket;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.ticket.IEditableTicketService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.TaskEditTicketConstants;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskEditTicket}
 *
 */
public class EditTicketTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_EDIT_TICKET_CONFIG = "admin/plugins/workflow/modules/ticketing/task_edit_ticket_config.html";
    private static final String TEMPLATE_TASK_EDIT_TICKET_FORM = "admin/plugins/workflow/modules/ticketing/task_edit_ticket_form.html";

    // Marks
    private static final String MARK_CONFIG = "config";
    private static final String MARK_AGENT_VIEW = "agent_view";
    private static final String MARK_LIST_ENTRIES = "list_entries";
    private static final String MARK_MESSAGE_DIRECTIONS_LIST = "message_directions_list";
    private static final String MARK_MESSAGE_DIRECTION = "message_direction";

    // Parameters
    private static final String PARAMETER_MESSAGE_DIRECTION = "message_direction";
    private static final String PARAMETER_ID_USER_EDITION_ACTION = "idUserEditionAction";
    private static final String PARAMETER_MESSAGE = "message";

    // Other constants
    private static final String UNDERSCORE = "_";
    private static final String FIELD_MESSAGE = "message";

    // SERVICES
    @Inject
    private IEditableTicketService _editableTicketService;
    @Inject
    private TicketFormService _ticketFormService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        TaskEditTicketConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        ReferenceList listMessageDirections = MessageDirection.getReferenceList( locale );

        model.put( MARK_MESSAGE_DIRECTIONS_LIST, listMessageDirections );

        if ( config != null )
        {
            model.put( MARK_MESSAGE_DIRECTION, config.getMessageDirection( ).ordinal( ) );
        }
        else
        {
            model.put( MARK_MESSAGE_DIRECTION, MessageDirection.AGENT_TO_USER );
        }

        ModelUtils.storeRichText( request, model );

        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EDIT_TICKET_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        int nMessageDirectionId = Integer.parseInt( request.getParameter( PARAMETER_MESSAGE_DIRECTION ) );
        int nIdUserEditionAction = Integer.parseInt( request.getParameter( PARAMETER_ID_USER_EDITION_ACTION ) );

        TaskEditTicketConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        Boolean bConfigToCreate = false;

        if ( config == null )
        {
            config = new TaskEditTicketConfig( );
            config.setIdTask( task.getId( ) );
            bConfigToCreate = true;
        }

        config.setMessageDirection( MessageDirection.valueOf( nMessageDirectionId ) );
        config.setIdUserEditionAction( nIdUserEditionAction );

        if ( bConfigToCreate )
        {
            getTaskConfigService( ).create( config );
        }
        else
        {
            getTaskConfigService( ).update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        TaskEditTicketConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, TaskEditTicketConstants.MESSAGE_NO_CONFIGURATION, AdminMessage.TYPE_STOP );
        }

        MessageDirection messageDirection = config.getMessageDirection( );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_AGENT_VIEW, messageDirection == MessageDirection.AGENT_TO_USER );

        Ticket ticket = getTicket( nIdResource, strResourceType );

        if ( messageDirection == MessageDirection.AGENT_TO_USER )
        {
            List<Entry> listEntry = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), null );
            List<Entry> listEntryWithoutComment = new ArrayList<>( );

            for ( Entry entry : listEntry )
            {
                if ( !entry.getEntryType( ).getComment( ) )
                {
                    listEntryWithoutComment.add( entry );
                }
            }

            model.put( MARK_LIST_ENTRIES, listEntryWithoutComment );

            ModelUtils.storeUserSignature( request, model );
            ModelUtils.storeRichText( request, model );
        }
        else
        {
            EditableTicket editableTicket = _editableTicketService.findByIdTicket( ticket.getId( ) );

            List<Integer> listIdEntries = _editableTicketService.buildListIdEntriesToEdit( request, editableTicket.getListEditableTicketFields( ) );

            List<Entry> listEntries = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), listIdEntries );

            String htmlForm = _ticketFormService.getHtmlForm( listEntries, request.getLocale( ), false, request );

            model.put( TaskEditTicketConstants.MARK_EDITABLE_TICKET, editableTicket );
            model.put( TaskEditTicketConstants.MARK_ENTRIES_HTML_FORM, htmlForm );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EDIT_TICKET_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        String strAgentMessage = request.getParameter( PARAMETER_MESSAGE + UNDERSCORE + task.getId( ) );
        TaskEditTicketConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, TaskEditTicketConstants.MESSAGE_NO_CONFIGURATION, AdminMessage.TYPE_STOP );
        }

        if ( ( MessageDirection.AGENT_TO_USER == config.getMessageDirection( ) ) && StringUtils.isEmpty( strAgentMessage ) )
        {
            Object [ ] tabRequiredFields = {
                    FIELD_MESSAGE
            };

            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        return null;
    }
}
