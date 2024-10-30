/*
 * Copyright (c) 2002-2024, City of Paris
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.IResourceHistoryService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.ResourceHistory;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a task to register the channel on ticket creation
 *
 */
public class TaskRegisterChannelCourrierPostal extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_REGISTER_CHANNEL = "module.workflow.ticketing.task_register_channel.labelRegisterChannel";

    private static final String       PROPERTY_CHANNEL_SCAN_NAME = "ticketing.channelScan.name";
    private String                    _strchannelScanName        = AppPropertiesService.getProperty( PROPERTY_CHANNEL_SCAN_NAME );

    // Services
    @Inject
    protected IResourceHistoryService _resourceHistoryServiceTicketing;

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strChannel = StringUtils.EMPTY;
        int idChannel = TicketingConstants.NO_ID_CHANNEL;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            Channel channel = ChannelHome.findByName( _strchannelScanName );
            idChannel = channel.getId( );


            ResourceHistory resourceHistory = new ResourceHistory( );
            resourceHistory.setIdHistory( nIdResourceHistory );
            resourceHistory.setIdChannel( idChannel );
            _resourceHistoryServiceTicketing.create( resourceHistory, WorkflowUtils.getPlugin( ) );
        }

        return strChannel;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_REGISTER_CHANNEL, locale );
    }
}
