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

package fr.paris.lutece.plugins.workflow.modules.ticketing.web;

import java.sql.Timestamp;
import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.upload.business.file.UploadFile;
import fr.paris.lutece.portal.business.user.AdminUser;

/**
 * Class to display ticket email external user message and associated objects
 */
public class TicketEmailExternalUserMessageDisplay
{
    private Timestamp _dDateCreate;

    private String _strMessageQuestion;

    private AdminUser _adminUser;

    private List<UploadFile> _listUploadedFiles;

    /**
     * @return the _dateCreate
     */
    public Timestamp getDateCreate( )
    {
        return _dDateCreate;
    }

    /**
     * @param _dateCreate
     *            the _dateCreate to set
     */
    public void setDateCreate( Timestamp dDateCreate )
    {
        this._dDateCreate = dDateCreate;
    }

    /**
     * @return the _strMessageQuestion
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
     * @return the _adminUser
     */
    public AdminUser getAdminUser( )
    {
        return _adminUser;
    }

    /**
     * @param adminUser
     *            the adminUser to set
     */
    public void setAdminUser( AdminUser adminUser )
    {
        this._adminUser = adminUser;
    }

    /**
     * @return the _listUploadedFiles
     */
    public List<UploadFile> getUploadedFiles( )
    {
        return _listUploadedFiles;
    }

    /**
     * @param listUploadedFiles
     *            the listUploadedFiles to set
     */
    public void setUploadedFiles( List<UploadFile> listUploadedFiles )
    {
        this._listUploadedFiles = listUploadedFiles;
    }

}
