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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskModifyTicketCategoryConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskModifyTicketCategory}
 *
 */
public class ModifyTicketCategoryTaskComponent extends TicketingTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_MODIFY_TICKET_CATEGORY_FORM = "admin/plugins/workflow/modules/ticketing/task_modify_ticket_category_form.html";
    private static final String TEMPLATE_TASK_MODIFY_TICKET_CATEGORY_CONFIG = "admin/plugins/workflow/modules/ticketing/task_modify_ticket_category_config.html";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_CONFIG_ALL_ENTRY = "form_entries";
    private static final String MARK_CONFIG_SELECTED_ENTRY = "selected_form_entry";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_TICKET_PRECISIONS_LIST = "ticket_precisions_list";
    private static final String MARK_ID_TASK = "id_task";

    // Message reply
    private static final String MESSAGE_MODIFY_TICKET_ATTRIBUTE_ERROR = "module.workflow.ticketing.task_modify_ticket_attribute.error";
    private static final String MESSAGE_MODIFY_TICKET_ERROR_DOMAIN_NOT_SELECTED = "module.workflow.ticketing.task_modify_ticket_category.error.notSelected.domain";
    private static final String MESSAGE_MODIFY_TICKET_ERROR_CATEGORY_NOT_SELECTED = "module.workflow.ticketing.task_modify_ticket_category.error.notSelected.category";
    private static final String MESSAGE_MODIFY_TICKET_ERROR_PRECISION_NOT_SELECTED = "module.workflow.ticketing.task_modify_ticket_category.error.notSelected.precision";
    
    @Inject
    private TicketFormService _ticketFormService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskModifyTicketCategoryConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        entryFilter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> lReferenceEntry = EntryHome.getEntryList( entryFilter );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_CONFIG_ALL_ENTRY, mergeConfigAndReference( config, lReferenceEntry ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MODIFY_TICKET_CATEGORY_CONFIG, locale, model );

        return template.getHtml( );
    }

    private ReferenceList mergeConfigAndReference( TaskModifyTicketCategoryConfig config, List<Entry> lReferenceEntry )
    {
        ReferenceList refList = new ReferenceList( );

        for ( Entry refEntry : lReferenceEntry )
        {
            ReferenceItem refItem = new ReferenceItem( );
            refItem.setCode( Integer.toString( refEntry.getIdEntry( ) ) );

            StringBuilder strInput = new StringBuilder( refEntry.getTitle( ) );
            strInput.append( " (" ).append( refEntry.getEntryType( ).getTitle( ) ).append( ")" );
            refItem.setName( strInput.toString( ) );
            refItem.setChecked( config.getSelectedEntries( ).contains( refEntry.getIdEntry( ) ) );

            refList.add( refItem );
        }

        return refList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskModifyTicketCategoryConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        String [ ] tSelectedEntries = new String [ ] { };
        config.clearSelectedEntries( );

        if ( request.getParameterValues( MARK_CONFIG_SELECTED_ENTRY ) != null )
        {
            tSelectedEntries = request.getParameterValues( MARK_CONFIG_SELECTED_ENTRY );
        }

        for ( String strSelectedEntry : tSelectedEntries )
        {
            config.addSelectedEntry( Integer.parseInt( strSelectedEntry ) );
        }

        this.getTaskConfigService( ).update( config );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );
        _ticketFormService.saveTicketInSession( request.getSession( ), ticket );

        Map<String, Object> model = getModel( ticket );

        model.put( MARK_TICKET_TYPES_LIST, new ReferenceList( ) );
        model.put( MARK_TICKET_DOMAINS_LIST, new ReferenceList( ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, new ReferenceList( ) );
        model.put( MARK_TICKET_PRECISIONS_LIST, new ReferenceList( ) );
        model.put( MARK_ID_TASK, task.getId( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MODIFY_TICKET_CATEGORY_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        Ticket ticket = getTicket( nIdResource, strResourceType );
        String strError = StringUtils.EMPTY;
        List<String> listErrors = new ArrayList<>( );

        String strNewCategoryId = request.getParameter( TicketingConstants.PARAMETER_TICKET_CATEGORY_ID );
        int nNewCategoryId = Integer.parseInt( strNewCategoryId );

        if ( nNewCategoryId > 0 )
        {
            TaskModifyTicketCategoryConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
            List<Entry> listEntry = TicketFormService.getFilterInputs( nNewCategoryId, config.getSelectedEntries( ) );

            for ( Entry entry : listEntry )
            {
                listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), request.getLocale( ), ticket ) );
            }
        }

        // Validate the selection of items
        validateItemSelection( request, listErrors, locale);
        
        if ( !listFormErrors.isEmpty( ) )
        {
            for ( GenericAttributeError formError : listFormErrors )
            {
                strError += ( formError.getErrorMessage( ) + "<br/>" );
            }

            listErrors.add( strError );
        }
        
        if ( !listErrors.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MODIFY_TICKET_ATTRIBUTE_ERROR, listErrors.toArray( ), AdminMessage.TYPE_STOP );
        }

        return null;
    }
    
    /**
     * Validate the selection made by the user
     * 
     * @param request
     * @param listErrors
     * @param locale
     */
    protected void validateItemSelection( HttpServletRequest request, List<String> listErrors, Locale locale )
    {
        // Retrive the selected id from the request
        String strNewDomainId = request.getParameter( TicketingConstants.PARAMETER_TICKET_DOMAIN_ID );
        String strNewCategoryId = request.getParameter( TicketingConstants.PARAMETER_TICKET_CATEGORY_ID );
        
        // Control if a Domain has been selected or not
        if ( StringUtils.isNotBlank( strNewDomainId ) && strNewDomainId.equals( TicketingConstants.NO_ID_STRING ) )  
        {
            listErrors.add( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_ERROR_DOMAIN_NOT_SELECTED, locale ) );
        }
        
        // Control if a Category has been selected
        if ( StringUtils.isNotBlank( strNewCategoryId ) && strNewCategoryId.equals( TicketingConstants.NO_ID_STRING ) )
        {
            listErrors.add( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_ERROR_CATEGORY_NOT_SELECTED, locale ) );
        }
        
        // Control if a precision has been selected or not
        if ( StringUtils.isNumeric( strNewCategoryId ) )
        {
            TicketCategory ticketCategoryTemp = TicketCategoryHome.findByPrimaryKey( Integer.parseInt( strNewCategoryId ) );
            if ( ticketCategoryTemp != null && StringUtils.isNotBlank( ticketCategoryTemp.getPrecision( ) )
                    && StringUtils.isNotBlank( request.getParameter( TicketingConstants.PARAMETER_TICKET_PRECISION_ID ) )
                    && request.getParameter( TicketingConstants.PARAMETER_TICKET_PRECISION_ID ).equals( TicketingConstants.NO_ID_STRING ) )
            {
                listErrors.add( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_ERROR_PRECISION_NOT_SELECTED, locale ) );
            }
        }       
    }
}
