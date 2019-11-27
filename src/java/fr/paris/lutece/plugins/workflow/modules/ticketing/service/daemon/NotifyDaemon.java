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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.daemon;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotifyDaemon extends Daemon
{

    // Services
    private static WorkflowService _workflowService;
    private static final IResourceHistoryService _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );


    private int nIdWorkflow = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID, TicketingConstants.PROPERTY_UNSET_INT );


    /**
     * Statut "En attente d'une réponse"
      */
    private int nIdStateWaiting = AppPropertiesService.getPropertyInt( "workflow.ticketing.state.id.waiting", TicketingConstants.PROPERTY_UNSET_INT );

    /**
     * Action "Relance automatique"
     */
    private int nIdActionRelance = AppPropertiesService.getPropertyInt( "workflow.ticketing.action.id.notify", TicketingConstants.PROPERTY_UNSET_INT );

    /*
     * Actions manuelles de sollicitation
     */
    // cas : action manuelle = "Solliciter un acteur terrain" [à partir d'un niveau 2] (id 317 en PROD)
    private int nIdActionSolliciterFromTerrainNiv2 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.sollicite.terrain_deux", TicketingConstants.PROPERTY_UNSET_INT );
    // cas : action manuelle = "Solliciter un acteur terrain" [à partir d'un niveau 3] (id 319)
    private int nIdActionSolliciterFromTerrainNiv3 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.sollicite.terrain_trois", TicketingConstants.PROPERTY_UNSET_INT );
    // cas : action manuelle = "Solliciter un contributeur" [à partir d'un niveau 2] (id 326)
    private int nIdActionSolliciterFromContribNiv2 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.sollicite.contrib_deux", TicketingConstants.PROPERTY_UNSET_INT );
    // cas : actionmanuelle = "Solliciter un contributeur" [à partir d'un niveau 3] (id 329)
    private int nIdActionSolliciterFromContribNiv3 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.sollicite.contrib_un", TicketingConstants.PROPERTY_UNSET_INT );

    /*
     * Actions "Retour de la sollicitation"
     */
    // cas : Si dernière action manuelle = "Solliciter un acteur terrain" [à partir d'un niveau 2] (id 317 en PROD), état d'arrivée = "À traiter"
    private int nIdActionRetourFromTerrainNiv2 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.return.sollicite.terrain_deux", TicketingConstants.PROPERTY_UNSET_INT );
    // cas : Si dernière action manuelle = "Solliciter un acteur terrain" [à partir d'un niveau 3] (id 319), état d'arrivée = "Escaladé niveau 3"
    private int nIdActionRetourFromTerrainNiv3 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.return.sollicite.terrain_trois", TicketingConstants.PROPERTY_UNSET_INT );
    // cas : Si dernière action manuelle = "Solliciter un contributeur" [à partir d'un niveau 2] (id 326) état d'arrivée = "À traiter"
    private int nIdActionRetourFromContribNiv2 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.return.sollicite.contrib_deux", TicketingConstants.PROPERTY_UNSET_INT );
    // cas : Si dernière action manuelle = "Solliciter un contributeur" [à partir d'un niveau 3] (id 329), état d'arrivée = "Escaladé niveau 3"
    private int nIdActionRetourFromContribNiv3 = AppPropertiesService.getPropertyInt( "workflow-ticketing.actions.id.return.sollicite.contrib_un", TicketingConstants.PROPERTY_UNSET_INT );

    // nombre de relances maximum avant retour de la sollicitation
    private int nbRelanceMax = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_RELANCE_NB_MAX, 3 );
    // durée en jours entre chaque relance
    private int nFrequence = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_RELANCE_FREQUENCE, 10 );

    @Override
    public void run( )
    {
        setLastRunLogs( processNotification() );
    }

    private String processNotification( )
    {
        StringBuilder sbLog = new StringBuilder(  );

        boolean isParamOK = isConfParamOK( sbLog );
        if ( !isParamOK) {
            return sbLog.toString();
        }

        // Date execution
        Date dateExecution = new Date( );

        int nNbTicketRelance = 0;
        int nNbTicketRetour = 0;

        // step 0: récupération des ressources au statut "En attente d'une reponse"

        List<Integer> listResourceWaitingId = _workflowService.getResourceIdListByIdState( nIdStateWaiting, Ticket.TICKET_RESOURCE_TYPE );

        if ( listResourceWaitingId == null || listResourceWaitingId.isEmpty() )
        {
            // pas de ticket
            sbLog.append( "Aucun ticket au statut en attente d'une réponse" );
            return sbLog.toString();
        }

        // boucle sur les tickets
        for (int nIdResource : listResourceWaitingId) {
            Ticket ticket = TicketHome.findByPrimaryKey( nIdResource );

            if ( ticket != null ) {
                int nNbRelance = ticket.getNbRelance( );
                Timestamp dateDerniereRelance = ticket.getDateDerniereRelance( );


                if ( dateDerniereRelance == null)
                {
                    // pas de date
                    processRelanceNoDate( ticket, dateExecution );
                    nNbTicketRelance++;
                }
                else if ( nNbRelance < nbRelanceMax )
                {
                    nNbTicketRelance = nNbTicketRelance + processRelance( ticket, dateDerniereRelance, dateExecution );
                }
                else
                {
                    nNbTicketRetour = nNbTicketRetour + processRetour( ticket, dateExecution );
                }
            }
        }
        sbLog.append( "Nombre de tickets au statut" ).append( _workflowService.getState( nIdStateWaiting, Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, null ).getName( ) ).append( "dont :" );
        sbLog.append( "\n   " ).append( nNbTicketRelance ).append( " tickets relancés" );
        sbLog.append( "\n   " ).append( nNbTicketRetour ).append( " tickets en retour de sollicitation" );



        return sbLog.toString();
    }

    /**
     * Vérifie les paramètres configurés
     * @param sbLog logs
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

        if (nIdActionRelance <= 0 || nIdStateWaiting <=0 ||
                    nIdActionSolliciterFromTerrainNiv2 <= 0 || nIdActionSolliciterFromTerrainNiv3 <= 0 ||
                    nIdActionSolliciterFromContribNiv2 <= 0 || nIdActionSolliciterFromContribNiv3 <= 0 ||
                    nIdActionRetourFromTerrainNiv2 <= 0 || nIdActionRetourFromTerrainNiv3 <= 0 ||
                    nIdActionRetourFromContribNiv2 <= 0 || nIdActionRetourFromContribNiv3 <= 0 )
        {
            sbLog.append( "Paramétrage des id de workflow GRU incorrect, vérifier fichiers properties" );
            AppLogService.error( "Paramétrage des id de workflow GRU incorrect, vérifier fichiers properties" );
            return false;
        }

        return true;
    }

    /**
     * Relance si pas de date de dernière relance
     * @param ticket ticket
     * @param dateExecution date d'exécution
     */
    private void processRelanceNoDate( Ticket ticket, Date dateExecution )
    {
        // Relance automatique
        _workflowService.doProcessAction( ticket.getId(), Ticket.TICKET_RESOURCE_TYPE, nIdActionRelance, null, null, null, true );
        ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
        ticket.setNbRelance( ticket.getNbRelance( ) + 1 );
    }

    /**
     * Relance avec contrôle de la date de dernière relance
     * @param ticket ticket
     * @param dateDerniereRelance date de dernière relance
     * @param dateExecution date d'exécution
     * @return nombre de tickets relancé (0 ou 1)
     */
    private int processRelance( Ticket ticket, Timestamp dateDerniereRelance, Date dateExecution)
    {
        Calendar calendarLimiteRelance = Calendar.getInstance();
        calendarLimiteRelance.setTime( dateDerniereRelance );
        // date à 00h 00mn 00s
        calendarLimiteRelance.set(Calendar.HOUR_OF_DAY, 0);
        calendarLimiteRelance.set(Calendar.MINUTE, 0);
        calendarLimiteRelance.set(Calendar.SECOND, 0);
        calendarLimiteRelance.set(Calendar.MILLISECOND, 0);
        calendarLimiteRelance.add(Calendar.DAY_OF_YEAR, nFrequence);
        // date dernière relance + n jours
        Date dateLimiteRelance = calendarLimiteRelance.getTime();

        if ( dateLimiteRelance.before( dateExecution ))
        {
            // Relance automatique
            _workflowService.doProcessAction( ticket.getId(), Ticket.TICKET_RESOURCE_TYPE, nIdActionRelance, null, null, null, true );
            ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
            ticket.setNbRelance( ticket.getNbRelance( ) + 1 );

            return 1;
        }

        return 0;
    }

    /**
     * Gère le retour en fonction de la dernière action manuelle
     * @param ticket ticket
     * @param dateExecution date d'exécution
     * @return nombre de tickets en retour (0 ou 1)
     */
    private int processRetour( Ticket ticket, Date dateExecution )
    {
        // retour sollicitation
        int nIdDerniereActionManuelle = getLastManualActionSollicitation( ticket.getId(), nIdWorkflow );

        if ( nIdDerniereActionManuelle == nIdActionSolliciterFromTerrainNiv2 )
        {
            _workflowService.doProcessAction( ticket.getId(), Ticket.TICKET_RESOURCE_TYPE, nIdActionRetourFromTerrainNiv2, null, null, null, true );
            ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
            ticket.setNbRelance( ticket.getNbRelance( ) + 1 );

            return 1;
        }
        else if ( nIdDerniereActionManuelle == nIdActionSolliciterFromTerrainNiv3 )
        {
            _workflowService.doProcessAction( ticket.getId(), Ticket.TICKET_RESOURCE_TYPE, nIdActionRetourFromTerrainNiv3, null, null, null, true );
            ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
            ticket.setNbRelance( ticket.getNbRelance( ) + 1 );

            return 1;
        }
        else if ( nIdDerniereActionManuelle == nIdActionSolliciterFromContribNiv2 )
        {
            _workflowService.doProcessAction( ticket.getId(), Ticket.TICKET_RESOURCE_TYPE, nIdActionRetourFromContribNiv2, null, null, null, true );
            ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
            ticket.setNbRelance( ticket.getNbRelance( ) + 1 );

            return 1;
        }
        else if ( nIdDerniereActionManuelle == nIdActionSolliciterFromContribNiv3 )
        {
            _workflowService.doProcessAction( ticket.getId(), Ticket.TICKET_RESOURCE_TYPE, nIdActionRetourFromContribNiv3, null, null, null, true );
            ticket.setDateDerniereRelance( new Timestamp( dateExecution.getTime( ) ) );
            ticket.setNbRelance( ticket.getNbRelance( ) + 1 );

            return 1;
        }
        else
        {
            AppLogService.error( "Dernière action manuelle non trouvée pour le ticket " + ticket.getId() );
        }

        return 0;
    }

    /**
     *
     * @param nIdResource identifiant de la ressource
     * @param nIdWorkflow identifiant du workflow
     * @return identifiant de la dernière action si dans la liste des actions de sollicitation
     */
    private int getLastManualActionSollicitation( int nIdResource, int nIdWorkflow )
    {
        List<ResourceHistory> listAllHistoryByResource = _resourceHistoryService.getAllHistoryByResource( nIdResource, Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow );

        if ( listAllHistoryByResource != null && !listAllHistoryByResource.isEmpty() )
        {
            // récupération de la dernière action manuelle de sollicitation (itération inverse)
            for (int i = listAllHistoryByResource.size(); i-- > 0; ) {
                ResourceHistory resourceHistory = listAllHistoryByResource.get( i );
                int nIdAction = resourceHistory.getAction().getId();
                if ( nIdAction == nIdActionSolliciterFromTerrainNiv2 || nIdAction == nIdActionSolliciterFromTerrainNiv3 ||
                             nIdAction == nIdActionSolliciterFromContribNiv2 || nIdAction == nIdActionSolliciterFromContribNiv3 )
                {
                    return nIdAction;
                }
            }
        }

        // renvoi -1 si pas dans la liste des actions de sollicitation manuelle
        return -1;
    }
}
