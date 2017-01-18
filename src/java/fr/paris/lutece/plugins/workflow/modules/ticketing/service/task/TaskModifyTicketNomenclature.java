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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a task to modify the ticket nomenclature
 *
 */
public class TaskModifyTicketNomenclature extends AbstractTicketingTask
{
    // Message
    private static final String MESSAGE_TASK_TITLE = "module.workflow.ticketing.task_modify_ticket_nomenclature.labelModifyNomenclatureTicket";
    private static final String MESSAGE_NO_VALUE = "module.workflow.ticketing.task_modify_ticket_nomenclature.noValue";
    private static final String MESSAGE_MODIFY_TICKET_CATEGORY_INFORMATION = "module.workflow.ticketing.task_modify_ticket_nomenclature.information";

    // Parameter
    private static final String PARAMETER_TICKET_NOMENCLATURE = "nomenclature";

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strOldNomenclature = StringUtils.EMPTY;
        String strNewNomenclature = StringUtils.EMPTY;
        String strTaskInformation = StringUtils.EMPTY;

        Ticket ticket = getTicket( nIdResourceHistory );

        if ( ticket != null )
        {
            strOldNomenclature = ticket.getNomenclature( );

            if ( strOldNomenclature == null )
            {
                strOldNomenclature = StringUtils.EMPTY;
            }

            strNewNomenclature = request.getParameter( PARAMETER_TICKET_NOMENCLATURE );

            if ( !strNewNomenclature.equals( strOldNomenclature ) )
            {
                ticket.setNomenclature( strNewNomenclature );

                TicketHome.update( ticket );

                strTaskInformation = MessageFormat.format( I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_CATEGORY_INFORMATION, locale ),
                        StringUtils.isNotEmpty( strOldNomenclature ) ? strOldNomenclature : I18nService.getLocalizedString( MESSAGE_NO_VALUE, locale ),
                        StringUtils.isNotEmpty( strNewNomenclature ) ? strNewNomenclature : I18nService.getLocalizedString( MESSAGE_NO_VALUE, locale ) );
            }
        }

        return strTaskInformation;
    }
}
