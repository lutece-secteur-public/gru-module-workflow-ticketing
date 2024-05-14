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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.information;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 *
 * ITaskInformationDAO
 *
 */
public interface ITaskInformationDAO
{
    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "workflow-ticketing.taskInformationDAO";

    /**
     * Insert new record
     *
     * @param taskInformation
     *            the TaskInformation Object
     * @param plugin
     *            the plugin
     */
    void insert( TaskInformation taskInformation, Plugin plugin );

    /**
     * Load a record by primary key
     *
     * @param nIdHistory
     *            the action history id
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     * @return TaskInformation Object
     */
    TaskInformation load( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Upadte the value info
     *
     * @param idHistory
     *            the history id
     * @param infoValue
     *            the info value
     * @param plugin
     *            the plugin
     */
    void update( int idHistory, String infoValue, Plugin plugin );

    /**
     * Remove information by history
     *
     * @param nIdHistory
     *            the History id
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     */
    void deleteByHistory( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Remove information by task
     *
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     */
    void deleteByTask( int nIdTask, Plugin plugin );

    /**
     * Get the value in history
     *
     * @param idHistory
     *            the history id
     * @param plugin
     *            the plugin
     */
    String getInfoHistoryValueByIdHistory( int idHistory, Plugin plugin );

}
