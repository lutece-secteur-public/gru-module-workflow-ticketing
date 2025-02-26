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
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidatorResult;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskModifyTicketCategoryConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.UtilConstants;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
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
    private static final String MARK_ID_TASK = "id_task";

    // Message reply
    private static final String MESSAGE_MODIFY_TICKET_ATTRIBUTE_ERROR = "module.workflow.ticketing.task_modify_ticket_attribute.error";

    @Inject
    private TicketFormService _ticketFormService;

    private static final String PROPERTY_ACCOUNT_NUMBER_REGEXP = "module.workflow.ticketingfacilfamilles.workflow.automatic_assignment.accountNumberRegexp";
    private static final String PROPERTY_FF_CODE = "module.workflow.ticketingfacilfamilles.workflow.automatic_assignment.accountNumberFieldCode";
    private static final String MESSAGE_ERROR_FACIL_EMPTY_VALIDATION = "ticketing.validation.ticket.TicketFacilNumber.size";
    private static final String MESSAGE_ERROR_FACIL_REGEX_VALIDATION = "ticketing.validation.ticket.TicketFacilNumber.regex";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskModifyTicketCategoryConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        entryFilter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> lReferenceEntry = EntryHome.getEntryList( entryFilter );

        Map<String, Object> model = new HashMap<>( );
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

            refItem.setName( refEntry.getTitle( ) + " (" + refEntry.getEntryType( ).getTitle( ) + ")" );
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
        TaskModifyTicketCategoryConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
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

        getTaskConfigService( ).update( config );

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

        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getDepths( ) );
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
        Ticket ticket = getTicket( nIdResource, strResourceType );
        StringBuilder strError = new StringBuilder( StringUtils.EMPTY );
        List<String> listErrors = new ArrayList<>( );

        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request, locale ).validateTicketCategory( );

        // Check if a category have been selected
        if ( !categoryValidatorResult.isTicketCategoryValid( ) )
        {
            categoryValidatorResult.getListValidationErrors( ).stream( ).forEach( listErrors::add );
        }

        // Validate the selection of items
        if ( categoryValidatorResult.isTicketCategoryValid( ) )
        {
            List<GenericAttributeError> listFormErrors = new ArrayList<>( );
            TaskModifyTicketCategoryConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
            List<Entry> listEntry = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), config.getSelectedEntries( ) );

            boolean hasFFError = false;
            for ( Entry entry : listEntry )
            {
                listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), request.getLocale( ), ticket ) );

                // O2T 79251: contrôle facil'famille
                if ( !hasFFError && isDomainFacilFamille( categoryValidatorResult.getTicketCategory( ) ) )
                {
                    for ( GenericAttributeError error : listFormErrors )
                    {
                        if ( error.getErrorMessage( ).contains( "Facil'Famille" ) || error.getErrorMessage( ).contains( UtilConstants.CATEGORY_LABEL_PARIS_FAMILLE ) )
                        {
                            hasFFError = true;
                            break;
                        }
                    }
                    if ( !hasFFError && entry.getCode( ).equals( AppPropertiesService.getProperty( PROPERTY_FF_CODE ) ) )
                    {
                        String strFacilFamilleNumber = request.getParameter( "attribute" + entry.getIdEntry( ) );
                        if ( strFacilFamilleNumber != null )
                        {
                            if ( strFacilFamilleNumber.trim( ).isEmpty( ) )
                            {
                                GenericAttributeError formError = new GenericAttributeError( );
                                formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_FACIL_EMPTY_VALIDATION, request.getLocale( ) ) );
                                listFormErrors.add( formError );
                                hasFFError = true;
                            }
                            else
                                if ( !strFacilFamilleNumber.matches( AppPropertiesService.getProperty( PROPERTY_ACCOUNT_NUMBER_REGEXP ) ) )
                                {
                                    GenericAttributeError formError = new GenericAttributeError( );
                                    formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_FACIL_REGEX_VALIDATION, request.getLocale( ) ) );
                                    listFormErrors.add( formError );
                                    hasFFError = true;
                                }
                        }
                    }
                }
            }

            // O2T 79251: contrôle facil'famille
            if ( listEntry.isEmpty( ) && isDomainFacilFamille( categoryValidatorResult.getTicketCategory( ) ) )
            {
                GenericAttributeError facilFamilleError = getFacilFamilleError( request );
                if ( facilFamilleError != null )
                {
                    listFormErrors.add( facilFamilleError );
                }
            }

            if ( !listFormErrors.isEmpty( ) )
            {
                for ( GenericAttributeError formError : listFormErrors )
                {
                    strError.append( formError.getErrorMessage( ) ).append( "<br/>" );
                }

                listErrors.add( strError.toString( ) );
            }
        }

        if ( !listErrors.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MODIFY_TICKET_ATTRIBUTE_ERROR, listErrors.toArray( ), AdminMessage.TYPE_STOP );
        }

        return null;
    }

    private GenericAttributeError getFacilFamilleError( HttpServletRequest request )
    {
        String strFacilFamilleNumber = request.getParameter( "attribute202" );

        if ( strFacilFamilleNumber != null )
        {
            if ( strFacilFamilleNumber.trim( ).isEmpty( ) )
            {
                GenericAttributeError formError = new GenericAttributeError( );
                formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_FACIL_EMPTY_VALIDATION, request.getLocale( ) ) );
                return formError;
            }
            else
                if ( !strFacilFamilleNumber.matches( AppPropertiesService.getProperty( PROPERTY_ACCOUNT_NUMBER_REGEXP ) ) )
                {
                    GenericAttributeError formError = new GenericAttributeError( );
                    formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_FACIL_REGEX_VALIDATION, request.getLocale( ) ) );
                    return formError;
                }
        }

        return null;
    }

    private boolean isDomainFacilFamille( TicketCategory category )
    {
        if ( category != null )
        {
            if ( category.getDepth( ).getDepthNumber( ) > 1 )
            {
                return isDomainFacilFamille( category.getParent( ) );
            }
            else
                if ( category.getDepth( ).getDepthNumber( ) == 1 )
                {
                    return category.getLabel( ).equals( UtilConstants.CATEGORY_LABEL_PARIS_FAMILLE );
                }
                else
                {
                    // ne devrait pas se produire (0 ou négatif)
                    return false;
                }
        }
        return false;
    }

}
