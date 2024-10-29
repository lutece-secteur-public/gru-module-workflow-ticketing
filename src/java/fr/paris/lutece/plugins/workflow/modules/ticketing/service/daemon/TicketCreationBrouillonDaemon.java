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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.daemon;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.StringJoiner;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * Daemon used to Pj Migration Tickets
 */
public class TicketCreationBrouillonDaemon extends Daemon
{

    // Services
    private static WorkflowService     _workflowService;

    /**
     * Constructor
     */
    public TicketCreationBrouillonDaemon( )
    {
        super( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        StringJoiner sb = new StringJoiner( "\n\r" );

        sb.add( "Début de la migration" );
        createBrouillon( sb );
        sb.add( "Fin de la migration" );
        setLastRunLogs( sb.toString( ) );
    }


    /**
     * Create a ticket draft if file exist from a postal mail
     *
     * @param sb
     *            the logs
     */
    private void createBrouillon( StringJoiner sb )
    {
        /*
         * List<ReferentielScanner> referentielScannerList = ReferentielScannerHome.getReferentielScannersList( );
         *
         * List<ReferentielScanner> referentielScannerForTypeRoot = referentielScannerList.stream( ).filter( r -> r.getTypeScanner( ) == 0 ).collect( Collectors.toList( ) );
         *
         * List<ReferentielScanner> referentielScannerForTypeFolder = referentielScannerList.stream( ).filter( r -> r.getTypeScanner( ) == 1 ).collect( Collectors.toList( ) );
         *
         * for ( ReferentielScanner referentielScannerRoot : referentielScannerForTypeRoot ) {
         *
         * }
         *
         * for ( ReferentielScanner referentielScannerFolder : referentielScannerForTypeFolder ) {
         *
         * }
         */

        Ticket ticket = new Ticket( );
        TicketCategory category = TicketCategoryHome.findByPrimaryKey( 20 );
        ticket.setTicketCategory(category);
        ticket.setIdUserTitle( 0 );
        ticket.setUserTitle( "" );
        ticket.setFirstname( "" );
        ticket.setLastname( "" );
        ticket.setEmail( "" );
        ticket.setTicketComment( "" );
        ticket.setDateUpdate( new Timestamp( new Date( ).getTime( ) ) );
        ticket.setDateCreate( new Timestamp( new Date( ).getTime( ) ) );
        ticket.setIdContactMode( 2 );

        TicketHome.create( ticket );

        User user = AdminUserHome.findByPrimaryKey( 5 );
        Locale local = I18nService.getDefaultLocale( );

        WorkflowCapableJspBean.doProcessNextWorkflowActionInit( ticket, local, user );

        // Immediate indexation of the Ticket
        WorkflowCapableJspBean.immediateTicketIndexing( ticket.getId( ) );

    }
}
