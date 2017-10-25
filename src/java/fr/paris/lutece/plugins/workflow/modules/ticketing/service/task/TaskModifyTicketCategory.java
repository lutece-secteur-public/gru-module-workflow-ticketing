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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidatorResult;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskModifyTicketCategoryConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a task to modify the ticket category and so, its domain and type
 *
 */
public class TaskModifyTicketCategory extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_MODIFY_TICKET_CATEGORY = "module.workflow.ticketing.task_modify_ticket_category.labelModifyTicketCategory";
    private static final String MESSAGE_MODIFY_TICKET_TYPE_INFORMATION = "module.workflow.ticketing.task_modify_ticket_type.information";
    private static final String MESSAGE_MODIFY_TICKET_DOMAIN_INFORMATION = "module.workflow.ticketing.task_modify_ticket_domain.information";
    private static final String MESSAGE_MODIFY_TICKET_CATEGORY_INFORMATION = "module.workflow.ticketing.task_modify_ticket_category.information";
    private static final String MESSAGE_MODIFY_TICKET_PRECISION_INFORMATION = "module.workflow.ticketing.task_modify_ticket_precision.information";
    private static final String MESSAGE_MODIFY_TICKET_ATTRIBUTE_INFORMATION = "module.workflow.ticketing.task_modify_ticket_attribute.information";
    private static final String MESSAGE_NO_VALUE = "module.workflow.ticketing.task_modify_ticket_category.noValue";

    // PARAMETERS
    public static final String PARAMETER_TICKET_CATEGORY_ID = "id_ticket_category";
    public static final String PARAMETER_TICKET_DOMAIN_ID = "id_ticket_domain";
    public static final String PARAMETER_TICKET_TYPE_ID = "id_ticket_type";
    public static final String SEPARATOR = " - ";

    // Other constants
    private static final String REDIRECT_LIST = "list";

    // Beans
    private static final String BEAN_MODIFY_TICKET_CATEGORY_CONFIG_SERVICE = "workflow-ticketing.taskModifyTicketCategoryConfigService";
    @Inject
    private TicketFormService _ticketFormService;
    @Inject
    @Named( BEAN_MODIFY_TICKET_CATEGORY_CONFIG_SERVICE )
    private ITaskConfigService _taskModifyTicketCategoryConfigService;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            
            String strPreviousTypeLabel = ticket.getTicketType( ).getLabel( );
            String strPreviousDomainLabel = ticket.getTicketDomain( ).getLabel( );
            String strPreviousCategoryLabel = ticket.getTicketCategory( ).getLabel( );
            String strPreviousPrecisionLabel = ticket.getTicketPrecision( ).getLabel( );
            
            // Validate the TicketCategory
            TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request ).validateTicketCategory( );
            ticket.setTicketCategory( categoryValidatorResult.getTicketCategory( ) );

            TicketHome.update( ticket );

            if ( !strPreviousTypeLabel.equals( ticket.getTicketType( ).getLabel( ) ) )
            {
                strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_TYPE_INFORMATION, locale ),
                        strPreviousTypeLabel, ticket.getTicketType( ).getLabel( ) );
            }

            if ( !strPreviousDomainLabel.equals( ticket.getTicketDomain( ).getLabel( ) ) )
            {
                strTaskInformation += MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_DOMAIN_INFORMATION, locale ),
                        strPreviousDomainLabel, ticket.getTicketDomain( ).getLabel( ) );
            }

            if ( !strPreviousCategoryLabel.equals( ticket.getTicketCategory( ).getLabel( ) ) )
            {
                strTaskInformation += MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_CATEGORY_INFORMATION, locale ),
                        strPreviousCategoryLabel, ticket.getTicketCategory( ).getLabel( ) );
            }

            if ( !strPreviousPrecisionLabel.equals( ticket.getTicketPrecision( ).getLabel( ) ) )
            {
                strTaskInformation += MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_PRECISION_INFORMATION, locale ),
                        StringUtils.isNotEmpty( strPreviousPrecisionLabel ) ? strPreviousPrecisionLabel : I18nService.getLocalizedString( MESSAGE_NO_VALUE, locale ),
                        StringUtils.isNotEmpty( ticket.getTicketPrecision( ).getLabel( ) ) ? ticket.getTicketPrecision( ).getLabel( ) : I18nService.getLocalizedString( MESSAGE_NO_VALUE, locale ) );
            }

            if ( ticket.getTicketCategory( ).getId( ) > 0 )
            {
                TaskModifyTicketCategoryConfig config = _taskModifyTicketCategoryConfigService.findByPrimaryKey( getId( ) );
                int nIdCategory = ( ticket.getTicketPrecision( ) != null && StringUtils.isNotBlank( ticket.getTicketPrecision( ).getLabel( ) ) )?ticket.getTicketPrecision( ).getId( ):ticket.getTicketCategory( ).getId( );
                List<Entry> listEntry = TicketFormService.getFilterInputs( nIdCategory, config.getSelectedEntries( ) );
                // save current values, clear ticket
                List<Response> listCurrentResponse = ticket.getListResponse( );
                ticket.setListResponse( new ArrayList<>( ) );
                List<Response> listResponseToAdd = new ArrayList<>( );
                List<Integer> listResponseIdToDelete = new ArrayList<>( );

                // loop on filtered entry
                for ( Entry entry : listEntry )
                {
                    String strPreviousAttributeValue = StringUtils.EMPTY;
                    String strNewAttributeValue = StringUtils.EMPTY;
                    int nIdCurrentResponse = -1;

                    // current value
                    Iterator<Response> iterator = listCurrentResponse.iterator( );
                    while ( iterator.hasNext( ) )
                    {
                        Response response = iterator.next( );

                        if ( response.getEntry( ).getIdEntry( ) == entry.getIdEntry( ) )
                        {
                            if ( response.getResponseValue( ) != null )
                            {
                                strPreviousAttributeValue += ( " " + response.getResponseValue( ) );
                            }
                            else
                                if ( ( response.getFile( ) != null ) && ( response.getFile( ).getTitle( ) != null ) )
                                {
                                    strPreviousAttributeValue += ( " " + response.getFile( ).getTitle( ) );
                                }
                            // clear the response
                            nIdCurrentResponse = response.getIdResponse( );
                            iterator.remove( );
                            break;
                        }
                    }

                    // new value
                    _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), locale, ticket );
                    Response responseNew = null;
                    for ( Response response : ticket.getListResponse( ) )
                    {
                        if ( response.getEntry( ).getIdEntry( ) == entry.getIdEntry( ) )
                        {
                            if ( response.getResponseValue( ) != null )
                            {
                                strNewAttributeValue += ( " " + response.getResponseValue( ) );
                            }
                            else
                                if ( ( response.getFile( ) != null ) && ( response.getFile( ).getTitle( ) != null ) )
                                {
                                    strNewAttributeValue += ( " " + response.getFile( ).getTitle( ) );
                                }
                            responseNew = response;
                            break;
                        }
                    }

                    // compare
                    if ( !strPreviousAttributeValue.equals( strNewAttributeValue ) )
                    {
                        strTaskInformation += MessageFormat.format(
                                I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_ATTRIBUTE_INFORMATION, Locale.FRENCH ), entry.getTitle( ),
                                strPreviousAttributeValue, strNewAttributeValue );
                        if ( nIdCurrentResponse != -1 )
                        {
                            listResponseIdToDelete.add( nIdCurrentResponse );
                        }
                        if ( responseNew != null )
                        {
                            listResponseToAdd.add( responseNew );
                        }
                    }
                }

                // remove generic attributes responses updated
                for ( int nIdResponse : listResponseIdToDelete )
                {
                    TicketHome.removeTicketResponse( ticket.getId( ), nIdResponse );
                }

                // add new responses
                for ( Response response : listResponseToAdd )
                {
                    ResponseHome.create( response );
                    TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
                }
            }

            if ( !RBACService.isAuthorized( ticket.getTicketDomain( ), TicketCategory.PERMISSION_VIEW_DETAIL, AdminUserService.getAdminUser( request ) ) )
            {
                request.setAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION, REDIRECT_LIST );
            }
        }

        if ( !strTaskInformation.equals( StringUtils.EMPTY ) )
        {
            strTaskInformation = strTaskInformation.substring( 0, strTaskInformation.length( ) - 5 );
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_CATEGORY, locale );
    }
}
