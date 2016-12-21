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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.config.TaskModifyTicketCategoryConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
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
    
    
    /**
	 * {@inheritDoc}
	 */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
    	TaskModifyTicketCategoryConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
    	
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        entryFilter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> lReferenceEntry = EntryHome.getEntryList( entryFilter );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_CONFIG, config );
        model.put( MARK_CONFIG_ALL_ENTRY, mergeConfigAndReference( config, lReferenceEntry ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MODIFY_TICKET_CATEGORY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    private ReferenceList mergeConfigAndReference( TaskModifyTicketCategoryConfig config, List<Entry> lReferenceEntry )
    {
    	ReferenceList refList = new ReferenceList(  );
    	
    	for ( Entry refEntry : lReferenceEntry )
        {
	        ReferenceItem refItem = new ReferenceItem(  );
	        refItem.setCode( Integer.toString( refEntry.getIdEntry(  ) ) );
	        StringBuilder strInput = new StringBuilder( refEntry.getTitle(  ) );
	        strInput.append( " (" ).append( refEntry.getEntryType(  ).getTitle(  ) ).append( ")" );
	        refItem.setName( strInput.toString(  ) );
	        refItem.setChecked( config.getSelectedEntries(  ).contains( refEntry.getIdEntry(  ) ) );
	        
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
    	TaskModifyTicketCategoryConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
    	String[] tSelectedEntries = new String[  ]{  };
    	config.clearSelectedEntries(  );
    	
    	if( request.getParameterValues( MARK_CONFIG_SELECTED_ENTRY ) != null )
    	{
    		tSelectedEntries = request.getParameterValues( MARK_CONFIG_SELECTED_ENTRY );
    	}
    	
    	for ( String strSelectedEntry : tSelectedEntries )
        {
    		config.addSelectedEntry( Integer.parseInt( strSelectedEntry ) );
        }
    	
    	this.getTaskConfigService(  ).update( config );

    	return null;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Ticket ticket = getTicket( nIdResource, strResourceType );
        Map<String, Object> model = getModel( ticket );
        
        TaskModifyTicketCategoryConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
    	

        model.put( MARK_CONFIG, config );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST,
            TicketDomainHome.getReferenceListByType( ( ticket != null ) ? ticket.getIdTicketType(  ) : 1 ) );
        model.put( MARK_TICKET_CATEGORIES_LIST,
            TicketCategoryHome.getReferenceListByDomain( ( ticket != null ) ? ticket.getIdTicketDomain(  ) : 1 ) );
        model.put( MARK_TICKET_PRECISIONS_LIST, new ReferenceList(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_MODIFY_TICKET_CATEGORY_FORM, locale, model );

        return template.getHtml(  );
    }
}
