/*
 * Copyright (c) 2002-2023, City of Paris
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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.search.IndexerAction;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * This class represents a task to assign to me
 *
 */
public class TaskIndexTicket extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_INDEX_TICKET = "module.workflow.ticketing.task_index_ticket.labelIndexTicket";
    private static final String PROPERTY_WORKFLOW_ACTION_ID_NEW = "ticketing.workflow.action.id.new";

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );
        if ( ticket == null )
        {
            AppLogService.error( "No ticket found for idResourecehistory : " + nIdResourceHistory );
        }
        else
        {
            int nIdWorkflow = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID, TicketingConstants.PROPERTY_UNSET_INT );
            State state = WorkflowService.getInstance( ).getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, null );

            IndexerAction indexerAction = new IndexerAction( );
            indexerAction.setIdTicket( ticket.getId( ) );

            if ( ( state == null ) || ( state.getId( ) == AppPropertiesService.getPropertyInt( PROPERTY_WORKFLOW_ACTION_ID_NEW, 301 ) ) )
            {
                indexerAction.setIdTask( IndexerAction.TASK_CREATE );
            }
            else
            {
                indexerAction.setIdTask( IndexerAction.TASK_MODIFY );
            }

            IndexerActionHome.create( indexerAction );
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_INDEX_TICKET, locale );
    }
}
