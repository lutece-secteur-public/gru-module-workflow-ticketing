/*
 * Copyright (c) 2002-2023, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.cc;

/**
 * TicketEmailExternalUserCc
 */
public class TicketEmailExternalUserCc
{
    /** The _n id cc. */
    private int _nIdCc;

    /** The _n id task. */
    private int _nIdTask;

    /** The _n id resource history. */
    private int _nIdResourceHistory;

    /** The _str email. */
    private String _strEmail;

    /** The _str field. */
    private String _strField;

    /**
     * @return the _nIdInfosHistory
     */
    public int getIdInfosHistory( )
    {
        return _nIdCc;
    }

    /**
     * @param nIdCc
     *            id cc
     */
    public void setIdInfosHistory( int nIdCc )
    {
        _nIdCc = nIdCc;
    }

    /**
     * @return the _nIdTask
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * @param nIdTask
     *            the nIdTask to set
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * @return the _nIdResourceHistory
     */
    public int getIdResourceHistory( )
    {
        return _nIdResourceHistory;
    }

    /**
     * @param nIdResourceHistory
     *            the nIdResourceHistory to set
     */
    public void setIdResourceHistory( int nIdResourceHistory )
    {
        _nIdResourceHistory = nIdResourceHistory;
    }

    /**
     * @return the _strEmail
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * @param strEmail
     *            the strEmail to set
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * @return the _strField
     */
    public String getField( )
    {
        return _strField;
    }

    /**
     * @param strField
     *            the strField to set
     */
    public void setField( String strField )
    {
        _strField = strField;
    }
}
