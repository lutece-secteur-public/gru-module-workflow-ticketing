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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.resourcehistory;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 *
 * IResourceHistoryDAO
 *
 */
public interface IResourceHistoryDAO
{
    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "workflow-ticketing.resourceHistoryDAO";

    /**
     * Insert new record
     *
     * @param resourceHistory
     *            the ResourceHistory Object
     * @param plugin
     *            the plugin
     */
    void insert( ResourceHistory resourceHistory, Plugin plugin );

    /**
     * Load a record by primary key
     *
     * @param nIdHistory
     *            the history id
     * @param plugin
     *            the plugin
     * @return ResourceHistory Object
     */
    ResourceHistory load( int nIdHistory, Plugin plugin );

    /**
     * Remove resourceHistory by history
     *
     * @param nIdHistory
     *            the History id
     * @param plugin
     *            the plugin
     */
    void deleteByHistory( int nIdHistory, Plugin plugin );

    /**
     * Remove resourceHistory by resource
     *
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @param plugin
     *            the plugin
     */
    void deleteByResource( int nIdResource, String strResourceType, Plugin plugin );

    /**
     * Load a record by primary key id_history and old unit
     *
     * @param nIdHistory
     *            the history id
     * @param plugin
     *            the plugin
     * @return ResourceHistory Object
     */
    ResourceHistory loadUnitOld( int nIdHistory, Plugin plugin );

    /**
     * Get the list of id history for a resource(ticket)
     *
     * @param idTicket
     *            the ticket id
     * @param plugin
     *            the plugin
     */
    List<Integer> getIdHistoryListByResource( int idTicket, Plugin plugin );
}
