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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableXPage;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.ticket.EditableTicket;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.authentication.EditTicketRequestAuthenticationService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.ticket.EditableTicketService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.ticket.IEditableTicketService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.TaskEditTicketConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.WorkflowTicketingUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * This class represents a controller to edit a ticket
 *
 */
public class EditTicketXPage implements XPageApplication
{
    // XPage
    public static final String XPAGE = "editticket";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = 7677620731962218061L;

    // TEMPLATES
    private static final String TEMPLATE_EDIT_TICKET = "skin/plugins/ticketing/ticket/view_ticket_details.html";

    // Properties
    private static final String PROPERTY_XPAGE_EDIT_TICKET_PAGETITLE = "module.workflow.ticketing.edit_ticket.page_title";
    private static final String PROPERTY_XPAGE_EDIT_TICKET_PATHLABEL = "module.workflow.ticketing.edit_ticket.page_label";
    private static final String PROPERTY_URL_RETURN = "module.workflow.ticketing.task_edit_ticket.url_return";

    // MESSAGES
    private static final String MESSAGE_TICKET_ALREADY_EDITED = "module.workflow.ticketing.edit_ticket.message.ticket_already_edited";
    private static final String MESSAGE_EDITION_COMPLETE = "module.workflow.ticketing.edit_ticket.message.edition_complete";

    // Marks
    private static final String MARK_SIGNATURE = "signature";
    private static final String MARK_TIMESTAMP = "timestamp";
    private static final String MARK_ID_ACTION = "id_action";

    // Parameters
    private static final String PARAMETER_ACTION = "action";

    // ACTIONS
    private static final String ACTION_DO_MODIFY_TICKET = "do_modify_ticket";
    private static transient WorkflowService _workflowService = WorkflowService.getInstance( );

