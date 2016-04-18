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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.reference;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.reference.ITicketReferenceDAO;

import org.jsoup.helper.StringUtil;

import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * This class manages ticket reference in the following format : <prefix><date><sequence>
 *
 */
public class TicketReferencePrefixAndNumberService implements ITicketReferenceService
{
    private static final int SEQUENCE_INITIAL_VALUE = 0;
    private static final String REFERENCE_FORMAT = "%s%05d";
    private static final String DATE_FORMAT = "yyMM";
    private SimpleDateFormat _simpleDateFormat = new SimpleDateFormat( DATE_FORMAT );
    private ITicketReferenceDAO _dao;

    /**
     * Constructor of a TicketReferencePrefixAndNumberService
     * @param ticketReferenceDAO the dao to access the ticket reference
     */
    public TicketReferencePrefixAndNumberService( ITicketReferenceDAO ticketReferenceDAO )
    {
        _dao = ticketReferenceDAO;
    }

    @Override
    public String generateReference( Ticket ticket )
    {
        TicketType ticketType = TicketTypeHome.findByPrimaryKey( ticket.getIdTicketType(  ) );

        Date dateToday = new Date(  );
        String strPrefixWithDate = ticketType.getReferencePrefix(  ) + _simpleDateFormat.format( dateToday );

        String strSequence = _dao.findLastTicketReference( strPrefixWithDate );
        int nSequence = SEQUENCE_INITIAL_VALUE;

        if ( !StringUtil.isBlank( strSequence ) )
        {
            nSequence = Integer.parseInt( strSequence );
        }

        nSequence++;

        return String.format( REFERENCE_FORMAT, strPrefixWithDate, nSequence );
    }
}
