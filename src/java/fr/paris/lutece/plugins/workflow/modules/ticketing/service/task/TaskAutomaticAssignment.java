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

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.assignment.IAutomaticAssignmentService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents a task to edit a ticket
 *
 */
public class TaskAutomaticAssignment extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_AUTOMATIC_ASSIGNMENT = "module.workflow.ticketing.task_automatic_assignment.labelAutomaticAssignment";
    private static final String MESSAGE_AUTOMATIC_ASSIGN_TICKET_INFORMATION = "module.workflow.ticketing.task_automatic_assignment.information";

    // Properties
    private static final String PROPERTY_ACCOUNT_NUMBER_REGEXP = "workflow-ticketing.workflow.automatic_assignment.accountNumberRegexp";
    private static final String PROPERTY_ACCOUNT_NUMBER_FIELD_CODE = "workflow-ticketing.workflow.automatic_assignment.accountNumberFieldCode";
    private static final String PROPERTY_ACCOUNT_NUMBER_DOMAIN_LABEL = "workflow-ticketing.workflow.automatic_assignment.domainLabel";

    // Services
    @Inject
    private IAutomaticAssignmentService _automaticAssignmentService;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_AUTOMATIC_ASSIGNMENT, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = null;
        Ticket ticket = getTicket( nIdResourceHistory );
        TicketDomain domain = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain(  ) );

        if ( ( domain != null ) &&
                domain.getLabel(  ).equals( AppPropertiesService.getProperty( PROPERTY_ACCOUNT_NUMBER_DOMAIN_LABEL ) ) )
        {
            String strSuffix = getTicketAssignCriteria( ticket );

            if ( StringUtils.isNotBlank( strSuffix ) )
            {
                AdminUser adminUser = _automaticAssignmentService.getAssignedUser( getId(  ), strSuffix );

                if ( adminUser != null )
                {
                    AssigneeUser assigneeUser = new AssigneeUser( adminUser );
                    ticket.setAssigneeUser( assigneeUser );

                    List<Unit> listUnit = UnitHome.findByIdUser( adminUser.getUserId(  ) );
                    AssigneeUnit assigneeUnit = null;

                    if ( ( listUnit != null ) && ( listUnit.size(  ) > 0 ) )
                    {
                        assigneeUnit = new AssigneeUnit( listUnit.get( 0 ) );
                    }

                    if ( assigneeUnit != null )
                    {
                        ticket.setAssigneeUnit( assigneeUnit );
                    }

                    TicketHome.update( ticket );

                    strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( 
                                MESSAGE_AUTOMATIC_ASSIGN_TICKET_INFORMATION, Locale.FRENCH ),
                            adminUser.getFirstName(  ) + " " + adminUser.getLastName(  ),
                            ticket.getAssigneeUnit(  ).getName(  ) );
                }
            }
        }

        return strTaskInformation;
    }

    /**
     * returns suffix from ticket account number
     * @param ticket ticket
     * @return suffix load from ticket account number
     */
    private String getTicketAssignCriteria( Ticket ticket )
    {
        String strSuffix = null;

        if ( ( ticket.getListResponse(  ) != null ) && !ticket.getListResponse(  ).isEmpty(  ) )
        {
            for ( Response response : ticket.getListResponse(  ) )
            {
                if ( ( response.getEntry(  ) != null ) && StringUtils.isNotBlank( response.getEntry(  ).getCode(  ) ) &&
                        response.getEntry(  ).getCode(  )
                                    .equals( AppPropertiesService.getProperty( PROPERTY_ACCOUNT_NUMBER_FIELD_CODE ) ) )
                {
                    //field ff account number
                    if ( StringUtils.isNotBlank( response.getResponseValue(  ) ) &&
                            response.getResponseValue(  )
                                        .matches( AppPropertiesService.getProperty( PROPERTY_ACCOUNT_NUMBER_REGEXP ) ) )
                    {
                        Pattern pattern = Pattern.compile( AppPropertiesService.getProperty( 
                                    PROPERTY_ACCOUNT_NUMBER_REGEXP ) );
                        Matcher matcher = pattern.matcher( response.getResponseValue(  ) );

                        if ( matcher.find(  ) )
                        {
                            strSuffix = matcher.group( 1 );
                        }

                        break;
                    }
                }
            }
        }

        return strSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        super.doRemoveTaskInformation( nIdHistory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
        _automaticAssignmentService.removeConfig( getId(  ) );
        super.doRemoveConfig(  );
    }
}
