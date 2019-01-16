/*
 * Copyright (c) 2002-2016, Mairie de Paris
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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.service.IdentityService;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.identity.TicketingIdentityService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a task to edit a ticket
 *
 */
public class TaskAutomaticAssignmentDirection extends AbstractTicketingTask
{

    // Messages
    private static final String MESSAGE_AUTOMATIC_ASSIGNMENT_DIRECTION = "module.workflow.ticketing.task_automatic_assignment_direction.labelAutomaticAssignmentDirection";
    private static final String MESSAGE_AUTOMATIC_ASSIGN_DIRECTION_TICKET_INFORMATION = "module.workflow.ticketing.task_automatic_assignment_direction.information";

    // Properties
    private static final String PROPERTY_DIRECTION_DOMAIN_LABEL = "workflow-ticketing.workflow.automatic_assignment_direction.domainLabel";
    private static final String PROPERTY_USER_ATTRIBUTE_DIRECTION = "workflow-ticketing.workflow.identity.attribute.user.vdp.direction";
    private static final String PROPERTY_USER_ATTRIBUTE_DEFAULT_DIRECTION = "direction";

    private static final String PROPERTY_DIRECTION_ID_UNIT = "workflow-ticketing.workflow.direction.idUnit";

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_AUTOMATIC_ASSIGNMENT_DIRECTION, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {

        String strTaskInformation = null;
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket.getTicketDomain( ) != null
                && ticket.getTicketDomain( ).getLabel( ).equals( AppPropertiesService.getProperty( PROPERTY_DIRECTION_DOMAIN_LABEL ) ) )
        {
            String strGuid = ticket.getGuid( );
            IdentityService identityService = TicketingIdentityService.getInstance( ).getIdentityService( );
            IdentityDto identity = identityService.getIdentityByConnectionId( strGuid, TicketingConstants.APPLICATION_CODE );
            String strDirectionAttribute = AppPropertiesService.getProperty( PROPERTY_USER_ATTRIBUTE_DIRECTION, PROPERTY_USER_ATTRIBUTE_DEFAULT_DIRECTION );
            String strDirection = identity.getAttributes( ).get( strDirectionAttribute ).getValue( );

            // TODO maybe replace this with a backoffice admin page
            int nIdUnit = AppPropertiesService.getPropertyInt( PROPERTY_DIRECTION_ID_UNIT + "." + strDirection, -1 );
            Unit unit = UnitHome.findByPrimaryKey( nIdUnit );

            ticket.setAssigneeUnit( new AssigneeUnit( unit ) );
            TicketHome.update( ticket );
            strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_AUTOMATIC_ASSIGN_DIRECTION_TICKET_INFORMATION, Locale.FRENCH ),
                    ticket.getAssigneeUnit( ).getName( ) );
        }

        return strTaskInformation;
    }

}
