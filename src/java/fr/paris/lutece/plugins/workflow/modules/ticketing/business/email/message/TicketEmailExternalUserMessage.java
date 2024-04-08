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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message;

/**
 * TicketEmailExternalUserMessage
 */
public class TicketEmailExternalUserMessage
{
    /** The id message. */
    private int _nIdMessageExternalUser;

    /** The id demand. */
    private int _nIdTicket;

    /** the recipients email */
    private String _strEmailRecipients;

    /** the recipients email in copy */
    private String _strEmailRecipientsCc;

    /** the message to the agent */
    private String _strMessageQuestion;

    /** the response to the agent */
    private String _strMessageResponse;

    /** the subject of the mail */
    private String _strEmailSubject;

    /** true if the external user has answered */
    private boolean _bIsAnswered;

    /**
     * @return the idMessageExternalUser
     */
    public int getIdMessageExternalUser( )
    {
        return _nIdMessageExternalUser;
    }

    /**
     * @param nIdMessageExternalUser
     *            the idMessageExternalUser to set
     */
    public void setIdMessageExternalUser( int nIdMessageExternalUser )
    {
        this._nIdMessageExternalUser = nIdMessageExternalUser;
    }

    /**
     * @return the IdTicket
     */
    public int getIdTicket( )
    {
        return _nIdTicket;
    }

    /**
     * @param nIdTicket
     *            the IdTicket to set
     */
    public void setIdTicket( int nIdTicket )
    {
        this._nIdTicket = nIdTicket;
    }

    /**
     * @return the _strEmailRecipients
     */
    public String getEmailRecipients( )
    {
        return _strEmailRecipients;
    }

    /**
     * @param strEmailRecipients
     *            the strEmailRecipients to set
     */
    public void setEmailRecipients( String strEmailRecipients )
    {
        this._strEmailRecipients = strEmailRecipients;
    }

    /**
     * @return the _strEmailRecipientsCc
     */
    public String getEmailRecipientsCc( )
    {
        return _strEmailRecipientsCc;
    }

    /**
     * @param strEmailRecipientsCc
     *            the strEmailRecipientsCc to set
     */
    public void setEmailRecipientsCc( String strEmailRecipientsCc )
    {
        this._strEmailRecipientsCc = strEmailRecipientsCc;
    }

    /**
     * @return the strMessageQuestion
     */
    public String getMessageQuestion( )
    {
        return _strMessageQuestion;
    }

    /**
     * @param strMessageQuestion
     *            the strMessageQuestion to set
     */
    public void setMessageQuestion( String strMessageQuestion )
    {
        this._strMessageQuestion = strMessageQuestion;
    }

    /**
     * @return the strMessageResponse
     */
    public String getMessageResponse( )
    {
        return _strMessageResponse;
    }

    /**
     * @param strMessageResponse
     *            the strMessageResponse to set
     */
    public void setMessageResponse( String strMessageResponse )
    {
        this._strMessageResponse = strMessageResponse;
    }

    /**
     * @return the bIsAnswered
     */
    public boolean getIsAnswered( )
    {
        return _bIsAnswered;
    }

    /**
     * @param bIsAnswered
     *            the bIsAnswered to set
     */
    public void setIsAnswered( boolean bIsAnswered )
    {
        this._bIsAnswered = bIsAnswered;
    }

    public String getEmailSubject( )
    {
        return _strEmailSubject;
    }

    public void setEmailSubject( String emailSubject )
    {
        _strEmailSubject = emailSubject;
    }
}
