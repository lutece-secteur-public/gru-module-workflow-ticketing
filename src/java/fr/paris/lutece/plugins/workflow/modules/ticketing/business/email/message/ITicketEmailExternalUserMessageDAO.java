/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message;

import java.util.List;

/**
 * TicketEmailExternalUserMessage DAO interface
 */
public interface ITicketEmailExternalUserMessageDAO
{
    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "workflow-ticketing.ticketEmailExternalUserMessageDAO";

    /**
     * Create Question.
     *
     * @param emailExternalUserMessage
     *            the TicketEmailExternalUserMessage
     */
    void createQuestion( TicketEmailExternalUserMessage emailExternalUserMessage );

    /**
     * Test if the id given is the last question of a ticket
     * 
     * @param nIdTicket
     *            ticket ID
     * @param nIdMessageExternalUser
     *            message ID
     * @return true if the nIdMessageExternalUser is the last question of nIdTicket
     */
    boolean isLastQuestion( int nIdTicket, int nIdMessageExternalUser );

    /**
     * add Answer.
     *
     * @param nIdTicket
     *            the id of the ticket
     * @param strReponse
     *            the response to the question
     * @return the id of the TicketEmailExternalUserMessage
     */
    int addAnswer( int nIdTicket, String strReponse );

    /**
     * Close all tickets for given id_ticket.
     *
     * @param nIdTicket
     *            the id of the ticket
     */
    public void closeMessagesByIdTicket( int nIdTicket );

    /**
     * Load.
     *
     * @param nIdMessageExternalUser
     *            the id of message
     * @return the TicketEmailExternalUserMessage element
     */
    TicketEmailExternalUserMessage loadByIdMessageExternalUser( int nIdMessageExternalUser );

    /**
     * Load first message by ticket
     *
     * @param nIdTicket
     *            the id of ticket
     * @return the TicketEmailExternalUserMessage element
     */
    TicketEmailExternalUserMessage loadFirstByIdTicket( int nIdTicket );

    /**
     * Load list of TicketEmailExternalUserMessage by given nIdTicket
     *
     * @param nIdTicket
     *            the id of message
     * @return the list of TicketEmailExternalUserMessage
     */
    List<TicketEmailExternalUserMessage> loadByIdTicketNotClosed( int nIdTicket );

    /**
     * Load last of TicketEmailExternalUserMessage by given nIdTicket
     *
     * @param nIdTicket
     *            the id of message
     * @return the last TicketEmailExternalUserMessage
     */
    TicketEmailExternalUserMessage loadLastByIdTicket( int nIdTicket );

    /**
     * Delete by idMessageExternalUser.
     *
     * @param nIdMessageExternalUser
     *            the n id of message
     */
    void deleteByIdMessageExternalUser( int nIdMessageExternalUser );
}
