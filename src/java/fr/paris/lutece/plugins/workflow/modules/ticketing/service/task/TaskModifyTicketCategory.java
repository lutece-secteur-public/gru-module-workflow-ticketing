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

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.Locale;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents a task to modify the ticket category and so, its domain and type
 *
 */
public class TaskModifyTicketCategory extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_MODIFY_TICKET_CATEGORY = "module.workflow.ticketing.task_modify_ticket_category.labelModifyTicketCategory";
    private static final String MESSAGE_MODIFY_TICKET_CATEGORY_INFORMATION = "module.workflow.ticketing.task_modify_ticket_category.information";

    // PARAMETERS
    public static final String PARAMETER_TICKET_CATEGORY_ID = "id_ticket_category";
    public static final String PARAMETER_TICKET_DOMAIN_ID = "id_ticket_domain";
    public static final String PARAMETER_TICKET_TYPE_ID = "id_ticket_type";

    // Services
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            String strNewCategoryId = request.getParameter( PARAMETER_TICKET_CATEGORY_ID );
            int nNewCategoryId = Integer.parseInt( strNewCategoryId );
            String strNewTypeId = request.getParameter( PARAMETER_TICKET_TYPE_ID );
            int nNewTypeId = Integer.parseInt( strNewTypeId );
            String strNewDomainId = request.getParameter( PARAMETER_TICKET_DOMAIN_ID );
            int nNewDomainId = Integer.parseInt( strNewDomainId );

            String strNewTypeLabel = TicketCategoryHome.findByPrimaryKey( nNewCategoryId ).getLabel(  );
            String strNewDomainLabel = TicketDomainHome.findByPrimaryKey( nNewDomainId ).getLabel(  );
            String strNewCategoryLabel = TicketTypeHome.findByPrimaryKey( nNewTypeId ).getLabel(  );

            String strPreviousCategoryLabel = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) )
                                                                .getLabel(  );
            String strPreviousDomainLabel = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain(  ) ).getLabel(  );
            String strPreviousTypeLabel = TicketTypeHome.findByPrimaryKey( ticket.getIdTicketType(  ) ).getLabel(  );

            ticket.setIdTicketType( nNewTypeId );
            ticket.setIdTicketDomain( nNewDomainId );
            ticket.setIdTicketCategory( nNewCategoryId );
            TicketHome.update( ticket );

            strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( 
                        MESSAGE_MODIFY_TICKET_CATEGORY_INFORMATION, Locale.FRENCH ), strPreviousTypeLabel,
                    strPreviousDomainLabel, strPreviousCategoryLabel, strNewTypeLabel, strNewDomainLabel,
                    strNewCategoryLabel );
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_CATEGORY, locale );
    }
}
