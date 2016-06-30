/*
 * Copyright (c) 2002-2016, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.assignment;

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.assignment.UserAutomaticAssignmentConfig;
import fr.paris.lutece.portal.business.user.AdminUser;

import java.util.List;


/**
 *
 * Automatic assignment service interface
 *
 */
public interface IAutomaticAssignmentService
{
    /**
     * insert automaticAssignement
     * @param idTask id of task
     * @param strUserAccessCode user access code
     * @param strSuffix suffix to add
     */
    void assign( int idTask, String strUserAccessCode, String strSuffix );

    /**
     * return adminUser assigned to the suffix
     * @param nIdTask task id
     * @param strSuffix suffix
     * @return adminUser assigned to the suffix
     */
    AdminUser getAssignedUser( int nIdTask, String strSuffix );

    /**
     * returns user assignment conf
     * @param nIdTask id of task
     * @param adminUser  admin user
     * @return user assignment conf
     */
    UserAutomaticAssignmentConfig getUserAssignments( int nIdTask, AdminUser adminUser );

    /**
     *
     * @param nIdTask task id
     * @return map containing all of automatic assignement config
     * unassigned conf is put on StringUtils.Empty entry of map
     */
    List<UserAutomaticAssignmentConfig> getAllAutoAssignementConf( int nIdTask );

    /**
     * return AutomaticAssignment which are not linked to user
     * @param nIdTask id task
     * @return AutomaticAssignment which are not linked to user
     */
    UserAutomaticAssignmentConfig getAvailableAutoAssignementList( int nIdTask );

    /**
     * remove all assignement for a user
     * @param nIdtask id of task
     * @param strUserAccessCode user access code who will no longer have autoassignment
     */
    void unassignByUser( int nIdtask, String strUserAccessCode );

    /**
     * remove all configuration for a task
     * @param nIdtask task id
     */
    void removeConfig( int nIdtask );
}
