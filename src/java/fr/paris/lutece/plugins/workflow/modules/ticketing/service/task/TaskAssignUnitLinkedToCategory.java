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

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a task to assign a unit from the category selected
 *
 */
public class TaskAssignUnitLinkedToCategory extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_ASSIGN_TICKET_TO_UNIT_LINKED_TO_CATEGORY = "module.workflow.ticketing.task_assign_unit_linked_to_category.labelAssignTicketLinkedToCategory";
    private static final String MESSAGE_ASSIGN_TICKET_TO_UNIT_LINKED_TO_CATEGORY_INFORMATION = "module.workflow.ticketing.task_assign_unit_linked_to_category.information";

    // Other constants
    private static final String REDIRECT_LIST = "list";

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            AssigneeUnit assigneeUnit = ticket.getAssigneeUnit( );

            if ( assigneeUnit == null )
            {
                assigneeUnit = new AssigneeUnit( );
            }

            TicketCategory ticketCategory = ticket.getTicketCategory( );

            Unit unit = UnitHome.findByPrimaryKey( ticketCategory.getAssigneeUnit( ).getUnitId( ) );

            if ( unit != null )
            {
                if ( assigneeUnit.getUnitId( ) != unit.getIdUnit( ) )
                {
                    request.setAttribute( TicketingConstants.ATTRIBUTE_IS_UNIT_CHANGED, true );
                }

                assigneeUnit.setUnitId( unit.getIdUnit( ) );
                assigneeUnit.setName( unit.getLabel( ) );
                ticket.setAssigneeUnit( assigneeUnit );
                ticket.setAssigneeUser( null );
                TicketHome.update( ticket );

                strTaskInformation = MessageFormat.format(
                        I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_UNIT_LINKED_TO_CATEGORY_INFORMATION, Locale.FRENCH ), assigneeUnit.getName( ),
                        ticketCategory.getLabel( ) );

                request.setAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION, REDIRECT_LIST );
            }
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ASSIGN_TICKET_TO_UNIT_LINKED_TO_CATEGORY, locale );
    }
}
