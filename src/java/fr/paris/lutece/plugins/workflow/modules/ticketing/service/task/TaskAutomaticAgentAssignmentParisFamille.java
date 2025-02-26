/*
 * Copyright (c) 2002-2025, City of Paris
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
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.AssignmentParisFamilleService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.assignment.IAutomaticAssignmentAgentParisFamilleService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a task that sends ticket store the admin user who created the sollicitation BO
 *
 */
public class TaskAutomaticAgentAssignmentParisFamille extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_AGENT_ASSIGNATION_TICKET       = "module.workflow.ticketing.task_automatic_agent_assignment_paris_famille.labelAutomaticAssignment";
    private static final String MESSAGE_AGENT_AUTOMATIC_ASSIGNATION_INFORMATION = "module.workflow.ticketing.task_automatic_agent_assignment_paris_famille.information";
    private static final String                          PROPERTY_PARIS_FAMILLE_DOMAIN_LABEL             = "workflow.ticketing.task_automatic_agent_assignment_paris_famille.domainLabel";

    // Services
    @Inject
    private IAutomaticAssignmentAgentParisFamilleService _automaticAssignmentAgentParisFamilleService;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ( ticket.getTicketDomain( ) != null ) && ticket.getTicketDomain( ).getLabel( ).equalsIgnoreCase( AppPropertiesService.getProperty( PROPERTY_PARIS_FAMILLE_DOMAIN_LABEL ) )
                && !ticket.getLastname( ).isEmpty( ) )
        {
            String lastNameClassicAlphabet = AssignmentParisFamilleService.convertSpecialCharOrAccentToClassicChar( ticket.getLastname( ).trim( ).toLowerCase( ) );
            AdminUser adminUser = _automaticAssignmentAgentParisFamilleService.getAssignedParisFamilleAgent( lastNameClassicAlphabet );

            if ( adminUser != null )
            {
                strTaskInformation = returnInfoWhenAssignUserAndUnit( adminUser, ticket, request );
            }
        }

        return strTaskInformation;
    }

    private String returnInfoWhenAssignUserAndUnit( AdminUser adminUser, Ticket ticket, HttpServletRequest request )
    {
        AssigneeUser assigneeUser = new AssigneeUser( adminUser );
        ticket.setAssigneeUser( assigneeUser );

        List<Unit> listUnit = UnitHome.findByIdUser( adminUser.getUserId( ) );
        AssigneeUnit assigneeUnit = null;

        if ( ( listUnit != null ) && ( !listUnit.isEmpty( ) ) )
        {
            assigneeUnit = new AssigneeUnit( listUnit.get( 0 ) );
        }

        if ( assigneeUnit != null )
        {
            if ( ( null != ticket.getAssigneeUnit( ) ) && ( ticket.getAssigneeUnit( ).getUnitId( ) != assigneeUnit.getUnitId( ) ) && ( request != null ) )
            {
                request.setAttribute( TicketingConstants.ATTRIBUTE_IS_UNIT_CHANGED, true );
            }

            ticket.setAssigneeUnit( assigneeUnit );
        }

        TicketHome.update( ticket );

        return MessageFormat.format( I18nService.getLocalizedString( MESSAGE_AGENT_AUTOMATIC_ASSIGNATION_INFORMATION, Locale.FRENCH ),
                adminUser.getFirstName( ) + " " + adminUser.getLastName( ), ticket.getAssigneeUnit( ).getName( ) );
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_AGENT_ASSIGNATION_TICKET, locale );
    }
}
