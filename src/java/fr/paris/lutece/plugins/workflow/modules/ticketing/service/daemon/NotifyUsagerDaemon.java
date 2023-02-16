/*
 * Copyright (c) 2002-2022, City of Paris
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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexerActionUtil;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

public class NotifyUsagerDaemon extends Daemon
{

    // Services
    private static WorkflowService _workflowService;
    private static final IResourceHistoryService _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );

    private int nIdWorkflow = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID,
            TicketingConstants.PROPERTY_UNSET_INT );
    private int                                  isMinuteMode                               = AppPropertiesService.getPropertyInt( "workflow.ticketing.delai.minute",
            TicketingConstants.PROPERTY_UNSET_INT );

    /**
     * Statut "En attente de compléments par l'usager"
     */
    private int                                  nIdStateWaitingUsager                    = AppPropertiesService.getPropertyInt( "workflow.ticketing.state.id.waiting.usager",
            TicketingConstants.PROPERTY_UNSET_INT );

    /**
     * Action "Relance automatique usager"
     */
    private int                                  nIdActionRelance                           = AppPropertiesService.getPropertyInt( "workflow.ticketing.action.id.notify.usager",
            TicketingConstants.PROPERTY_UNSET_INT );

    /*
     * Actions manuelles de sollicitation
     */
    // cas : action manuelle = "Demander compléments"
    private int                                  nIdActionDemandeComplementUsager           = AppPropertiesService.getPropertyInt( "workflow.ticketing.actions.id.ask.complement.usager",
            TicketingConstants.PROPERTY_UNSET_INT );

    /*
     * Actions "Retour de la sollicitation"
     */
    // cas : Si dernière action manuelle = "Demander compléments", état d'arrivée = "traité"
    private int                                  nIdActionRetourFromDemandeComplementUsager = AppPropertiesService.getPropertyInt( "workflow.ticketing.actions.id.return.ask.complement.usager",
            TicketingConstants.PROPERTY_UNSET_INT );

    // nombre de relances maximum avant retour de la sollicitation
    private int                                  nbRelanceMax                               = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_RELANCE_USAGER_NB_MAX, 1 );
    // durée en jours entre chaque relance
    private int                                  nFrequence                                 = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_RELANCE_USAGER_FREQUENCE, 15 );

    @Override
    public void run( )
    {
        setLastRunLogs( processNotification( ) );
    }

    private String processNotification( )
    {
        StringBuilder sbLog = new StringBuilder( );

        boolean isParamOK = isConfParamOK( sbLog );
        if ( !isParamOK )
        {
            return sbLog.toString( );
        }

        // Date execution
        Date dateExecution = new Date( );

        int nNbTicketRelance = 0;
        int nNbTicketRetour = 0;

        // step 0: récupération des ressources au statut "En attente de compléments par l'usager"

        List<Integer> listResourceWaitingId = _workflowService.getResourceIdListByIdState( nIdStateWaitingUsager, Ticket.TICKET_RESOURCE_TYPE );

        if ( ( listResourceWaitingId == null ) || listResourceWaitingId.isEmpty( ) )
        {
            // pas de ticket
            sbLog.append( "Aucun ticket au statut en attente de compléments par l'usager" );
            return sbLog.toString( );
        }

        // boucle sur les tickets
        for ( int nIdResource : listResourceWaitingId )
        {
            int nNbRelance = 0;
            Timestamp dateDerniereRelance = null;
            Ticket ticket = null;
            boolean isTicketUpdated = false;
            try
            {
                ticket = TicketHome.findByPrimaryKey( nIdResource );

                if ( ticket != null )
                {
                    nNbRelance = ticket.getNbRelance( );
                    dateDerniereRelance = ticket.getDateDerniereRelance( );

                    if ( ( dateDerniereRelance == null ) || ( nNbRelance == 0 ) )
                    {
                        List<ResourceHistory> allHistory = _resourceHistoryService.getAllHistoryByResource( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE,
                                nIdWorkflow );
                        allHistory.sort( Comparator.comparing( ResourceHistory::getCreationDate ).reversed( ) ); // tri du plus récent au plus ancien
                        for ( ResourceHistory resourceHistory : allHistory )
                        {
                            // pas de dernière relance, récupération de la date de dernière sollicitation
                            if ( ( resourceHistory.getAction( ).getId( ) == nIdActionDemandeComplementUsager ) )
                            {
                                int nRelance = processRelance( ticket, resourceHistory.getCreationDate( ), dateExecution );
                                nNbTicketRelance = nNbTicketRelance + nRelance;
                                isTicketUpdated = ( nRelance == 1 );
                                break;
                            }
                        }
                    }
                    else
                        if ( nNbRelance < nbRelanceMax )
                        {
                            int nRelance = processRelance( ticket, dateDerniereRelance, dateExecution );
                            nNbTicketRelance = nNbTicketRelance + nRelance;
                            isTicketUpdated = ( nRelance == 1 );
                        }
                        else
                        {
                            int nRelance = processRetour( ticket, dateDerniereRelance, dateExecution );
                            nNbTicketRetour = nNbTicketRetour + nRelance;
                            isTicketUpdated = ( nRelance == 1 );
                        }
                }
            }
            catch( Exception e )
            {
                AppLogService.error( "Erreur du traitement du ticket " + nIdResource, e );

                if ( ( ticket != null ) && ( ticket.getNbRelance( ) != nNbRelance ) )
                {
                    // si le ticket a été mis à jour mais a eu une erreur
                    ticket.setNbRelance( nNbRelance );
                    ticket.setDateDerniereRelance( dateDerniereRelance );
                    TicketHome.update( ticket, false );
                    isTicketUpdated = true;
                }
            }
            finally
            {
                if ( isTicketUpdated )
                {
                    // Index: store the Ticket in the table for the daemon
                    IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
                }
            }
        }

        sbLog.append( "Nombre de tickets au statut " )
        .append( _workflowService.getState( nIdStateWaitingUsager, Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, null ).getName( ) ).append( " dont :" );
        sbLog.append( "\n   " ).append( nNbTicketRelance ).append( " tickets relancés" );
        sbLog.append( "\n   " ).append( nNbTicketRetour ).append( " tickets en retour de sollicitation" );

        AppLogService.info( sbLog.toString( ) );

        return sbLog.toString( );
    }

    /**
     * Vérifie les paramètres configurés
     *
     * @param sbLog
     *            logs
     * @return true si conf OK, false sinon
     */
    private boolean isConfParamOK( StringBuilder sbLog )
    {
        if ( _workflowService == null )
        {
            _workflowService = WorkflowService.getInstance( );
        }

        if ( ( nIdWorkflow <= 0 ) || !_workflowService.isAvailable( ) )
        {
            // pas de workflow trouvé
            sbLog.append( "Workflow GRU non trouvé" );
            AppLogService.error( "Workflow GRU non trouvé" );
            return false;
        }

        if ( ( nIdActionRelance <= 0 ) || ( nIdStateWaitingUsager <= 0 ) || ( nIdActionDemandeComplementUsager <= 0 ) || ( nIdActionRetourFromDemandeComplementUsager <= 0 ) )
        {
            sbLog.append( "Paramétrage des id de workflow GRU incorrect, vérifier fichiers properties" );
            AppLogService.error( "Paramétrage des id de workflow GRU incorrect, vérifier fichiers properties" );
            return false;
        }

        if ( nbRelanceMax < 1 )
        {
            // relance desactivée
            sbLog.append( "Paramétrage relance_auto.nb_relance_max inf. à 1, relance desactivée" );
            AppLogService.error( "Paramétrage relance_auto.nb_relance_max inférieur à 1, relance desactivée" );
            return false;
        }

        return true;
    }

    /**
     * Relance avec contrôle de la date de dernière relance
     *
     * @param ticket
     *            ticket
     * @param dateDerniereRelance
     *            date de dernière relance
     * @param dateExecution
     *            date d'exécution
     * @return nombre de tickets relancé (0 ou 1)
     */
    private int processRelance( Ticket ticket, Timestamp dateDerniereRelance, Date dateExecution )
    {
        Date dateLimiteRelance = getDatelimiteRelance( dateDerniereRelance );

        if ( dateLimiteRelance.before( dateExecution ) )
        {
            ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
            ticket.setNbRelance( ticket.getNbRelance( ) + 1 );

            // mise à jour du ticket (sans màj de la date d'update)
            // update date true si retour de sollicitation, false si relance auto
            TicketHome.update( ticket, false );

            // Relance automatique
            _workflowService.doProcessAction( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdActionRelance, null, null, null, true );

            return 1;
        }

        return 0;
    }

    /**
     * Gère le retour en fonction de la dernière action manuelle
     *
     * @param ticket
     *            ticket
     * @param dateExecution
     *            date d'exécution
     * @return nombre de tickets en retour (0 ou 1)
     */
    private int processRetour( Ticket ticket, Timestamp dateDerniereRelance, Date dateExecution )
    {
        Date dateLimiteRelance = getDatelimiteRelance( dateDerniereRelance );

        if ( dateLimiteRelance.before( dateExecution ) )
        {
            // retour sollicitation
            int nIdDerniereActionManuelle = getLastManualActionSollicitation( ticket.getId( ), nIdWorkflow );

            if ( nIdDerniereActionManuelle == nIdActionDemandeComplementUsager )
            {
                ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
                // remise à 0
                ticket.setNbRelance( 0 );

                // mise à jour du ticket
                // update date true si retour de sollicitation, false si relance auto
                TicketHome.update( ticket, true );

                _workflowService.doProcessAction( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdActionRetourFromDemandeComplementUsager, null, null, null, true );

                return 1;
            }
            else
            {
                AppLogService.error( "Dernière action manuelle non trouvée pour le ticket " + ticket.getId( ) );
            }
        }
        return 0;
    }

    /**
     * Renvoie la date de prochaine relance
     *
     * @param dateDerniereRelance
     *            date de dernière relance
     * @return date limite de relance
     */
    private Date getDatelimiteRelance( Timestamp dateDerniereRelance )
    {
        Calendar calendarLimiteRelance = Calendar.getInstance( );

        if ( isMinuteMode == 1 )
        {
            calendarLimiteRelance.setTime( dateDerniereRelance );
            calendarLimiteRelance.add( Calendar.MINUTE, nFrequence );
            // date dernière relance + n minutes
        }
        else
        {
            // date à 00h 00mn 00s
            calendarLimiteRelance.set( Calendar.HOUR_OF_DAY, 0 );
            calendarLimiteRelance.set( Calendar.MINUTE, 0 );
            calendarLimiteRelance.set( Calendar.SECOND, 0 );
            calendarLimiteRelance.set( Calendar.MILLISECOND, 0 );
            calendarLimiteRelance.add( Calendar.DAY_OF_YEAR, nFrequence );
            // date dernière relance + n jours
        }
        return calendarLimiteRelance.getTime( );
    }


    /**
     *
     * @param nIdResource
     *            identifiant de la ressource
     * @param nIdWorkflow
     *            identifiant du workflow
     * @return identifiant de la dernière action si dans la liste des actions de sollicitation
     */
    private int getLastManualActionSollicitation( int nIdResource, int nIdWorkflow )
    {
        List<ResourceHistory> listAllHistoryByResource = _resourceHistoryService.getAllHistoryByResource( nIdResource, Ticket.TICKET_RESOURCE_TYPE,
                nIdWorkflow );

        if ( ( listAllHistoryByResource != null ) && !listAllHistoryByResource.isEmpty( ) )
        {
            // récupération de la dernière action manuelle de sollicitation (itération inverse)
            for ( int i = listAllHistoryByResource.size( ); i-- > 0; )
            {
                ResourceHistory resourceHistory = listAllHistoryByResource.get( i );
                int nIdAction = resourceHistory.getAction( ).getId( );
                if ( ( nIdAction == nIdActionDemandeComplementUsager ) )
                {
                    return nIdAction;
                }
            }
        }

        // renvoi -1 si pas dans la liste des actions de sollicitation manuelle
        return -1;
    }
}
