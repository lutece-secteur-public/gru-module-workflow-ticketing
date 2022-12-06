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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.supportentity.SupportEntity;
import fr.paris.lutece.plugins.ticketing.business.supportentity.SupportEntityHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.information.TaskInformation;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.information.ITaskInformationService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.web.task.SimpleTaskComponent;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class represents a TaskComponent for Ticketing
 *
 */
public class TicketingTaskComponent extends SimpleTaskComponent
{
    protected static final String ATTRIBUTE_HIDE_NEXT_STEP_BUTTON = "hide_next_button";

    private static final String MESSAGE_DEFAULT_LABEL_ENTITY_TASK_FORM = "module.workflow.ticketing.task_assign_up_ticket.default.label.entity";
    private static final String MESSAGE_DEFAULT_LABEL_VSP_RULE_TASK_FORM = "module.workflow.ticketing.task_vsp_ticket.default.label.rule";

    // Markers
    private static final String MARK_ERRORS = "errors";
    private static final String MARK_TICKET = "ticket";



    // SERVICES
    @Inject
    private ITaskInformationService _taskInformationService;
    private List<ErrorMessage> _listErrors;

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        String strTaskInformation = StringUtils.EMPTY;

        TaskInformation taskInformation = _taskInformationService.findByPrimaryKey( nIdHistory, task.getId( ), WorkflowUtils.getPlugin( ) );

        if ( taskInformation != null )
        {
            strTaskInformation = "<p>" + taskInformation.getValue( ) + "</p>";
        }

        return "<div class='mt-30'>" + strTaskInformation + "</div>";
    }

    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        return null;
    }

    /**
     * Add an error message
     *
     * @param strMessage
     *            The message
     */
    protected void addError( String strMessage )
    {
        _listErrors.add( new MVCMessage( strMessage ) );
    }

    /**
     * Add an error message
     *
     * @param strMessageKey
     *            The message
     * @param locale
     *            The locale
     */
    protected void addError( String strMessageKey, Locale locale )
    {
        _listErrors.add( new MVCMessage( I18nService.getLocalizedString( strMessageKey, locale ) ) );
    }

    /**
     * Fill the model with commons objects used in templates
     *
     * @param model
     *            The model
     */
    protected void fillCommons( Map<String, Object> model )
    {
        _listErrors = new ArrayList<ErrorMessage>( );
        model.put( MARK_ERRORS, _listErrors );
    }

    /**
     * Get a model Object filled with default values
     *
     * @param ticket
     *            the ticket used to fill the model
     * @return The model
     */
    protected Map<String, Object> getModel( Ticket ticket )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        fillCommons( model );

        if ( ticket != null )
        {
            model.put( MARK_TICKET, ticket );
        }

        return model;
    }

    /**
     * Gives the ticket from resource
     *
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @return the ticket if the resource corresponds to a ticket, {@code null} otherwise
     */
    protected Ticket getTicket( int nIdResource, String strResourceType )
    {
        Ticket ticket = null;

        if ( ( strResourceType != null ) && Ticket.TICKET_RESOURCE_TYPE.equals( strResourceType ) )
        {
            ticket = TicketHome.findByPrimaryKey( nIdResource );
        }

        return ticket;
    }

    /**
     * Load the data of all the unit objects allowed for assignment and returns them in form of a collection
     *
     * @param user
     *            connected admin user
     * @param level
     *            the level
     * @return the list which contains the data of all the unit objects
     */
    protected static ReferenceList getUnitsList( AdminUser user, int level )
    {

        List<AssigneeUnit> listUnitByLevel = SupportEntityHome.getSupportEntityListByLevel( level ).stream( ).map( SupportEntity::getUnit )
                .collect( Collectors.toList( ) );

        ReferenceList lstRef = new ReferenceList( listUnitByLevel.size( ) );
        ReferenceItem emptyReferenceItem = new ReferenceItem( );
        emptyReferenceItem.setCode( StringUtils.EMPTY );
        emptyReferenceItem.setName( I18nService.getLocalizedString( MESSAGE_DEFAULT_LABEL_ENTITY_TASK_FORM, Locale.FRANCE ) );
        emptyReferenceItem.setChecked( true );
        lstRef.add( emptyReferenceItem );

        for ( AssigneeUnit unit : listUnitByLevel )
        {

            if ( RBACService.isAuthorized( unit, AssigneeUnit.PERMISSION_ASSIGN, user ) )
            {
                lstRef.addItem( unit.getUnitId( ), unit.getName( ) );
            }
        }

        return lstRef;
    }

    /**
     * Load the data of all the unit objects allowed for assignment and returns them in form of a collection
     *
     * @param user
     *            connected admin user
     * @param levelList
     *            the level list
     * @return the list which contains the data of all the unit objects
     */
    protected static ReferenceList getUnitsList( AdminUser user, List<Integer> levelList )
    {

        List<AssigneeUnit> listUnitByLevel = new ArrayList<>( );

        for ( Integer level : levelList )
        {
            listUnitByLevel
            .addAll( SupportEntityHome.getSupportEntityListByLevel( level ).stream( ).map( SupportEntity::getUnit ).collect( Collectors.toList( ) ) );
        }
        ReferenceList lstRef = new ReferenceList( listUnitByLevel.size( ) );
        ReferenceItem emptyReferenceItem = new ReferenceItem( );
        emptyReferenceItem.setCode( StringUtils.EMPTY );
        emptyReferenceItem.setName( I18nService.getLocalizedString( MESSAGE_DEFAULT_LABEL_ENTITY_TASK_FORM, Locale.FRANCE ) );
        emptyReferenceItem.setChecked( true );
        lstRef.add( emptyReferenceItem );

        for ( AssigneeUnit unit : listUnitByLevel )
        {

            if ( RBACService.isAuthorized( unit, AssigneeUnit.PERMISSION_ASSIGN, user ) )
            {
                lstRef.addItem( unit.getUnitId( ), unit.getName( ) );
            }
        }

        return lstRef;
    }

    /*    *//**
     * Load the data of all the vsp rules returns them in form of a collection
     *
     * @param user
     *            connected admin user
     * @return the list which contains the data of all the vsp rules active
     *//*
    protected static ReferenceList getVspRulesActiveList( Request request )
    {

        ReferenceList listVspRules = VspRuleHome.getReferenceList( request.g )

        ReferenceList lstRef = new ReferenceList( );
        ReferenceItem emptyReferenceItem = new ReferenceItem( );
        emptyReferenceItem.setCode( StringUtils.EMPTY );
        emptyReferenceItem.setName( I18nService.getLocalizedString( MESSAGE_DEFAULT_LABEL_VSP_RULE_TASK_FORM, Locale.FRANCE ) );
        emptyReferenceItem.setChecked( true );
        lstRef.add( emptyReferenceItem );

        for ( VspRule rule : listVspRules )
        {
            lstRef.addItem( rule.getId( ), unit.task_vsp_ticket.default.label.rule= - Choisir une r\u00e8gle - );
        }

        return lstRef;
    }*/
}
