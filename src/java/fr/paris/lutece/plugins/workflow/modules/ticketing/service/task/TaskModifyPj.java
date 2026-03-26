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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPj;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPjHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.ticketing.service.strois.STroisService;
import fr.paris.lutece.plugins.ticketing.service.strois.StockageService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.service.util.ResponseUtil;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represent a task to modify a ticket
 */
public class TaskModifyPj extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_TASK_MODIFY_TICKET                         = "module.workflow.ticketing.task_modify_pj.labelModifyTicket";
    private static final String MESSAGE_MODIFY_TICKET_NO_MODIFICATIONS_INFORMATION    = "module.workflow.ticketing.task_modify_ticket.no_modifications_information";
    private static final String MESSAGE_MODIFY_TICKET_ATTACHMENT                      = "module.workflow.ticketing.task_modify_ticket_attachment.information";

    // Constant
    private static final String NOT_FILLED_INFORMATION                                = "module.workflow.ticketing.task_modify_ticket.no_information";
    private static final String SERVEUR_SIDE                                          = AppPropertiesService.getProperty( TicketingConstants.PROPERTY_STROIS_SERVEUR );


    @Inject
    private TicketFormService   _ticketFormService;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_MODIFY_TICKET, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        Ticket ticket = getTicket( nIdResourceHistory );


        // save current values, clear ticket
        List<Response> listCurrentResponse = ticket.getListResponse( );


        // Gets Map of Response by idEntry
        Map<Integer, List<Response>> currentResponsesByIdEntry = listCurrentResponse.stream( )
                .filter( r -> StringUtils.equals( r.getEntry( ).getEntryType( ).getBeanName( ), EntryTypeFile.BEAN_NAME ) ).collect( Collectors.groupingBy( r -> r.getEntry( ).getIdEntry( ) ) );

        for ( Map.Entry<Integer, List<Response>> mapEntry : currentResponsesByIdEntry.entrySet( ) )
        {

            Entry entry = EntryHome.findByPrimaryKey( mapEntry.getKey( ) );
            _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), locale, ticket );
            List<Response> newResponsesForEntry = ticket.getListResponse( ).stream( ).filter( r -> ( r.getEntry( ).getIdEntry( ) == entry.getIdEntry( ) ) && ( r.getIdResponse( ) == 0 ) )
                    .collect( Collectors.toList( ) );

            // Create new responses
            newResponsesForEntry.forEach( response ->
            {
                ResponseUtil.createResponse( response );
                TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
            } );

            // Delete old responses
            mapEntry.getValue( ).forEach( response ->
            {
                TicketPj pj = TicketPjHome.findIdPjFromIdResponse( response.getIdResponse( ) );
                if ( ( null != pj ) && ( pj.getStockageTicketing( ) != 0 ) )
                {
                    deletePj( pj );
                }
                TicketHome.removeTicketResponse( ticket.getId( ), response.getIdResponse( ) );
            } );

            strTaskInformation += I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_ATTACHMENT, locale );
        }
        TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
        _ticketFormService.removeTicketFromSession( request.getSession( ) );

        // In the case when there are no modifications
        if ( strTaskInformation.equals( StringUtils.EMPTY ) )
        {
            strTaskInformation = I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_NO_MODIFICATIONS_INFORMATION, locale );
        }

        return strTaskInformation;
    }

    private void deletePj( TicketPj pj )
    {
        String profil = STroisService.findTheProfilAndServerS3( pj.getStockageTicketing( ), SERVEUR_SIDE );
        StockageService stockageService = new StockageService( profil );
        stockageService.deleteFileOnS3Serveur( pj.getUrlTicketing( ) );
    }

}
