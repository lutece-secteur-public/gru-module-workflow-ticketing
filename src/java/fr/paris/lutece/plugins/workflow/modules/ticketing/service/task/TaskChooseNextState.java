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

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketInitService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskChooseNextStateConfig;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceHistoryFactory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.exception.WorkflowRuntimeException;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.sql.TransactionManager;

public class TaskChooseNextState extends AbstractTicketingTask
{
    private static final String      MESSAGE_MARK_TITLE                    = "module.workflow.ticketing.task_choose_next_state.labelChooseNextState";

    private static final String      BEAN_CHOOSE_NEXT_STATE_CONFIG_SERVICE = "workflow-ticketing.taskChooseNextStateConfigService";

    private static final String      PROPERTY_CHANNEL_SCAN_NAME            = "ticketing.channelScan.name";
    private String                   _strchannelScanName                   = AppPropertiesService.getProperty( PROPERTY_CHANNEL_SCAN_NAME );

    private static final String      PROPERTY_AUTOMATIC_INIT_ACTION        = "ticketing.taskAutomaticInitAction.id";
    private String                   _nActionInitWorkflowId                = AppPropertiesService.getProperty( PROPERTY_AUTOMATIC_INIT_ACTION );

    private IResourceWorkflowService _resourceWorkflowService              = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );

    private static TicketInitService _ticketInitService                    = SpringContextService.getBean( TicketInitService.BEAN_NAME );

    @Inject
    private IWorkflowService         _serviceWorkflow;

    @Inject
    private ITaskService             _taskService;

    @Inject
    protected IActionService         _actionService;

    @Inject
    protected IStateService          _stateService;

    @Inject
    private IResourceHistoryFactory  _resourceHistoryFactory;

    @Inject
    @Named( BEAN_CHOOSE_NEXT_STATE_CONFIG_SERVICE )
    private ITaskConfigService       _taskConfigService;



    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        Ticket ticket = getTicket( nIdResourceHistory );
        IWorkflowService workflowService = SpringContextService.getBean( "workflow.workflowService" );

        Action action = _actionService.findByPrimaryKey( Integer.parseInt( _nActionInitWorkflowId ) );
        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( action.getId( ), Locale.getDefault( ) );

        ITask task = listActionTasks.get( 0 );

        State state = _stateService.getInitialState( 301 );

        ResourceWorkflow resource = new ResourceWorkflow( );
        resource.setIdResource( ticket.getId( ) );
        resource.setResourceType( "ticket" );
        resource.setWorkFlow( workflowService.findByPrimaryKey( state.getWorkflow( ).getId( ) ) );
        resource.setState( state );

        TaskChooseNextStateConfig config = _taskConfigService.findByPrimaryKey( this.getId( ) );
        if ( task != null )
        {
            try
            {
                chooseNewState( resource.getIdResource( ), resource.getResourceType( ), task, config, 301, resource.getState( ).getId( ), ticket );
            } catch ( Exception e )
            {
                AppLogService.error( "Unexpected Error", e );
            }
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_MARK_TITLE, locale );
    }

    @Override
    public void doRemoveConfig( )
    {
        _taskConfigService.remove( getId( ) );
    }

    private void chooseNewState( int nIdResource, String strResourceType, ITask task, TaskChooseNextStateConfig config, int nIdWorkflow, int oldState, Ticket ticket )
    {
        int newState = -1;

        if ( !ticket.getChannel( ).getLabel( ).equals( _strchannelScanName ) )
        {
            newState = config.getIdStateOK( );
        } else
        {
            newState = config.getIdStateKO( );
        }

        if ( ( newState != -1 ) && ( newState != oldState ) )
        {
            doChangeState( task, nIdResource, strResourceType, nIdWorkflow, newState );
        }
    }

    private void doChangeState( ITask task, int nIdResource, String strResourceType, int nIdWorkflow, int newState )
    {
        Locale locale = I18nService.getDefaultLocale( );
        State state = _stateService.findByPrimaryKey( newState );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

        if ( ( state != null ) && ( action != null ) )
        {
            // Update Resource
            ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType, nIdWorkflow );
            resourceWorkflow.setState( state );
            _resourceWorkflowService.update( resourceWorkflow );

            _ticketInitService.doProcessAutomaticReflexiveActions( nIdResource, strResourceType, state.getId( ), null, locale, null );
        }
    }

    public void doProcessAction( int nIdResource, String strResourceType, int nIdAction, Integer nIdExternalParent, HttpServletRequest request, Locale locale, boolean bIsAutomatic,
            String strUserAccessCode, User user )
    {
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( ( action == null ) )
        {
            return;
        }

        ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType, action.getWorkflow( ).getId( ) );

        if ( resourceWorkflow == null )
        {
            resourceWorkflow = _serviceWorkflow.getInitialResourceWorkflow( nIdResource, strResourceType, action.getWorkflow( ), nIdExternalParent );

            if ( resourceWorkflow != null )
            {
                _resourceWorkflowService.create( resourceWorkflow );
            }
        }

        // Create ResourceHistory
        ResourceHistory resourceHistory = _resourceHistoryFactory.newResourceHistory( nIdResource, strResourceType, action, strUserAccessCode, bIsAutomatic, user );
        _resourceHistoryService.create( resourceHistory );

        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( nIdAction, locale );

        for ( ITask task : listActionTasks )
        {
            task.setAction( action );

            try
            {
                task.processTask( resourceHistory.getId( ), request, locale, user );
            } catch ( Exception e )
            {
                // Revert the creation of the resource history
                _resourceHistoryService.remove( resourceHistory.getId( ) );

                throw new WorkflowRuntimeException( "WorkflowService - Error when executing task ID " + task.getId( ), e );
            }
        }

        // Reload the resource workflow in case a task had modified it
        resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType, action.getWorkflow( ).getId( ) );
        resourceWorkflow.setState( action.getStateAfter( ) );
        resourceWorkflow.setExternalParentId( nIdExternalParent );
        _resourceWorkflowService.update( resourceWorkflow );

    }

    /**
     * Proceed automatic reflexive actions of state given in parameter. This method should be called anytime a service changed the state of a resource without
     * proceeding a workflow action
     *
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @param nIdState
     *            the state of the resource id
     * @param nIdExternalParent
     *            the external parent id*
     * @param locale
     *            locale
     * @param user
     *            the user
     */
    public void doProcessAutomaticReflexiveActions( int nIdResource, String strResourceType, int nIdState, Integer nIdExternalParent, Locale locale, User user )
    {
        try
        {
            TransactionManager.beginTransaction( null );

            doProcessAutomaticReflexiveActionsNext( nIdResource, strResourceType, nIdState, nIdExternalParent, locale, user );
            TransactionManager.commitTransaction( null );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( null );
            throw new AppException( e.getMessage( ), e );
        }
    }

    public void doProcessAutomaticReflexiveActionsNext( int nIdResource, String strResourceType, int nIdState, Integer nIdExternalParent, Locale locale, User user )
    {
        State state = _stateService.findByPrimaryKey( nIdState );
        ActionFilter actionFilter = new ActionFilter( );
        actionFilter.setIdWorkflow( state.getWorkflow( ).getId( ) );
        actionFilter.setIdStateBefore( state.getId( ) );
        actionFilter.setAutomaticReflexiveAction( true );

        List<Action> listAction = _actionService.getListActionByFilter( actionFilter );

        if ( CollectionUtils.isNotEmpty( listAction ) )
        {
            for ( Action action : listAction )
            {
                doProcessAction( nIdResource, strResourceType, action.getId( ), nIdExternalParent, null, locale, true, null, user );
            }
        }
    }

}

