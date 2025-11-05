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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPj;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPjHome;
import fr.paris.lutece.plugins.ticketing.service.TicketTransfertPjService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * This class represents a task that centralize pj when one is added
 *
 */
public class TaskAutomaticCentralizeUsager extends AbstractTicketingTask
{
    // Messages
    private static final String        MESSAGE_AUTOMATIC_CENTRALIZATION_PJ = "module.workflow.ticketing.task_automatic_centralize_attachments.labelAutomaticCentralizationUsager";

    private static Plugin              _plugin                                = WorkflowTicketingPlugin.getPlugin( );

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        try
        {
            TransactionManager.beginTransaction( _plugin );

            // recuperation des PJ usager
            managePjforS3ForUsager( ticket );
        } catch ( Exception e )
        {
            TransactionManager.rollBack( _plugin );
            AppLogService.error( e );
        }
        TransactionManager.commitTransaction( _plugin );

        // no information stored in the history
        return null;
    }

    /**
     * Manage pj for usager
     *
     * @param ticket
     *            the ticket
     */
    private void managePjforS3ForUsager( Ticket ticket )
    {
        List<Integer> usagerAttachment = TicketTransfertPjService.findUsagerAttachment( ticket );

        List<Integer> idUsagerResponseToDelete = TicketPjHome.getIdResponsePjUsagerToDelete( ticket.getId( ) );

        if ( ( null != idUsagerResponseToDelete ) && !idUsagerResponseToDelete.isEmpty( ) )
        {
            TicketPjHome.removePjUsager( idUsagerResponseToDelete );
        }

        if ( ( null != usagerAttachment ) && !usagerAttachment.isEmpty( ) )
        {
            // usager true
            insertTicketPjFromList( usagerAttachment, ticket, true );
        }
    }

    /**
     * Keep in list only the id_file still exists in core_file
     *
     * @param idFileList
     *            the list of id file
     * @return the clean list of id file
     */
    private List<Integer> cleanIdCoreList( List<Integer> idFileList )
    {
        List<Integer> cleanidList = new ArrayList<>( );
        for ( Integer idFile : idFileList )
        {
            if ( TicketPjHome.isFileExistInCoreFile( idFile ) && !TicketPjHome.isFileExistInTicketPj( idFile ) )
            {
                File file = FileHome.findByPrimaryKey( idFile );
                if ( TicketPjHome.isFileExistInCorePhysicalFile( file.getPhysicalFile( ).getIdPhysicalFile( ) ) )
                {
                    cleanidList.add( idFile );
                }
            }

        }
        return cleanidList;
    }

    /**
     * Insert pj in ticketing_ticket_pj and update file name in core_file with a id File list
     *
     * @param idFileList
     *            the id pj list
     * @param ticket
     *            the ticket
     * @param isUsagerPj
     *            true if the pj is from usager otherwise false
     */
    private void insertTicketPjFromList( List<Integer> idFileList, Ticket ticket, boolean isUsagerPj )
    {
        idFileList = cleanIdCoreList( idFileList );
        insertTicketPj( idFileList, ticket, isUsagerPj );
    }

    /**
     * Insert pj in ticketing_ticket_pj from id file list
     *
     * @param idFileList
     *            the id file list
     * @param ticket
     *            the ticket
     * @param isUsagerPj
     *            true if the pj is from usager otherwise false
     */
    private void insertTicketPj( List<Integer> idFileList, Ticket ticket, boolean isUsagerPj )
    {
        if ( ( null != idFileList ) && !idFileList.isEmpty( ) )
        {
            for ( Integer idFile : idFileList )
            {
                TicketPj pj = new TicketPj( );
                for ( Response response : ticket.getListResponse( ) )
                {
                    if ( ( null != response.getFile( ) ) && ( response.getFile( ).getIdFile( ) == idFile ) )
                    {
                        pj.setIdResponse( response.getIdResponse( ) );
                        break;
                    }
                }
                if ( !TicketPjHome.isCoupleIdFileIdResponseExistInTicketPj( pj.getIdResponse( ), idFile ) )
                {
                    pj.setIdTicket( ticket.getId( ) );
                    pj.setIdFile( idFile );
                    pj.setUrlTicketing( "" );
                    pj.setStockageTicketing( 0 );
                    pj.setUsager( isUsagerPj );
                    TicketPjHome.create( pj );
                }
            }
        }
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_AUTOMATIC_CENTRALIZATION_PJ, locale );
    }
}

