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

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.information.TaskInformation;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.information.ITaskInformationService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.WorkflowTicketingUtils;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.Locale;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents a Task for Ticketing
 *
 */
public abstract class AbstractTicketingTask extends SimpleTask
{
    private static final String LOG_ERROR_SAVE_INFORMATION = "Error when saving message '{0}' for resourceId {1} and taskId {2}";
    protected final static String REDIRECT_TO_LIST = "list";

    // Services
    @Inject
    protected IResourceHistoryService _resourceHistoryService;
    @Inject
    protected ITaskInformationService _taskInformationService;

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = processTicketingTask( nIdResourceHistory, request, locale );

        if ( !StringUtils.isEmpty( strTaskInformation ) )
        {
            try
            {
                TaskInformation taskInformation = new TaskInformation(  );
                taskInformation.setIdResourceHistory( nIdResourceHistory );
                taskInformation.setIdTask( this.getId(  ) );
                taskInformation.setValue( strTaskInformation );
                taskInformation.setIdChannel( this.getIdChannel(  ) );
                _taskInformationService.create( taskInformation, WorkflowUtils.getPlugin(  ) );
            }
            catch ( Exception e )
            {
                String strErrorMessage = MessageFormat.format( LOG_ERROR_SAVE_INFORMATION, strTaskInformation,
                        nIdResourceHistory, this.getId(  ) );
                AppLogService.error( strErrorMessage );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        _taskInformationService.removeByHistory( nIdHistory, this.getId(  ), WorkflowUtils.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
        _taskInformationService.removeByTask( this.getId(  ), WorkflowUtils.getPlugin(  ) );
    }

    /**
     * Gives the ticket from resourceHistory
     * @param nIdResourceHistory the resourceHistory id
     * @return the ticket if the resourceHistory corresponds to a ticket, {@code null} otherwise
     */
    protected Ticket getTicket( int nIdResourceHistory )
    {
        return WorkflowTicketingUtils.findTicketByIdHistory( nIdResourceHistory );
    }

    /**
     * Get id channel
     * @return the channel id
     */
    protected int getIdChannel(  )
    {
        return TicketingConstants.NO_ID_CHANNEL;
    }

    /**
     * Process the Ticketing task
     * @param nIdResourceHistory the resource history id
     * @param request the request
     * @param locale locale
     * @return the message to save
     */
    protected abstract String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale );
}
