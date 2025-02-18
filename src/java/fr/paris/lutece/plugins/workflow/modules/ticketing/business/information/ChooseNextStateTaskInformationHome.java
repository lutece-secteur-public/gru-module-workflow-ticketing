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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.information;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public class ChooseNextStateTaskInformationHome
{
    private static IChooseNextStateTaskInformationDAO _dao = SpringContextService.getBean( "workflow-ticketing.chooseNextStateTaskInformationDAO" );

    /**
     * Private constructor
     */
    private ChooseNextStateTaskInformationHome( )
    {
        super( );
    }

    /**
     * Creates a task information
     *
     * @param taskInformation
     *            The task information to create
     * @return The task information which has been created
     */
    public static ChooseNextStateTaskInformation create( ChooseNextStateTaskInformation taskInformation )
    {
        _dao.insert( taskInformation );

        return taskInformation;
    }

    /**
     * Finds the task information for the specified couple {history id, task id}
     *
     * @param nIdHistory
     *            the history id
     * @param nIdTask
     *            the task id
     * @return the task information
     */
    public static ChooseNextStateTaskInformation find( int nIdHistory, int nIdTask )
    {
        return _dao.load( nIdHistory, nIdTask );
    }

    /**
     * Deletes the task information for the specified couple {history id, task id}
     *
     * @param nIdHistory
     * @param nIdTask
     */
    public static void remove( int nIdHistory, int nIdTask )
    {
        _dao.delete( nIdHistory, nIdTask );
    }
}
