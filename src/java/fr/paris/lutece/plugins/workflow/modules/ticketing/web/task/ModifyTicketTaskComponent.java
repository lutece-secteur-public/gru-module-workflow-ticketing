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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.address.TicketAddress;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.FormValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidator;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.WorkflowTicketingUtils;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.bean.BeanUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * 
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskModifyTicket}
 *
 */
public class ModifyTicketTaskComponent extends TicketingTaskComponent
{
    // Constants
    private static final String JSP_VIEW_TICKET                  = TicketingConstants.ADMIN_CONTROLLLER_PATH + TicketingConstants.JSP_VIEW_TICKET;

    // Templates
    private static final String TEMPLATE_TASK_MODIFY_TICKET_FORM = "admin/plugins/workflow/modules/ticketing/task_modify_ticket.html";

    // Marks
    private static final String MARK_USER_TITLE_LIST             = "user_titles_list";
    private static final String MARK_CONTACT_MODE_LIST           = "contact_modes_list";

    // Messages
    private static final String MESSAGE_MODIFY_TICKET_ERROR      = "module.workflow.ticketing.task_modify_ticket.error";
    private static final String MESSAGE_ERROR_COMMENT_VALIDATION = "ticketing.validation.ticket.TicketComment.size";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );

        Map<String, Object> model = getModel( ticket );

        model.put( MARK_USER_TITLE_LIST, UserTitleHome.getReferenceList( request.getLocale( ) ) );
        model.put( MARK_CONTACT_MODE_LIST, ContactModeHome.getReferenceList( request.getLocale( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MODIFY_TICKET_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        // Validate the selection of items
        List<ErrorMessage> listErrors = valideTicket( request );

        if ( !listErrors.isEmpty( ) )
        {
            UrlItem urlReturnFromErros = new UrlItem( JSP_VIEW_TICKET );
            urlReturnFromErros.addParameter( TicketingConstants.PARAMETER_ID_TICKET, nIdResource );

            return AdminMessageService.getMessageUrl( request, MESSAGE_MODIFY_TICKET_ERROR, WorkflowTicketingUtils.formatValidationErrors( request, listErrors ), urlReturnFromErros.getUrl( ),
                    AdminMessage.TYPE_ERROR );
        }

        return null;
    }

    /**
     * Method used to validate informations for the new ticket modification
     */
    private List<ErrorMessage> valideTicket( HttpServletRequest request )
    {
        List<ErrorMessage> listErrors = new ArrayList<>( );

        // Populate the Ticket
        Ticket ticketToValidate = new Ticket( );
        ticketToValidate.setTicketCategory( new TicketCategory( ) ); // -- to not generate validation error on this field
        BeanUtil.populate( ticketToValidate, request );

        // Update the ticket adress
        TicketAddress ticketAdressToValidate = new TicketAddress( );
        BeanUtil.populate( ticketAdressToValidate, request );
        ticketToValidate.setTicketAddress( ticketAdressToValidate );

        // Validate the ticket
        listErrors.addAll( validateBean( ticketToValidate, request.getLocale( ) ) );

        // Validate the contact mode
        String strContactModeFilled = new FormValidator( request ).isContactModeFilled( );
        if ( strContactModeFilled != null )
        {
            listErrors.add( new MVCMessage( strContactModeFilled ) );
        }

        // The validation for the ticket comment size
        String strNewComment = ticketToValidate.getTicketComment( );
        if ( FormValidator.countCharTicketComment( strNewComment ) > 5000 )
        {
            listErrors.add( new MVCMessage( I18nService.getLocalizedString( MESSAGE_ERROR_COMMENT_VALIDATION, request.getLocale( ) ) ) );
        }

        return listErrors;
    }

    /**
     * Validate a bean. his method convert the String result of the validateBean method of the TicketValidator to MVCMessage
     * 
     * @param request
     * @param bean
     * @param strPrefix
     * @return listErrors : the list of all validations errors
     */
    private List<ErrorMessage> validateBean( Ticket ticket, Locale locale )
    {
        List<ErrorMessage> listErrors = new ArrayList<>( );
        List<String> listTicketValidationErrors = new TicketValidator( locale ).validateBean( ticket );

        if ( listTicketValidationErrors.isEmpty( ) )
        {
            return listErrors;
        } else
        {
            for ( String errorValidation : listTicketValidationErrors )
            {
                listErrors.add( new MVCMessage( errorValidation ) );
            }
        }
        return listErrors;
    }

}
