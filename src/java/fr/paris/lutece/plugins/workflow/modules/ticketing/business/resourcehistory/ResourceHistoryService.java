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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.resourcehistory.IResourceHistoryInformationService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 *
 * ResourceHistoryService
 *
 */
public class ResourceHistoryService implements IResourceHistoryService, IResourceHistoryInformationService
{
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_SERVICE = "workflow-ticketing.resourceHistoryService";
    @Inject
    private IResourceHistoryDAO _dao;
    @Inject
    private fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService _resourceHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create( ResourceHistory resourceHistory, Plugin plugin )
    {
        _dao.insert( resourceHistory, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeByHistory( int nIdHistory, Plugin plugin )
    {
        _dao.deleteByHistory( nIdHistory, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceHistory findByPrimaryKey( int nIdHistory, Plugin plugin )
    {
        return _dao.load( nIdHistory, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeByResource( int nIdResource, String strResourceType )
    {
        _dao.deleteByResource( nIdResource, strResourceType, WorkflowUtils.getPlugin( ) );
    }

    /**
     * returns the list of id_history/channel associated to a ticket resource.
     *
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @param nIdWorkflow
     *            the workflow id
     * @return the list of id_history/channel associated to a ticket resource
     */
    @Override
    public Map<String, Channel> getChannelHistoryMap( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        Map<String, Channel> mapHistoryChannel = new HashMap<>( );

        List<fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory> listResourceHistory = _resourceHistoryService
                .getAllHistoryByResource( nIdResource, strResourceType, nIdWorkflow );

        for ( fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory resourceHistory : listResourceHistory )
        {
            fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.ResourceHistory resourceHistoryChannel = findByPrimaryKey(
                    resourceHistory.getId( ), WorkflowUtils.getPlugin( ) );

            if ( resourceHistoryChannel != null )
            {
                Channel channel = ChannelHome.findByPrimaryKey( resourceHistoryChannel.getIdChannel( ) );
                mapHistoryChannel.put( Integer.toString( resourceHistoryChannel.getIdHistory( ) ), channel );
            }
        }

        return mapHistoryChannel;
    }

    /**
     * remove all the list of ticketing history resource associated to a ticket resource.
     *
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @param nIdWorkflow
     *            the workflow id
     */
    public void removeResourceHistory( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        List<fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory> listResourceHistory = _resourceHistoryService
                .getAllHistoryByResource( nIdResource, strResourceType, nIdWorkflow );

        for ( fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory resourceHistory : listResourceHistory )
        {
            fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory.ResourceHistory resourceHistoryTicketing = findByPrimaryKey(
                    resourceHistory.getId( ), WorkflowUtils.getPlugin( ) );

            if ( resourceHistoryTicketing != null )
            {
                removeByHistory( resourceHistory.getId( ), WorkflowUtils.getPlugin( ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceHistory findOldUnitByPrimaryKey( int nIdHistory, Plugin plugin )
    {
        return _dao.loadUnitOld( nIdHistory, plugin );
    }

}
