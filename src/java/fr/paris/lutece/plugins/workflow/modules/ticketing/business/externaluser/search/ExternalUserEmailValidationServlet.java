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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.search;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.IExternalUserDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.task.TaskTicketEmailExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.utils.WorkflowTicketingUtils;
import fr.paris.lutece.plugins.workflow.modules.ticketing.web.task.TicketEmailExternalUserTaskComponent;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Servlet for the validation email for the external user
 */
public class ExternalUserEmailValidationServlet extends HttpServlet
{
    // Other constants
    public static final String URL_SERVLET = "servlet/plugins/workflow/ticketing/externaluser/externaluseremailvalidation";

    /**
     * Generated serial Id
     */
    private static final long serialVersionUID = -1109810381598265699L;

    // Properties
    private static final String PROPERTY_ENCODING = "lutece.encoding";

    // Json parameters
    private static final String ERROR_INVALID_EMAIL = "error_invalid_email";
    private static final String ERROR_INVALID_EMAIL_CC = "error_invalid_email_cc";

    // message
    private static final String LOG_UNAUTHENTICATED_USER = "Calling ExternalUserEmailValidationServlet with unauthenticated user";

    // BEAN
    private final IExternalUserDAO _externalUserDAO = SpringContextService.getBean( IExternalUserDAO.BEAN_SERVICE );

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

        Locale locale = user.getLocale( );
        Map<String, String> mapErrors = new HashMap<>( );
        mapErrors.put( ERROR_INVALID_EMAIL, StringUtils.EMPTY );
        mapErrors.put( ERROR_INVALID_EMAIL_CC, StringUtils.EMPTY );

        // request param
        String strEmailRecipients = request.getParameter( TaskTicketEmailExternalUser.PARAMETER_EMAIL_RECIPIENTS );
        String strEmailRecipientsCc = request.getParameter( TaskTicketEmailExternalUser.PARAMETER_EMAIL_RECIPIENTS_CC );
        String strNextActionId = request.getParameter( TaskTicketEmailExternalUser.PARAM_NEXT_ACTION_ID );

        if ( StringUtils.isEmpty( strEmailRecipients ) )
        {
            mapErrors.put( ERROR_INVALID_EMAIL, I18nService.getLocalizedString( TicketEmailExternalUserTaskComponent.MESSAGE_EMPTY_EMAIL, locale ) );
        }
        else
        {
            List<String> listErrorRecipients = WorkflowTicketingUtils.validEmailList( strEmailRecipients, _externalUserDAO, strNextActionId );
            if ( !listErrorRecipients.isEmpty( ) )
            {
                String strErrorRecipients;
                if ( listErrorRecipients.size( ) == 1 )
                {
                    strErrorRecipients = I18nService.getLocalizedString( listErrorRecipients.get( 0 ), locale );
                }
                else
                {
                    strErrorRecipients = I18nService.getLocalizedString( listErrorRecipients.get( 0 ),
                            listErrorRecipients.subList( 1, listErrorRecipients.size( ) ).toArray( ), locale );
                }
                mapErrors.put( ERROR_INVALID_EMAIL, strErrorRecipients );
            }
        }

        if ( StringUtils.isNotEmpty( strEmailRecipientsCc ) )
        {
            List<String> listErrorRecipientsCc = WorkflowTicketingUtils.validEmailList( strEmailRecipientsCc, null, null );
            if ( !listErrorRecipientsCc.isEmpty( ) )
            {
                String strErrorRecipientsCc;
                if ( listErrorRecipientsCc.size( ) == 1 )
                {
                    strErrorRecipientsCc = I18nService.getLocalizedString( listErrorRecipientsCc.get( 0 ), locale );
                }
                else
                {
                    strErrorRecipientsCc = I18nService.getLocalizedString( listErrorRecipientsCc.get( 0 ),
                            listErrorRecipientsCc.subList( 1, listErrorRecipientsCc.size( ) ).toArray( ), locale );
                }
                mapErrors.put( ERROR_INVALID_EMAIL_CC, strErrorRecipientsCc );
            }
        }

        String jsonText = new ObjectMapper( ).writeValueAsString( mapErrors );

        ServletOutputStream outStream = httpResponse.getOutputStream( );
        outStream.write( jsonText.getBytes( AppPropertiesService.getProperty( PROPERTY_ENCODING ) ) );
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
        return "Servlet validating emails";
    }

}
