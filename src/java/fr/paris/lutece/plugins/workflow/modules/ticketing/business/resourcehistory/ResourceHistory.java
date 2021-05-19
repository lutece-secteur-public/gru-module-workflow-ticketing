/*
 * Copyright8 (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory;

/**
 *
 * ResourceHistory
 *
 */
public class ResourceHistory
{
    private int _nIdHistory;
    private int _nIdChannel;
    private int _nIdUnitOld;
    private int _nIdUnitNew;

    /**
     *
     * @return the resource history id
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * the resource history id
     *
     * @param nIdHistory
     *            the resource history id
     */
    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    /**
     *
     * @return the channel id
     */
    public int getIdChannel( )
    {
        return _nIdChannel;
    }

    /**
     * the channel id
     *
     * @param nIdChannel
     *            the channel id
     */
    public void setIdChannel( int nIdChannel )
    {
        _nIdChannel = nIdChannel;
    }

    /**
     * Get old id unit assignee to the ticket
     * @return _nIdUnitOld
     *
     */
    public int getIdUnitOld( )
    {
        return _nIdUnitOld;
    }

    /**
     * Get new id unit assignee to the ticket
     * @return _nIdUnitNew;
     */
    public int getIdUnitNew( )
    {
        return _nIdUnitNew;
    }

    /**
     * Sets the id unit old.
     *
     * @param idUnitOld the new id unit old
     */
    public void setIdUnitOld( int idUnitOld )
    {
        _nIdUnitOld = idUnitOld;
    }

    /**
     * Sets the id unit new.
     *
     * @param idUnitNew the new id unit new
     */
    public void setIdUnitNew( int idUnitNew )
    {
        _nIdUnitNew = idUnitNew;
    }

}
