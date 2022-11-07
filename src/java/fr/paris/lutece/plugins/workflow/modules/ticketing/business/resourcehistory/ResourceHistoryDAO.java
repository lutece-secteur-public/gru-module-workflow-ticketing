/*
 * Copyright (c) 2002-2022, City of Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 * ResourceHistoryDAO
 *
 */
public class ResourceHistoryDAO implements IResourceHistoryDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_history,id_channel  "
            + "FROM workflow_resource_history_ticketing WHERE id_history=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_resource_history_ticketing "
            + "(id_history,id_channel, id_unit_old, id_unit_new ) VALUES( ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE_BY_HISTORY = "DELETE FROM workflow_resource_history_ticketing WHERE id_history=?";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE = "DELETE wrht FROM workflow_resource_history wrh, workflow_resource_history_ticketing wrht WHERE wrh.id_history = wrht.id_history AND wrh.id_resource = ? AND wrh.resource_type = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( ResourceHistory resourceHistory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, resourceHistory.getIdHistory( ) );
            if ( resourceHistory.getIdChannel( ) > 0 )
            {
                daoUtil.setInt( 2, resourceHistory.getIdChannel( ) );
            }
            else
            {
                daoUtil.setIntNull( 2 );
            }

            if ( resourceHistory.getIdUnitOld( ) > 0 )
            {
                daoUtil.setInt( 3, resourceHistory.getIdUnitOld( ) );
            }
            else
            {
                daoUtil.setIntNull( 3 );
            }

            if ( resourceHistory.getIdUnitNew( ) > 0 )
            {
                daoUtil.setInt( 4, resourceHistory.getIdUnitNew( ) );
            }
            else
            {
                daoUtil.setIntNull( 4 );
            }

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceHistory load( int nIdHistory, Plugin plugin )
    {
        ResourceHistory resourceHistory = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdHistory );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                resourceHistory = new ResourceHistory( );
                resourceHistory.setIdHistory( daoUtil.getInt( 1 ) );
                resourceHistory.setIdChannel( daoUtil.getInt( 2 ) );
            }
        }
        return resourceHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByHistory( int nIdHistory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, plugin ) )
        {
            daoUtil.setInt( 1, nIdHistory );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByResource( int nIdResource, String strResourceType, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_RESOURCE, plugin ) )
        {
            daoUtil.setInt( 1, nIdResource );
            daoUtil.setString( 2, strResourceType );

            daoUtil.executeUpdate( );
        }
    }
}
