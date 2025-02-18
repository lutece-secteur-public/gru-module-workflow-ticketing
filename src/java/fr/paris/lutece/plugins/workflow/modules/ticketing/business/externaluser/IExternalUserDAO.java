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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser;

import java.util.List;

/**
 * Interface for the ExternalUser DAO
 */
public interface IExternalUserDAO
{
    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "workflow-ticketing.ExternalUserDAO";

    /**
     * @return the value store in properties or null
     */
    int getSearchLimit( );

    /**
     * Check if email is a valid email for ExternalUser
     *
     * @param strEmail
     *            the email to check
     * @param strNextActionId
     *            the action the externaluser must be allowed to exectute
     *
     * @return true if the email is ok
     */
    boolean isValidEmail( String strEmail, String strNextActionId );

    /**
     * return list of ExternalUser which match the filter
     *
     * @param strLastname
     *            lastname to search
     * @param strEmail
     *            email to search
     * @param strIdAttribute
     *            id of user attribute to search
     * @param strAttributeValue
     *            value of user attribute to search
     * @param strActionId
     *            Action Id
     * @return list of ExternalUser
     */
    List<ExternalUser> findExternalUser( String strLastname, String strEmail, String strIdAttribute, String strAttributeValue, String strActionId );
}
