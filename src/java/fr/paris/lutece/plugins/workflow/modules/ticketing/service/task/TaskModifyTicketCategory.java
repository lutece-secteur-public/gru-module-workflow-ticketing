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
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskModifyTicketCategoryConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

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
    private static final String MESSAGE_MODIFY_TICKET_ATTRIBUTE_INFORMATION = "module.workflow.ticketing.task_modify_ticket_attribute.information";

    // PARAMETERS
    public static final String PARAMETER_TICKET_CATEGORY_ID = "id_ticket_category";
    public static final String PARAMETER_TICKET_DOMAIN_ID = "id_ticket_domain";
    public static final String PARAMETER_TICKET_TYPE_ID = "id_ticket_type";
    public static final String SEPARATOR = " - ";

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
            String strNewTypeId = request.getParameter( PARAMETER_TICKET_TYPE_ID );
            int nNewTypeId = Integer.parseInt( strNewTypeId );
            String strNewDomainId = request.getParameter( PARAMETER_TICKET_DOMAIN_ID );
            int nNewDomainId = Integer.parseInt( strNewDomainId );
            String strNewCategoryId = request.getParameter( PARAMETER_TICKET_CATEGORY_ID );
            int nNewCategoryId = Integer.parseInt( strNewCategoryId );
            TicketCategory newTicketCategory = TicketCategoryHome.findByPrimaryKey( nNewCategoryId );
            TicketCategory previousTicketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getTicketCategory( ).getId( ) );

            String strNewTypeLabel = TicketTypeHome.findByPrimaryKey( nNewTypeId ).getLabel( );
            String strPreviousTypeLabel = TicketTypeHome.findByPrimaryKey( ticket.getIdTicketType( ) ).getLabel( );
            String strNewDomainLabel = TicketDomainHome.findByPrimaryKey( nNewDomainId ).getLabel( );
            String strPreviousDomainLabel = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain( ) ).getLabel( );
            String strNewCategoryLabel = newTicketCategory.getLabel( );
            String strPreviousCategoryLabel = previousTicketCategory.getLabel( );

            if ( StringUtils.isNotEmpty( newTicketCategory.getPrecision( ) ) )
            {
                strNewCategoryLabel += ( SEPARATOR + newTicketCategory.getPrecision( ) );
            }

            if ( StringUtils.isNotEmpty( previousTicketCategory.getPrecision( ) ) )
            {
                strPreviousCategoryLabel += ( SEPARATOR + previousTicketCategory.getPrecision( ) );
            }

            ticket.setIdTicketType( nNewTypeId );
            ticket.setIdTicketDomain( nNewDomainId );

            TicketCategory ticketCategory = new TicketCategory( );
            ticketCategory.setId( nNewCategoryId );
            ticket.setTicketCategory( ticketCategory );

            TicketHome.update( ticket );

            if ( !strPreviousTypeLabel.equals( strNewTypeLabel ) )
            {
                strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_TYPE_INFORMATION, Locale.FRENCH ),
                        strPreviousTypeLabel, strNewTypeLabel );
            }

            if ( !strPreviousDomainLabel.equals( strNewDomainLabel ) )
            {
                strTaskInformation += MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_DOMAIN_INFORMATION, Locale.FRENCH ),
                        strPreviousDomainLabel, strNewDomainLabel );
            }

            if ( !strPreviousCategoryLabel.equals( strNewCategoryLabel ) )
            {
                strTaskInformation += MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_CATEGORY_INFORMATION, Locale.FRENCH ),
                        strPreviousCategoryLabel, strNewCategoryLabel );
            }

            if ( ticket.getTicketCategory( ).getId( ) > 0 )
            {
                TaskModifyTicketCategoryConfig config = _taskModifyTicketCategoryConfigService.findByPrimaryKey( getId( ) );
                List<Entry> listEntry = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), config.getSelectedEntries( ) );

                for ( Entry entry : listEntry )
                {
                    String strPreviousAttributeValue = StringUtils.EMPTY;
                    String strNewAttributeValue = StringUtils.EMPTY;

                    Iterator<Response> iterator = ticket.getListResponse( ).iterator( );

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

                            iterator.remove( );
                        }
                    }

                    _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), locale, ticket );

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
                        }
                    }

                    if ( !strPreviousAttributeValue.equals( strNewAttributeValue ) )
                    {
                        strTaskInformation += MessageFormat.format(
                                I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_ATTRIBUTE_INFORMATION, Locale.FRENCH ), entry.getTitle( ),
                                strPreviousAttributeValue, strNewAttributeValue );
                    }
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
