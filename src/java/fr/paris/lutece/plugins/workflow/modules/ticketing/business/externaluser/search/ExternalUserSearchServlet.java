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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.ExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.IExternalUserDAO;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.attribute.IAttribute;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.user.attribute.AttributeService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Servlet for the search of external user
 */
public class ExternalUserSearchServlet extends HttpServlet
{
    // Other constants
    public static final String URL_SERVLET = "servlet/plugins/workflow/ticketing/externaluser/externalusersearch";

    /**
     * Generated serial Id
     */
    private static final long serialVersionUID = -1109810381598265699L;

    // Template
    private static final String TEMPLATE_SEARCH_RESULT = "admin/plugins/workflow/modules/ticketing/external_user/search_external_user_result.html";

    // Properties
    private static final String PROPERTY_ENCODING = "lutece.encoding";

    // Model
    private static final String MARK_ERRORS = "errors";
    private static final String MARK_INFOS = "infos";
    private static final String MARK_LIST_USERS = "result_user";
    private static final String MARK_INPUT_EMAIL = "input_email";
    private static final String MARK_ID_TASK = "id_task";
    private static final String MARK_ATTRIBUTE_LABEL = "attribute_label";

    // Request parameter
    private static final String PARAM_INPUT_EMAIL = "input_email";
    private static final String PARAM_ID_TASK = "id_task";
    private static final String PARAM_LASTNAME = "lastname";
    private static final String PARAM_ID_ATTRIBUTE = "id_attribute";
    private static final String PARAM_ATTRIBUTE_VALUE = "attribute_value";
    private static final String PARAM_NEXT_ACTION_ID = "next_action_id";

    // message
    private static final String LOG_UNAUTHENTICATED_USER = "Calling ExternalUserSearchServlet with unauthenticated user";
    private static final String KEY_ERROR_NO_RESULT = "module.workflow.ticketing.task_ticket_email_external_user.error.search.no_result";
    private static final String KEY_INFOS_LIMIT_RESULT = "module.workflow.ticketing.task_ticket_email_external_user.info.search.limit_result";

    // BEAN
    private final IExternalUserDAO        _externalUserDAO         = SpringContextService.getBean( IExternalUserDAO.BEAN_SERVICE );

    // SERVICE
    private static final AttributeService _attributeService = AttributeService.getInstance( );

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * 
     * @param request
     *            servlet request
     * @param httpResponse
     *            servlet response
     * @throws ServletException
     *             the servlet Exception
     * @throws IOException
     *             the io exception
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse httpResponse ) throws ServletException, IOException
    {
        AdminUser user = AdminAuthenticationService.getInstance( ).getRegisteredUser( request );

        if ( user == null )
        {
            AppLogService.error( LOG_UNAUTHENTICATED_USER );
            throw new ServletException( LOG_UNAUTHENTICATED_USER );
        }

        // request param
        String strInputEmail = request.getParameter( PARAM_INPUT_EMAIL );
        String strIdTask = request.getParameter( PARAM_ID_TASK );
        String strLastname = request.getParameter( PARAM_LASTNAME );
        String strIdAttribute = request.getParameter( PARAM_ID_ATTRIBUTE );
        String strAttributeValue = request.getParameter( PARAM_ATTRIBUTE_VALUE );
        String strNextActionId = request.getParameter( PARAM_NEXT_ACTION_ID );

        Locale locale = user.getLocale( );
        int searchLimit = _externalUserDAO.getSearchLimit( );
        Map<String, Object> model = new HashMap<String, Object>( );
        List<ErrorMessage> listErrors = new ArrayList<ErrorMessage>( );
        List<ErrorMessage> listInfos = new ArrayList<ErrorMessage>( );

        // TMP
        List<ExternalUser> listExternalUsers = _externalUserDAO.findExternalUser( strLastname, null, strIdAttribute, strAttributeValue, strNextActionId );

        String strLabelContactAttribute = StringUtils.EMPTY;
        if ( StringUtils.isNotBlank( strIdAttribute ) && StringUtils.isNumeric( strIdAttribute ) )
        {
            IAttribute attribute = _attributeService.getAttributeWithFields( Integer.parseInt( strIdAttribute ), locale );
            if ( attribute != null )
            {
                strLabelContactAttribute = attribute.getTitle( );
            }
        }

        // error no result
        if ( listExternalUsers.isEmpty( ) )
        {
            listErrors.add( new MVCMessage( I18nService.getLocalizedString( KEY_ERROR_NO_RESULT, locale ) ) );
        }

        if ( ( searchLimit > 0 ) && ( listExternalUsers.size( ) > searchLimit ) )
        {
            listInfos.add( new MVCMessage( I18nService.getLocalizedString( KEY_INFOS_LIMIT_RESULT, new Object [ ] {
                    searchLimit, listExternalUsers.size( )
            }, locale ) ) );
            listExternalUsers = listExternalUsers.subList( 0, searchLimit );
        }

        model.put( MARK_ERRORS, listErrors );
        model.put( MARK_INFOS, listInfos );
        model.put( MARK_LIST_USERS, listExternalUsers );
        model.put( MARK_INPUT_EMAIL, strInputEmail );
        model.put( MARK_ID_TASK, strIdTask );
        model.put( MARK_ATTRIBUTE_LABEL, strLabelContactAttribute );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEARCH_RESULT, locale, model );
        ServletOutputStream outStream = httpResponse.getOutputStream( );
        outStream.write( template.getHtml( ).getBytes( AppPropertiesService.getProperty( PROPERTY_ENCODING ) ) );
        outStream.flush( );
        outStream.close( );
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             the servlet Exception
     * @throws IOException
     *             the io exception
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             the servlet Exception
     * @throws IOException
     *             the io exception
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return message
     */
    @Override
    public String getServletInfo( )
    {
        return "Servlet serving external user search result";
    }
}