    // SERVICES
    private transient IEditableTicketService _editableTicketService = SpringContextService.getBean( EditableTicketService.BEAN_NAME );
    private transient TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );

    /**
     * {@inheritDoc}
     */
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException, SiteMessageException
    {
        XPage page = null;

        String strUrlReturn = AppPropertiesService.getProperty( PROPERTY_URL_RETURN );

        if ( isRequestAuthenticated( request ) )
        {
            String strIdHistory = request.getParameter( TaskEditTicketConstants.PARAMETER_ID_HISTORY );
            String strIdTask = request.getParameter( TaskEditTicketConstants.PARAMETER_ID_TASK );
            String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );

            if ( StringUtils.isNotBlank( strIdHistory ) && StringUtils.isNumeric( strIdHistory ) && StringUtils.isNotBlank( strIdTask )
                    && StringUtils.isNumeric( strIdTask ) && StringUtils.isNotBlank( strIdAction ) && StringUtils.isNumeric( strIdAction ) )
            {
                int nIdHistory = Integer.parseInt( strIdHistory );
                int nIdTask = Integer.parseInt( strIdTask );
                int nIdAction = Integer.parseInt( strIdAction );

                page = getPage( request, nIdHistory, nIdTask, nIdAction, strUrlReturn );
            }
            else
            {
                setSiteMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP, strUrlReturn );
            }
        }
        else
        {
            setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP, strUrlReturn );
        }

        return page;
    }

    /**
     * Get the page
     * 
     * @param request
     *            teh request
     * @param nIdHistory
     *            the history id
     * @param nIdTask
     *            the task id
     * @param nIdAction
     *            the action id
     * @param strUrlReturn
     *            the URL to return
     * @return the page
     * @throws SiteMessageException
     *             if there is an exception
     */
    private XPage getPage( HttpServletRequest request, int nIdHistory, int nIdTask, int nIdAction, String strUrlReturn ) throws SiteMessageException
    {
        XPage page = null;

        EditableTicket editableTicket = _editableTicketService.find( nIdHistory, nIdTask );

        if ( ( editableTicket != null ) && !editableTicket.isEdited( ) )
        {
            if ( _editableTicketService.isStateValid( editableTicket, request.getLocale( ) ) )
            {
                try
                {
                    if ( doProcessWorkflowAction( request, nIdAction, editableTicket ) )
                    {
                        // Back to home page
                        setSiteMessage( request, MESSAGE_EDITION_COMPLETE, SiteMessage.TYPE_INFO, strUrlReturn );
                    }
                    else
                    {
                        page = getEditTicketPage( request, editableTicket );
                    }
                }
                catch( RuntimeException e )
                {
                    AppLogService.error( e );
                    setSiteMessage( request, WorkflowCapableXPage.ERROR_WORKFLOW_ACTION_ABORTED, SiteMessage.TYPE_STOP, strUrlReturn );
                }
            }
            else
            {
                setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP, strUrlReturn );
            }
        }
        else
        {
            setSiteMessage( request, MESSAGE_TICKET_ALREADY_EDITED, SiteMessage.TYPE_INFO, strUrlReturn );
        }

        return page;
    }

    /**
     * Get the page to edit the ticket
     * 
     * @param request
     *            the HTTP request
     * @param editableTicket
     *            the editable ticket
     * @return a XPage
     * @throws SiteMessageException
     *             a site message if there is a problem
     */
    private XPage getEditTicketPage( HttpServletRequest request, EditableTicket editableTicket ) throws SiteMessageException
    {
        XPage page = new XPage( );

        List<Integer> listIdEntries = _editableTicketService.buildListIdEntriesToEdit( request, editableTicket.getListEditableTicketFields( ) );

        Ticket ticket = WorkflowTicketingUtils.findTicketByIdHistory( editableTicket.getIdHistory( ) );

        List<Entry> listEntries = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), listIdEntries );

        String htmlForm = _ticketFormService.getHtmlForm( listEntries, request.getLocale( ), true, request );
        TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( TaskEditTicketConstants.MARK_EDITABLE_TICKET, editableTicket );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_ID_ACTION, request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION ) );
        model.put( TaskEditTicketConstants.MARK_ENTRIES_HTML_FORM, htmlForm );
        model.put( MARK_SIGNATURE, request.getParameter( TicketingConstants.PARAMETER_SIGNATURE ) );
        model.put( MARK_TIMESTAMP, request.getParameter( TicketingConstants.PARAMETER_TIMESTAMP ) );

        ModelUtils.storeReadOnlyHtmlResponses( request, model, ticket );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EDIT_TICKET, request.getLocale( ), model );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_EDIT_TICKET_PAGETITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_EDIT_TICKET_PATHLABEL, request.getLocale( ) ) );
        page.setContent( template.getHtml( ) );

        return page;
    }

    /**
     * Do process the workflow action
     * 
     * @param request
     *            HttpServletRequest
     * @param nIdAction
     *            the action id
     * @param editableTicket
     *            editable ticket
     * @return {@code true} if the action is processed, {@code false} otherwise
     */
    private boolean doProcessWorkflowAction( HttpServletRequest request, int nIdAction, EditableTicket editableTicket )
    {
        boolean bIsActionProccessed = false;
        String strAction = request.getParameter( PARAMETER_ACTION );

        if ( StringUtils.isNotBlank( strAction ) )
        {
            if ( ACTION_DO_MODIFY_TICKET.equals( strAction ) )
            {
                TicketUtils.registerAdminUserFront( request );

                try
                {
                    Ticket ticket = WorkflowTicketingUtils.findTicketByIdHistory( editableTicket.getIdHistory( ) );
                    TicketCategory ticketCategory = ticket.getTicketCategory( );

                    _workflowService.doProcessAction( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdAction, ticketCategory.getId( ), request,
                            request.getLocale( ), false );

                    bIsActionProccessed = true;
                }
                finally
                {
                    TicketUtils.unregisterAdminUserFront( request );
                }
            }
        }

        return bIsActionProccessed;
    }

    /**
     * Set the site message
     * 
     * @param request
     *            the HTTP request
     * @param strMessage
     *            the message
     * @param nTypeMessage
     *            the message type
     * @param strUrlReturn
     *            the url return
     * @throws SiteMessageException
     *             the site message
     */
    private void setSiteMessage( HttpServletRequest request, String strMessage, int nTypeMessage, String strUrlReturn ) throws SiteMessageException
    {
        if ( StringUtils.isNotBlank( strUrlReturn ) )
        {
            SiteMessageService.setMessage( request, strMessage, nTypeMessage, strUrlReturn );
        }
        else
        {
            SiteMessageService.setMessage( request, strMessage, nTypeMessage );
        }
    }

    /**
     * Checks if the request is authenticated or not
     * 
     * @param request
     *            the HTTP request
     * @return {@code true} if the request is authenticated, {@code false} otherwise
     */
    private boolean isRequestAuthenticated( HttpServletRequest request )
    {
        return EditTicketRequestAuthenticationService.getRequestAuthenticator( ).isRequestAuthenticated( request );
    }
}
