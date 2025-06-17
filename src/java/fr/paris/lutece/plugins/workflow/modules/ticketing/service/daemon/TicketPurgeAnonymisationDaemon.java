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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.daemon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexerException;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPjHome;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.IResourceHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.purgeanonymisation.PurgeAnonymisationService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * The purge daemon for tickets to be anonymized.
 */
public class TicketPurgeAnonymisationDaemon extends Daemon
{
    private static final int DELAI_ANONYMISATION = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_DELAI, 1096 );
    private static final int MAX_ANONYMISATION_PAR_DOMAINE = PluginConfigurationService
            .getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_TICKET_MAX_DOMAINE, 200 );


    private static IResourceHistoryDAO daoResourceHist = SpringContextService.getBean( IResourceHistoryDAO.BEAN_SERVICE );
    // Services
    private PurgeAnonymisationService  purgeAnonymisationService     = SpringContextService.getBean( PurgeAnonymisationService.BEAN_SERVICE );;

    private static Plugin plugin = WorkflowTicketingPlugin.getPlugin( );

    /*
     * Constructor
     */
    public TicketPurgeAnonymisationDaemon( )
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

        sb.add( "Début de l'anonymisation" );
        anonymisationPurge( sb );
        sb.add( "Fin de l'anonymisation" );
        setLastRunLogs( sb.toString( ) );
    }

    /**
     * Purge the ticket to anonymize
     *
     * @param sb
     *            the logs
     */
    public void anonymisationPurge( StringJoiner sb )
    {
        List<Integer> allIdDomaines = TicketCategoryHome.selectIdCategoriesDomainList( );

        for ( Integer idCategory : allIdDomaines )
        {
            TicketCategory categorieDomaine = TicketCategoryService.getInstance( true ).findCategoryById( idCategory );

            if ( null != categorieDomaine )
            {
                anonymizePurgeByDomaine( categorieDomaine, sb );
            }
        }
    }

    /**
     * Purge the ticket to anonymize by domaine
     *
     * @param categorieDomaine
     *            the category domaine
     * @param sb
     *            The logs
     */
    private void anonymizePurgeByDomaine( TicketCategory categorieDomaine, StringJoiner sb )
    {

        List<Integer> allChildrenForADomaine = TicketCategoryService.getInstance( true ).getAllChildren( categorieDomaine );

        int delaiAnonymisation = ( !categorieDomaine.getDelaiAnonymisation( ).trim( ).isEmpty( ) )
                ? Integer.parseInt( categorieDomaine.getDelaiAnonymisation( ).trim( ) )
                        : DELAI_ANONYMISATION;

        if ( !allChildrenForADomaine.isEmpty( ) )
        {
            java.sql.Date date = findDateClotureForAnonymisationDomaine( delaiAnonymisation, categorieDomaine, sb );

            if ( allChildrenForADomaine.isEmpty( ) )
            {
                allChildrenForADomaine.add( categorieDomaine.getId( ) );
            }

            List<Integer> listIdTickets = TicketHome.getForAnonymisationForDomaine( date, allChildrenForADomaine, MAX_ANONYMISATION_PAR_DOMAINE );

            if ( !listIdTickets.isEmpty( ) )
            {
                sb.add( "nombre de tickets à anonymiser : " + listIdTickets.size( ) );
                for ( Integer idTicket : listIdTickets )
                {
                    if ( ( null != idTicket ) && ( idTicket != 0 ) )
                    {
                        try
                        {
                            TransactionManager.beginTransaction( plugin );
                            // suppression des données historique du ticket
                            cleanHistoryData( idTicket );

                            // delete address
                            TicketHome.removeAddress( idTicket );

                            // suppression des pj
                            purgeAnonymisationService.deleteAllAttachment( idTicket );

                            // suppression genatt response
                            removeFromGenattResponseAndPjCentralized( idTicket );

                            // retrait indexation
                            removeIndexingTicketToAnonymize( idTicket, sb );

                            TicketHome.removeJustTicketAndTicketResponse( idTicket );
                        }
                        catch( Exception e )
                        {
                            TransactionManager.rollBack( plugin );
                            sb.add( "Annulation de l'anonymisation pour le domaine" + categorieDomaine.getLabel( ) + "idTicket : " + idTicket );
                            AppLogService.error( e );
                        }
                        TransactionManager.commitTransaction( plugin );
                    }
                }
            }
            else
            {
                sb.add( "aucun ticket à anonymiser " );
            }
        }

    }

    ////// GENERAL

    /**
     * Find the date closed max for a domaine accross a delay
     *
     * @param delaiAnonymisation
     *            the delay for a domaine
     * @param category
     *            the category of the ticket to anonymize
     * @param sb
     *            the logs
     * @return the date for cloture for a domaine
     */
    private java.sql.Date findDateClotureForAnonymisationDomaine( int delaiAnonymisation, TicketCategory category, StringJoiner sb )
    {
        Date date = new Date( );
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        Calendar calDate = Calendar.getInstance( );
        calDate.setTime( date );
        calDate.add( Calendar.DAY_OF_YEAR, -delaiAnonymisation );
        date = calDate.getTime( );
        sb.add( "- anonymisation des tickets du domaine " + category.getLabel( ) + " dont la date de clôture est plus ancienne que le " + sdf.format( date ) );
        return new java.sql.Date( date.getTime( ) );
    }

    /**
     * Clean historic data with message exchanges
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void cleanHistoryData( int idTicket )
    {
        purgeTicketHistoryFromUsager( idTicket );
        purgeTicketHistoryFromAgent( idTicket );
    }

    private void removeFromGenattResponseAndPjCentralized( Integer idTicket )
    {
        List<Integer> idResponseList = TicketPjHome.removeAllIdResponseToDelete( idTicket );
        if ( ( null != idResponseList ) && !idResponseList.isEmpty( ) )
        {
            TicketPjHome.removeGenattResponseByIdResponseList( idResponseList );
        }
        // Retrait des lignes de PJ pour les agents (id_response = 0)
        TicketPjHome.removeByIdTicket( idTicket );
    }

    /**
     * Remove indexation of a Ticket for the anonymisation
     *
     * @param idTicket
     *            the id of the Ticket to index
     */
    protected void removeIndexingTicketToAnonymize( int idTicket, StringJoiner sb )
    {
        try
        {
            TicketIndexer ticketIndexer = new TicketIndexer( );
            ticketIndexer.deleteTicketIndex( idTicket );
        } catch ( TicketIndexerException ticketIndexerException )
        {
            sb.add( "Le ticket id " + idTicket + " est en attente pour retrait indexation" );

            // The indexation of the Ticket fail, we will store the Ticket in the table for the daemon
            IndexerActionHome.removeByIdTicket( idTicket );
        }
    }

    ////// USAGER

    /**
     * Anonymize historic data with message exchanges From Usager
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void purgeTicketHistoryFromUsager( int idTicket )
    {
        // suppression message externe lié au message
        purgeAnonymisationService.removeMessageExternalUserByIdTicket( idTicket );
    }


    ////// AGENT

    /**
     * Anonymize historic data with message exchanges From Usager
     *
     * @param ticket
     *            the ticket to anonymize
     */
    private void purgeTicketHistoryFromAgent( int idTicket )
    {
        List<Integer> idHistoryList = daoResourceHist.getIdHistoryListByResource( idTicket, plugin );

        if ( ( null != idHistoryList ) && !idHistoryList.isEmpty( ) )
        {
            purgeAnonymisationService.purgeHistoryAnonymisation( idHistoryList, plugin );

            purgeAnonymisationService.purgeHistoryWorkflowTablesAnonymisation( idHistoryList, plugin );
        }
        purgeAnonymisationService.purgeWorkflowResourceAnonymisation( idTicket, plugin );
    }



}
