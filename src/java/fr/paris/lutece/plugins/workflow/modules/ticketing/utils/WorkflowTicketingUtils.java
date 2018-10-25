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
package fr.paris.lutece.plugins.workflow.modules.ticketing.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.IExternalUserDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.web.task.TicketEmailExternalUserTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class provides utility methods for the module-workflow-ticketing
 *
 */
public final class WorkflowTicketingUtils
{
    private static final IResourceHistoryService _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );
    private static final String                  SEMICOLON               = ";";

    // Templates
    private static final String                  TEMPLATE_ERRORS_LIST    = "admin/util/errors_list.html";

    // Marks
    private static final String                  MARK_ERRORS_LIST        = "errors_list";

    /**
     * Private constructor
     */
    private WorkflowTicketingUtils( )
    {
    }

    /**
     * Get the ticket from a given id history
     * 
     * @param nIdHistory
     *            the id history
     * @return the ticket
     */
    public static Ticket findTicketByIdHistory( int nIdHistory )
    {
        Ticket ticket = null;
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );

        if ( ( resourceHistory != null ) && Ticket.TICKET_RESOURCE_TYPE.equals( resourceHistory.getResourceType( ) ) )
        {
            ticket = TicketHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
        }

        return ticket;
    }

    /**
     * Check if a list of emails (as string) is valid
     * 
     * @param strEmails
     *            the string of emails
     * @param externalUserDAO
     *            if not null check in luteceUser if email is valid
     * 
     * @return Empty list if no error, else a list with first element is the message key, and following element are parameters
     */
    public static List<String> validEmailList( String strEmails, IExternalUserDAO externalUserUserDAO, String strNextActionId )
    {
        List<String> listForError = new ArrayList<String>( );
        if ( StringUtils.isBlank( strEmails ) )
        {
            listForError.add( TicketEmailExternalUserTaskComponent.MESSAGE_EMPTY_EMAIL );
        } else
        {
            String[] arrayEmails = strEmails.split( SEMICOLON );
            if ( arrayEmails.length == 0 )
            {
                listForError.add( TicketEmailExternalUserTaskComponent.MESSAGE_EMPTY_EMAIL );
            } else
            {
                EmailValidator validator = EmailValidator.getInstance( );
                for ( String strEmail : arrayEmails )
                {
                    if ( StringUtils.isBlank( strEmail ) )
                    {
                        listForError.add( TicketEmailExternalUserTaskComponent.MESSAGE_INVALID_EMAIL );
                        listForError.add( strEmail );
                        break;
                    } else
                    {
                        if ( !validator.isValid( strEmail ) )
                        {
                            listForError.add( TicketEmailExternalUserTaskComponent.MESSAGE_INVALID_EMAIL );
                            listForError.add( strEmail );
                            break;
                        } else if ( externalUserUserDAO != null && !externalUserUserDAO.isValidEmail( strEmail, strNextActionId ) )
                        {
                            listForError.add( TicketEmailExternalUserTaskComponent.MESSAGE_INVALID_EMAIL_OR_NOT_AUTHORIZED );
                            listForError.add( strEmail );
                            break;
                        }
                    }
                }
            }
        }
        return listForError;
    }

    /**
     * {@link fr.paris.lutece.portal.service.message.AdminMessageService#formatValidationErrors}
     */
    public static <T> Object[] formatValidationErrors( HttpServletRequest request, List<? extends ErrorMessage> errors )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ERRORS_LIST, errors );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ERRORS_LIST, request.getLocale( ), model );
        String[] formatedErrors = { template.getHtml( ) };

        return formatedErrors;
    }
}
