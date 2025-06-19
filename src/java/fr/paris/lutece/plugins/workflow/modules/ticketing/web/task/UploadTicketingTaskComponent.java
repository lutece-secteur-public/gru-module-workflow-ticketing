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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.ticketing.service.upload.TicketWorkflowAsynchronousUploadHandler;
import fr.paris.lutece.plugins.workflow.modules.upload.business.task.TaskUploadConfig;
import fr.paris.lutece.plugins.workflow.modules.upload.web.UploadTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * UploadTicketingTaskComponent
 *
 */
public class UploadTicketingTaskComponent extends UploadTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_UPLOAD_FORM                = "admin/plugins/workflow/modules/upload/task_upload_form.html";

    // MARKS
    private static final String MARK_CONFIG                               = "config";
    private static final String MARK_HANDLER                              = "handler";
    private static final String MARK_FILE_NAME                            = "upload_value";
    private static final String MARK_LIST_UPLOADED_FILE                   = "listUploadedFiles";

    // PARAMETERS
    private static final String PARAMETER_UPLOAD_VALUE                    = "upload_value";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD                   = "module.workflow.upload.task_upload_config.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_UPLOAD  = "module.workflow.upload.task_upload_config.message.no_configuration_for_task_upload";

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        String strUploadValue = PARAMETER_UPLOAD_VALUE + "_" + task.getId( );
        TaskUploadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_CONFIGURATION_FOR_TASK_UPLOAD, AdminMessage.TYPE_STOP );
        }

        List<FileItem> listFiles = TicketWorkflowAsynchronousUploadHandler.getHandler( ).getListUploadedFiles( strUploadValue, request.getSession( ) );

        if ( config.isMandatory( ) && listFiles.isEmpty( ) )
        {
            Object[] tabRequiredFields = { config.getTitle( ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        TaskUploadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        String strUpload = PARAMETER_UPLOAD_VALUE + "_" + task.getId( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_HANDLER, TicketWorkflowAsynchronousUploadHandler.getHandler( ) );
        model.put( MARK_FILE_NAME, strUpload );

        TicketWorkflowAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
        model.put( MARK_LIST_UPLOADED_FILE, new ArrayList<FileItem>( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPLOAD_FORM, locale, model );

        return template.getHtml( );
    }
}

