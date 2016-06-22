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

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.assignment.ITaskAutomaticAssignmentDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.assignment.UserAutomaticAssignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.PluginService;

import java.util.List;

import javax.inject.Inject;


/**
 *
 * AutomaticAssignmentService
 *
 */
public class AutomaticAssignmentService implements IAutomaticAssignmentService
{
    public static final String BEAN_NAME = "workflow-ticketing.automaticAssignmentService";
    @Inject
    private ITaskAutomaticAssignmentDAO _dao;

    /**
     * constructor
     */
    public AutomaticAssignmentService(  )
    {
        super(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assign( int idTask, String strUserAccessCode, String strSuffix )
    {
        // TODO Auto-generated method stub        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unassign( int nIdTask, String strSuffix )
    {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminUser getAssignedUser( int nIdTask, String strSuffix )
    {
        return _dao.getAssignedUser( nIdTask, strSuffix, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<UserAutomaticAssignmentConfig> getAllAutoAssignementConf( int nIdTask )
    {
        List<UserAutomaticAssignmentConfig> listAutoAssConfig = _dao.getAllAutoAssignementConf( nIdTask );

        if ( _dao.getAllAutoAssignementConf( nIdTask ).size(  ) == 0 )
        {
            listAutoAssConfig = _dao.initializeAssignementConf( nIdTask );
        }

        return listAutoAssConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAutomaticAssignmentConfig getAvailableAutoAssignementList( int nIdTask )
    {
        return _dao.getAvailableAutoAssignementList( nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unassignByUser( int nIdtask, String strUserAccessCode )
    {
        _dao.unassignByUser( nIdtask, strUserAccessCode, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
    }

    @Override
    public void removeConfig( int nIdTask )
    {
        _dao.delete( nIdTask );
    }
}
