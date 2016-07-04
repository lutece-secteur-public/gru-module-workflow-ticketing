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
package fr.paris.lutece.plugins.workflow.modules.ticketing.web.task;

import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.assignment.UserAutomaticAssignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.information.TaskInformation;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.assignment.IAutomaticAssignmentService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.information.ITaskInformationService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 * This class is a component for the task {@link fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskEditTicket}
 *
 */
public class AutomaticAssignmentTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_AUTOMATIC_ASSIGNMENT_CONFIG = "admin/plugins/workflow/modules/ticketing/task_automatic_assignment_config.html";
    private static final String TEMPLATE_TASK_AUTOMATIC_ASSIGNMENT_USER_CONFIG = "admin/plugins/workflow/modules/ticketing/task_automatic_assignment_user_config.html";
    private static final String URL_DISPLAY_CONFIG_FORM = "ModifyTask.jsp";
    private static final String URL_DO_REMOVE_CONFIG = "jsp/admin/plugins/workflow/DoModifyTask.jsp";

    // Marks
    private static final String MARK_USER_ASSIGNMENT_LIST = "list_user_assignment";
    private static final String MARK_DOMAIN_USER_LIST = "list_domain_user";
    private static final String MARK_AVAILABLE_SLOTS = "available_slots";
    private static final String MARK_AGENT_SLOTS = "agent_slot";
    private static final String MARK_SELECTED_AGENT = "selected_agent";
    private static final String MARK_ADD_NEW_AGENT_CONFIG = "new_agent_config";
    private static final String MARK_HAS_UNASSIGNED_AGENT = "has_unassigned_agent";

    // Parameters
    private static final String PARAMETER_USER_ACCESS_CODE = "user_access_code";
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_AGENT_SLOTS = "agent_slots";
    private static final String PARAMETER_TASK_ID = "id_task";

    //action type
    private static final String ACTION_CONFIRM_REMOVE_ASSIGNMENT = "confirm_remove_assignment";
    private static final String ACTION_REMOVE_ASSIGNMENT = "remove_assignment";
    private static final String ACTION_SAVE_ASSIGNMENT = "save_user_assignment";
    private static final String ACTION_ADD_NEW_USERCONFIG = "add_user_config";
    private static final String ACTION_DISPLAY_USER_CONFIG = "display_user_config";
    private static final String ACTION_DISPLAY_GLOBAL_CONFIG = "display_global_config";
    private static final String PROPERTY_AUTOMATIC_ASSIGNMENT_DOMAIN_RBAC_CODE = "workflow-ticketing.workflow.automatic_assignment.domainRBACCode";
    private static final String MESSAGE_TASK_AUTOMATIC_ASSIGNMENT_SUCCESSFUL_SAVED = "module.workflow.ticketing.task_automatic_assignment.config.user.savedSuccessful";
    private static final String MESSAGE_TASK_AUTOMATIC_ASSIGNMENT_CONFIRMATION_REMOVE = "module.workflow.ticketing.task_automatic_assignment.config.labelConfirmRemove";
    private static final String MESSAGE_TASK_AUTOMATIC_ASSIGNMENT_SUCCESSFUL_REMOVED = "module.workflow.ticketing.task_automatic_assignment.config.successfulRemove";
    private static final String MARK_INFOS = "infos";

    // Other constants
    private static final String SEPARATOR = "<hr>";
    private List<ErrorMessage> _listInfos = new ArrayList<ErrorMessage>(  );
    private String _strRoleKey = AppPropertiesService.getProperty( PROPERTY_AUTOMATIC_ASSIGNMENT_DOMAIN_RBAC_CODE );

    // SERVICES
    @Inject
    private IAutomaticAssignmentService _automaticAssignmentService;

    // SERVICES
    @Inject
    private ITaskInformationService _taskInformationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_ACTION ) ) &&
                request.getParameter( PARAMETER_ACTION ).equals( ACTION_DISPLAY_USER_CONFIG ) )
        {
            return getDisplayUserConfigForm( request, task, locale );
        }
        else if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_ACTION ) ) &&
                request.getParameter( PARAMETER_ACTION ).equals( ACTION_ADD_NEW_USERCONFIG ) )
        {
            return getDisplayNewUserConfigForm( request, task, locale );
        }
        else
        {
            return getDisplayGlobalConfigForm( task, locale );
        }
    }

    /**
     * return automatic assignment user config form
     * @param request http request
     * @param task task
     * @param locale locale
     * @return html content of user config form
     */
    private String getDisplayUserConfigForm( HttpServletRequest request, ITask task, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        List<AdminUser> listAdminUser = new ArrayList<AdminUser>( AdminUserHome.findByRole( _strRoleKey ) );
        String strSelectedUserAccessCode = request.getParameter( PARAMETER_USER_ACCESS_CODE );
        AdminUser adminUserSelected = null;

        if ( StringUtils.isNotBlank( strSelectedUserAccessCode ) )
        {
            for ( AdminUser adminUser : listAdminUser )
            {
                if ( adminUser.getAccessCode(  ).equals( strSelectedUserAccessCode ) )
                {
                    adminUserSelected = adminUser;

                    break;
                }
            }
        }

        model.put( MARK_DOMAIN_USER_LIST, listAdminUser );
        model.put( MARK_SELECTED_AGENT, adminUserSelected );
        model.put( MARK_AVAILABLE_SLOTS,
            _automaticAssignmentService.getAvailableAutoAssignementList( task.getId(  ) ).getAssignedSuffix(  ) );
        model.put( MARK_INFOS, _listInfos );

        if ( adminUserSelected != null )
        {
            model.put( MARK_AGENT_SLOTS,
                _automaticAssignmentService.getUserAssignments( task.getId(  ), adminUserSelected ).getAssignedSuffix(  ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_AUTOMATIC_ASSIGNMENT_USER_CONFIG, locale,
                model );

        return template.getHtml(  );
    }

    /**
     * return automatic assignment user config form for unassigned users only
     * @param request http request
     * @param task task
     * @param locale locale
     * @return html content of user config form
     */
    private String getDisplayNewUserConfigForm( HttpServletRequest request, ITask task, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        List<AdminUser> listAdminUser = new ArrayList<AdminUser>( AdminUserHome.findByRole( _strRoleKey ) );
        String strSelectedUserAccessCode = request.getParameter( PARAMETER_USER_ACCESS_CODE );
        AdminUser adminUserSelected = null;

        if ( StringUtils.isNotBlank( strSelectedUserAccessCode ) )
        {
            for ( AdminUser adminUser : listAdminUser )
            {
                if ( adminUser.getAccessCode(  ).equals( strSelectedUserAccessCode ) )
                {
                    adminUserSelected = adminUser;

                    break;
                }
            }
        }

        List<AdminUser> listAdminUserUnassigned = new ArrayList<AdminUser>( listAdminUser );
        List<UserAutomaticAssignmentConfig> listAdminUserAssigmentConfig = _automaticAssignmentService.getAllAutoAssignementConf( task.getId(  ) );

        for ( AdminUser adminUser : listAdminUser )
        {
            for ( UserAutomaticAssignmentConfig userConf : listAdminUserAssigmentConfig )
            {
                if ( ( userConf.getAdminUser(  ) != null ) &&
                        StringUtils.isNotEmpty( userConf.getAdminUser(  ).getAccessCode(  ) ) &&
                        adminUser.getAccessCode(  ).equals( userConf.getAdminUser(  ).getAccessCode(  ) ) &&
                        ( userConf.getAssignedSuffix(  ) != null ) && ( userConf.getAssignedSuffix(  ).size(  ) > 0 ) )
                {
                    listAdminUserUnassigned.remove( adminUser );

                    break;
                }
            }
        }

        listAdminUser = listAdminUserUnassigned;

        model.put( MARK_DOMAIN_USER_LIST, listAdminUser );
        model.put( MARK_SELECTED_AGENT, adminUserSelected );
        model.put( MARK_ADD_NEW_AGENT_CONFIG, true );
        model.put( MARK_AVAILABLE_SLOTS,
            _automaticAssignmentService.getAvailableAutoAssignementList( task.getId(  ) ).getAssignedSuffix(  ) );
        model.put( MARK_INFOS, _listInfos );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_AUTOMATIC_ASSIGNMENT_USER_CONFIG, locale,
                model );

        return template.getHtml(  );
    }

    /**
     * return automatic assignment global config form
     * @param task task
     * @param locale locale
     * @return html content of global config form
     */
    private String getDisplayGlobalConfigForm( ITask task, Locale locale )
    {
        List<UserAutomaticAssignmentConfig> listAdminUserAssigmentConfig = _automaticAssignmentService.getAllAutoAssignementConf( task.getId(  ) );
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_USER_ASSIGNMENT_LIST, listAdminUserAssigmentConfig );

        List<AdminUser> listAdminUserDomain = new ArrayList<AdminUser>( AdminUserHome.findByRole( _strRoleKey ) );
        boolean hasUnassignedSlots = false;

        for ( UserAutomaticAssignmentConfig userAssignmentConf : listAdminUserAssigmentConfig )
        {
            if ( ( userAssignmentConf.getAdminUser(  ) == null ) ||
                    StringUtils.isEmpty( userAssignmentConf.getAdminUser(  ).getAccessCode(  ) ) )
            {
                hasUnassignedSlots = true;

                break;
            }
        }

        int nbAssignedAgent = listAdminUserAssigmentConfig.size(  );

        if ( hasUnassignedSlots )
        {
            nbAssignedAgent--;
        }

        if ( ( listAdminUserDomain != null ) && ( listAdminUserAssigmentConfig != null ) &&
                ( nbAssignedAgent < listAdminUserDomain.size(  ) ) )
        {
            model.put( MARK_HAS_UNASSIGNED_AGENT, true );
        }

        model.put( MARK_INFOS, _listInfos );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_AUTOMATIC_ASSIGNMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * unassign all assignment for given user
     * @param task task
     * @param strUserAccessCode user access code
     */
    private void deleteUserAssignement( ITask task, String strUserAccessCode )
    {
        _automaticAssignmentService.unassignByUser( task.getId(  ), strUserAccessCode );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strAction = request.getParameter( PARAMETER_ACTION );
        String strUserAccessCode = request.getParameter( PARAMETER_USER_ACCESS_CODE );
        HashMap<String, String> mapParameters = new HashMap<String, String>(  );
        _listInfos = new ArrayList<ErrorMessage>(  );

        if ( StringUtils.isNotBlank( strAction ) )
        {
            if ( strAction.equals( ACTION_CONFIRM_REMOVE_ASSIGNMENT ) )
            {
                return getConfirmRemoveUserAssignement( request, locale, task, strUserAccessCode );
            }
            else if ( strAction.equals( ACTION_REMOVE_ASSIGNMENT ) )
            {
                deleteUserAssignement( task, strUserAccessCode );
                addInfo( I18nService.getLocalizedString( MESSAGE_TASK_AUTOMATIC_ASSIGNMENT_SUCCESSFUL_REMOVED, locale ) );

                return getDisplayConfigFormUrl( task, null );
            }
            else if ( strAction.equals( ACTION_SAVE_ASSIGNMENT ) )
            {
                storeUserAssignments( request, task, strUserAccessCode );
                mapParameters.put( PARAMETER_ACTION, ACTION_DISPLAY_USER_CONFIG );
                mapParameters.put( PARAMETER_USER_ACCESS_CODE, strUserAccessCode );
                addInfo( I18nService.getLocalizedString( MESSAGE_TASK_AUTOMATIC_ASSIGNMENT_SUCCESSFUL_SAVED, locale ) );

                return getDisplayConfigFormUrl( task, mapParameters );
            }
            else if ( strAction.equals( ACTION_DISPLAY_USER_CONFIG ) )
            {
                mapParameters.put( PARAMETER_ACTION, ACTION_DISPLAY_USER_CONFIG );
                mapParameters.put( PARAMETER_USER_ACCESS_CODE, strUserAccessCode );

                return getDisplayConfigFormUrl( task, mapParameters );
            }
            else if ( strAction.equals( ACTION_DISPLAY_GLOBAL_CONFIG ) )
            {
                return getDisplayConfigFormUrl( task, null );
            }
            else if ( strAction.equals( ACTION_ADD_NEW_USERCONFIG ) )
            {
                mapParameters.put( PARAMETER_USER_ACCESS_CODE, strUserAccessCode );
                mapParameters.put( PARAMETER_ACTION, ACTION_ADD_NEW_USERCONFIG );

                return getDisplayConfigFormUrl( task, mapParameters );
            }
        }

        return null;
    }

    /**
     * build remove confirmation message
     * @param request request
     * @param locale locae
     * @param task task
     * @param strUserAccessCode userAccessCode
     * @return url
     */
    private String getConfirmRemoveUserAssignement( HttpServletRequest request, Locale locale, ITask task,
        String strUserAccessCode )
    {
        UrlItem url = new UrlItem( URL_DO_REMOVE_CONFIG );
        url.addParameter( PARAMETER_USER_ACCESS_CODE, strUserAccessCode );
        url.addParameter( PARAMETER_ACTION, ACTION_REMOVE_ASSIGNMENT );
        url.addParameter( PARAMETER_TASK_ID, task.getId(  ) );

        String strMessageUrl = AdminMessageService.getMessageUrl( request,
                MESSAGE_TASK_AUTOMATIC_ASSIGNMENT_CONFIRMATION_REMOVE, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return strMessageUrl;
    }

    /**
     * store user assignment configuration
     * @param request request
     * @param task task
     * @param strUserAccessCode user access code
     */
    private void storeUserAssignments( HttpServletRequest request, ITask task, String strUserAccessCode )
    {
        _automaticAssignmentService.unassignByUser( task.getId(  ), strUserAccessCode );

        List<String> listAssignedSuffix = RequestUtils.extractValueList( request, PARAMETER_AGENT_SLOTS );

        for ( String strSuffix : listAssignedSuffix )
        {
            _automaticAssignmentService.assign( task.getId(  ), strUserAccessCode, strSuffix );
        }
    }

    /**
     * build and return display Config Form Url
     * @param task task
     * @param mapParams map of parameters to add to redirect url
     * @return display Config Form Url
     */
    private static String getDisplayConfigFormUrl( ITask task, HashMap<String, String> mapParams )
    {
        UrlItem url = new UrlItem( URL_DISPLAY_CONFIG_FORM );
        url.addParameter( PARAMETER_TASK_ID, task.getId(  ) );

        if ( ( mapParams != null ) && ( mapParams.size(  ) > 0 ) )
        {
            for ( Map.Entry<String, String> entry : mapParams.entrySet(  ) )
            {
                url.addParameter( entry.getKey(  ), entry.getValue(  ) );
            }
        }

        return url.toString(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        String strTaskInformation = StringUtils.EMPTY;

        TaskInformation taskInformation = _taskInformationService.findByPrimaryKey( nIdHistory, task.getId(  ),
                WorkflowUtils.getPlugin(  ) );

        if ( taskInformation != null )
        {
            strTaskInformation = taskInformation.getValue(  ) + SEPARATOR;
        }

        return strTaskInformation;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Add an info message
     * @param strMessage The message
     */
    protected void addInfo( String strMessage )
    {
        _listInfos.add( new MVCMessage( strMessage ) );
    }
}
