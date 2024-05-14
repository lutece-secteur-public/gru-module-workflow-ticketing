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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser;

/**
 * ExternalUser
 */
public class ExternalUser
{
    private String _strFirstname;
    private String _strLastname;
    private String _strEmail;
    private String _strAdditionalAttribute;

    /**
     * @return the strFirstname
     */
    public String getFirstname( )
    {
        return _strFirstname;
    }

    /**
     * @param strFirstname
     *            the firstname to set
     */
    public void setFirstname( String strFirstname )
    {
        this._strFirstname = strFirstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname( )
    {
        return _strLastname;
    }

    /**
     * @param strLastname
     *            the lastname to set
     */
    public void setLastname( String strLastname )
    {
        this._strLastname = strLastname;
    }

    /**
     * @return the email
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * @param strEmail
     *            the email to set
     */
    public void setEmail( String strEmail )
    {
        this._strEmail = strEmail;
    }

    /**
     * @return the additional attribute value
     */
    public String getAdditionalAttribute( )
    {
        return _strAdditionalAttribute;
    }

    /**
     * @param strAdditionalAttribute
     *            the additional attribute value to set
     */
    public void setAdditionalAttribute( String strAdditionalAttribute )
    {
        this._strAdditionalAttribute = strAdditionalAttribute;
    }
}
