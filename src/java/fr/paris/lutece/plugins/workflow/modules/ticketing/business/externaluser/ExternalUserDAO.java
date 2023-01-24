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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implementation of the ExternalUser DAO
 */
public class ExternalUserDAO implements IExternalUserDAO
{
    // PROPERTIES
    private static final String PROP_SEARCH_LIMIT = "workflow-ticketing.workflow.externaluser.search.limit";

    // constants
    private static final String CONSTANT_PERCENT = "%";

    // SQL
    private static final String SQL_SELECT_USER_ADMIN = "SELECT u.last_name, u.first_name, u.email, f.user_field_value FROM core_admin_user u "
            + "INNER JOIN core_user_role ur ON ur.id_user = u.id_user INNER JOIN core_admin_role role ON role.role_key = ur.role_key INNER JOIN core_admin_role_resource rr ON rr.role_key = role.role_key "
            + "INNER JOIN core_user_right r ON u.id_user = r.id_user " + "LEFT JOIN core_admin_user_field f ON u.id_user = f.id_user AND f.id_attribute = ? "
            + "WHERE u.status = 0 AND r.id_right = 'TICKETING_EXTERNAL_USER' AND rr.resource_type = 'WORKFLOW_ACTION_TYPE' ";
    private static final String SQL_SELECT_USER_ADMIN_WITHOUT_ATTRIBUTE = "SELECT u.last_name, u.first_name, u.email, NULL FROM core_admin_user u INNER JOIN core_user_right r ON u.id_user = r.id_user "
            + "INNER JOIN core_user_role ur ON ur.id_user = u.id_user INNER JOIN core_admin_role role ON role.role_key = ur.role_key INNER JOIN core_admin_role_resource rr ON rr.role_key = role.role_key "
            + "WHERE u.status = 0 AND r.id_right = 'TICKETING_EXTERNAL_USER' ";
    private static final String SQL_VALID_EMAIL_USER_ADMIN = "SELECT u.first_name, u.email FROM core_admin_user u INNER JOIN core_user_right r ON u.id_user = r.id_user "
            + "INNER JOIN core_user_role ur ON ur.id_user = u.id_user INNER JOIN core_admin_role role ON role.role_key = ur.role_key INNER JOIN core_admin_role_resource rr ON rr.role_key = role.role_key "
            + " WHERE u.status = 0 AND r.id_right = 'TICKETING_EXTERNAL_USER' AND u.email = ? ";
    private static final String SQL_WHERE_LASTNAME_CLAUSE = " u.last_name LIKE ? ";
    private static final String SQL_WHERE_EMAIL_CLAUSE = " u.email LIKE ? ";
    private static final String SQL_WHERE_ADDITIONAL_ATTRIBUTE_CLAUSE = " f.user_field_value LIKE ? ";
    private static final String SQL_WHERE_ACTION_RBAC_CLAUSE = " (rr.resource_id = ? OR rr.resource_id = '*') ";
    private static final String SQL_SEPARATOR_AND = " AND ";

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSearchLimit( )
    {
        String strLimit = AppPropertiesService.getProperty( PROP_SEARCH_LIMIT );
        int nLimit = 0;

        if ( !StringUtils.isEmpty( strLimit ) && StringUtils.isNumeric( strLimit ) )
        {
            nLimit = Integer.parseInt( strLimit );
        }

        return Math.max( 0, nLimit );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidEmail( String strEmail, String strActionId )
    {
        StringBuilder strQuery = new StringBuilder( SQL_VALID_EMAIL_USER_ADMIN );
        if ( StringUtils.isNotEmpty( strActionId ) )
        {
            strQuery.append( SQL_SEPARATOR_AND );
            strQuery.append( SQL_WHERE_ACTION_RBAC_CLAUSE );
        }

        boolean bEmailOk = false;

        try ( DAOUtil daoUtil = new DAOUtil( strQuery.toString( ), WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, strEmail );

            if ( StringUtils.isNotEmpty( strActionId ) )
            {
                daoUtil.setString( nIndex++, strActionId );
            }

            daoUtil.executeQuery( );

            bEmailOk = daoUtil.next( );
        }
        return bEmailOk;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ExternalUser> findExternalUser( String strLastname, String strEmail, String strIdAttribute, String strAttributeValue, String strActionId )
    {
        StringBuilder strQuery = null;

        if ( StringUtils.isEmpty( strIdAttribute ) )
        {
            strQuery = new StringBuilder( SQL_SELECT_USER_ADMIN_WITHOUT_ATTRIBUTE );
        }
        else
        {
            strQuery = new StringBuilder( SQL_SELECT_USER_ADMIN );
        }

        if ( StringUtils.isNotEmpty( strLastname ) )
        {
            strQuery.append( SQL_SEPARATOR_AND );
            strQuery.append( SQL_WHERE_LASTNAME_CLAUSE );
        }

        if ( StringUtils.isNotEmpty( strEmail ) )
        {
            strQuery.append( SQL_SEPARATOR_AND );
            strQuery.append( SQL_WHERE_EMAIL_CLAUSE );
        }

        if ( StringUtils.isNotEmpty( strIdAttribute ) && StringUtils.isNotEmpty( strAttributeValue ) )
        {
            strQuery.append( SQL_SEPARATOR_AND );
            strQuery.append( SQL_WHERE_ADDITIONAL_ATTRIBUTE_CLAUSE );
        }

        if ( StringUtils.isNotEmpty( strActionId ) )
        {
            strQuery.append( SQL_SEPARATOR_AND );
            strQuery.append( SQL_WHERE_ACTION_RBAC_CLAUSE );
        }

        Set<ExternalUser> lstExternalUser = new TreeSet<>( new ExternalUserComparator( ) );

        try ( DAOUtil daoUtil = new DAOUtil( strQuery.toString( ), WorkflowTicketingPlugin.getPlugin( ) ) )
        {
            int nIndex = 1;

            if ( StringUtils.isNotEmpty( strIdAttribute ) )
            {
                daoUtil.setString( nIndex++, strIdAttribute );
            }

            if ( StringUtils.isNotEmpty( strLastname ) )
            {
                daoUtil.setString( nIndex++, CONSTANT_PERCENT + strLastname + CONSTANT_PERCENT );
            }

            if ( StringUtils.isNotEmpty( strEmail ) )
            {
                daoUtil.setString( nIndex++, CONSTANT_PERCENT + strEmail + CONSTANT_PERCENT );
            }

            if ( StringUtils.isNotEmpty( strIdAttribute ) && StringUtils.isNotEmpty( strAttributeValue ) )
            {
                daoUtil.setString( nIndex++, CONSTANT_PERCENT + strAttributeValue + CONSTANT_PERCENT );

            }

            if ( StringUtils.isNotEmpty( strActionId ) )
            {
                daoUtil.setString( nIndex++, strActionId );
            }

            daoUtil.executeQuery( );



            while ( daoUtil.next( ) )
            {
                ExternalUser externalUser = new ExternalUser( );
                externalUser.setLastname( daoUtil.getString( 1 ) );
                externalUser.setFirstname( daoUtil.getString( 2 ) );
                externalUser.setEmail( daoUtil.getString( 3 ) );
                externalUser.setAdditionalAttribute( daoUtil.getString( 4 ) );
                lstExternalUser.add( externalUser );
            }
        }
        return ( new ArrayList<ExternalUser>( lstExternalUser ) );
    }
}
