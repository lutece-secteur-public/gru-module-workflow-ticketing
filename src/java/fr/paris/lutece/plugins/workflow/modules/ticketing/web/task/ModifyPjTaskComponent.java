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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskModifyPj}
 *
 */
public class ModifyPjTaskComponent extends TicketingTaskComponent
{

    // Templates
    private static final String TEMPLATE_TASK_MODIFY_TICKET_FORM = "admin/plugins/workflow/modules/ticketing/task_modify_pj.html";

    // Marks
    private static final String MARK_HANDLER = "upload_handler";
    private static final String MARK_ENTRY_ATTACHED_FILE = "entry_attached_files";

    @Inject
    private TicketFormService _ticketFormService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );

        Map<String, Object> model = getModel( ticket );

        model.put( MARK_HANDLER, TicketAsynchronousUploadHandler.getHandler( ) );
        List<Entry> listEntries = new ArrayList<>( );

        List<Response> responseList = ticket.getListResponse( );
        // Get the list of file type entry with distinct idEntry
        listEntries = responseList.stream( ).map( Response::getEntry )
                .filter( e -> StringUtils.equals( e.getEntryType( ).getBeanName( ), EntryTypeFile.BEAN_NAME ) )
                .collect( Collectors.groupingBy( Entry::getIdEntry ) ).values( ).stream( ).flatMap( group -> group.stream( ).limit( 1 ) )
                .collect( Collectors.toList( ) );

        _ticketFormService.saveTicketInSession( request.getSession( ), ticket );
        String htmlForm = _ticketFormService.getHtmlForm( listEntries, request.getLocale( ), false, request );

        model.put( MARK_ENTRY_ATTACHED_FILE, htmlForm );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MODIFY_TICKET_FORM, locale, model );

        return template.getHtml( );
    }

}
