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

import java.text.MessageFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class represents a task that sends ticket store the admin user who created the sollicitation BO
 *
 */
public class TaskAutomaticIdAdminBOInit extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_ADMIN_BO_TICKET = "module.workflow.ticketing.task_automatic_id_bo_admin.labelAdmin";
    private static final String MESSAGE_AUTOMATIC_ADMIN_BO_INFORMATION = "module.workflow.ticketing.task_automatic_id_bo_admin.information";

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        // We get the ticket to modify
        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket.getIdAdminBOInit( ) > 0 )
        {
            String strCompletedAdminName = getCompletedNameBOAdmin( ticket );
            strTaskInformation += formatInfoMessageInit( MESSAGE_AUTOMATIC_ADMIN_BO_INFORMATION, strCompletedAdminName, locale );
        }

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_ADMIN_BO_TICKET, locale );
    }

    /**
     * Return the message formated for the name an of informations of the ticket
     *
     * @param strKey
     *            : the key of the message
     * @param strValue
     *            : the value
     * @param locale
     * @return the message formated
     */
    private String formatInfoMessageInit( String strKey, String strValue, Locale locale )
    {
        return MessageFormat.format( I18nService.getLocalizedString( strKey, locale ), strValue );
    }

    /**
     * Return the completesd name for the admin user BO who created the ticket
     *
     * @param ticket
     *            : the ticket created
     *
     * @return the message formated
     */
    private String getCompletedNameBOAdmin( Ticket ticket )
    {
        AdminUser adminUser = AdminUserHome.findByPrimaryKey( ticket.getIdAdminBOInit( ) );
        return adminUser.getFirstName( ) + " " + adminUser.getLastName( );
    }
}
