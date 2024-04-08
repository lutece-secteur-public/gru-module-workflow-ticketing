/*
 * Copyright (c) 2002-2024, City of Paris
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
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class represents a task to report (signalement) the ticket
 *
 */
public class TaskVspTicket extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_VSP_TICKET = "module.workflow.ticketing.task_signalement_ticket.labelVspTicket";
    private static final String MESSAGE_TICKET_VSP_RULE_INFORMATION = "module.workflow.ticketing.task_vsp_ticket.information";

    @Override
    public String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;

        String [ ] vspRulesChoice = request.getParameterMap( ).get( TicketingConstants.PARAMETER_VSP_RULES_CHOICE );

        // We get the ticket for vsp
        Ticket ticket = getTicket( nIdResourceHistory );

        String currentVSP = ticket.getVspRule( );
        String newRules = "";
        String oldRules = "";

        // Compare the vsp rules and write historic only when vspRules change
        if ( !cleanArray( vspRulesChoice ).equals( currentVSP ) )
        {

            newRules = ticket.getlistVspRuleslabel( cleanArray( vspRulesChoice ) );

            if ( ( null != currentVSP ) )
            {
                oldRules = ticket.getlistVspRuleslabel( currentVSP );
            }
            strTaskInformation += formatInfoMessage( MESSAGE_TICKET_VSP_RULE_INFORMATION, oldRules, newRules, locale );
        }

        TicketHome.update( ticket );

        return strTaskInformation;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_VSP_TICKET, locale );
    }

    /**
     * Return the message formated for the vsp rules of informations of the ticket
     *
     * @param strKey
     *            : the key of the message
     * @param strOldValue
     *            : the value which has been replaced
     * @param strNewValue
     *            : the new value
     * @param locale
     * @return the message formated
     */
    private String formatInfoMessage( String strKey, String strOldValue, String strNewValue, Locale locale )
    {
        return MessageFormat.format( I18nService.getLocalizedString( strKey, locale ), strOldValue, strNewValue );
    }

    /**
     * Return the String for the vsp rules cleaned
     *
     * @param ruleList
     *            : array of ids rules
     * @return the string formated
     */
    private String cleanArray( String [ ] ruleList )
    {
        return Arrays.toString( ruleList ).replace( "[", "" ).replace( "]", "" ).replace( ", ", ";" ).trim( );
    }

}
