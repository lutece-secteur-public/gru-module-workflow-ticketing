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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.assignment;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * TaskEditTicketConfigDAO
 *
 */
public class TaskAutomaticAssignmentDAO implements ITaskAutomaticAssignmentDAO
{
    private static final String SQL_QUERY_UNASSIGN = "UPDATE workflow_task_ticketing_automatic_assignment_config SET user_access_code = NULL " +
        " WHERE id_task = ? AND assignment_suffix = ?";
    private static final String SQL_QUERY_ASSIGN = "UPDATE workflow_task_ticketing_automatic_assignment_config SET user_access_code = ? " +
        " WHERE id_task = ? AND assignment_suffix = ?";
    private static final String SQL_QUERY_FIND_UNASSIGNED = "SELECT  assignment_suffix FROM workflow_task_ticketing_automatic_assignment_config WHERE user_access_code = NULL AND id_task = ?  ORDER BY assignment_suffix ASC";
    private static final String SQL_QUERY_FIND_BY_SUFFIX = "SELECT user_access_code FROM workflow_task_ticketing_automatic_assignment_config WHERE id_task = ? AND assignment_suffix = ? AND user_access_code IS NOT NULL ORDER BY assignment_suffix ASC";
    private static final String SQL_QUERY_UNASSIGN_BY_USER_ACCESS_CODE = "UPDATE workflow_task_ticketing_automatic_assignment_config SET user_access_code = NULL WHERE id_task = ? AND user_access_code = ? ";
    private static final String SQL_QUERY_FIND_ALL = "SELECT user_access_code, assignment_suffix FROM workflow_task_ticketing_automatic_assignment_config WHERE id_task = ? ORDER BY assignment_suffix ASC";
    private static final String SQL_QUERY_DELETE_ALL = "DELETE FROM workflow_task_ticketing_automatic_assignment_config WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_ticketing_automatic_assignment_config(id_task,assignment_suffix,user_access_code) VALUES(?, ?, ?);";
    private static final String PROPERTY_AUTOMATIC_ASSIGNMENT_NB_SLOT = "workflow-ticketing.workflow.automatic_assignment.nbSlot";

    /**
     * {@inheritDoc}
     */
    @Override
    public void assign( int idTask, String strUserAccessCode, String strSuffix, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ASSIGN, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        int nIndex = 1;
        daoUtil.setString( nIndex++, strUserAccessCode );
        daoUtil.setInt( nIndex++, idTask );
        daoUtil.setString( nIndex++, strSuffix );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unassign( int nIdtask, String strSuffix, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UNASSIGN,
                PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, nIdtask );
        daoUtil.setString( nIndex++, strSuffix );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unassignByUser( int nIdtask, String strUserAccessCode, Plugin plugin )
    {
        if ( StringUtils.isNotEmpty( strUserAccessCode ) )
        {
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UNASSIGN_BY_USER_ACCESS_CODE,
                    PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdtask );
            daoUtil.setString( nIndex++, strUserAccessCode );
            daoUtil.executeUpdate(  );
            daoUtil.free(  );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminUser getAssignedUser( int nIdTask, String strSuffix, Plugin plugin )
    {
        AdminUser adminUser = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_SUFFIX,
                PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.setString( 2, strSuffix );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            String strUserAccessCode = daoUtil.getString( nIndex++ );

            if ( StringUtils.isNotBlank( strUserAccessCode ) )
            {
                adminUser = AdminUserHome.findUserByLogin( strUserAccessCode );
            }
        }

        daoUtil.free(  );

        return adminUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAutomaticAssignmentConfig getAvailableAutoAssignementList( int nIdTask )
    {
        UserAutomaticAssignmentConfig userAutoAssignConf = new UserAutomaticAssignmentConfig(  );
        userAutoAssignConf.setAdminUser( null );

        List<String> listAssignedSuffix = new ArrayList<String>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_UNASSIGNED,
                PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            listAssignedSuffix.add( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free(  );
        userAutoAssignConf.setAssignedSuffix( listAssignedSuffix );

        return userAutoAssignConf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserAutomaticAssignmentConfig> getAllAutoAssignementConf( int nIdTask )
    {
        Map<String, UserAutomaticAssignmentConfig> mapAssignmentConfig = new HashMap<String, UserAutomaticAssignmentConfig>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL,
                PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            int nIndex = 1;

            String strUserAccessCode = daoUtil.getString( nIndex++ );

            if ( strUserAccessCode == null )
            {
                strUserAccessCode = StringUtils.EMPTY;
            }

            String strSuffix = daoUtil.getString( nIndex++ );

            if ( mapAssignmentConfig.containsKey( strUserAccessCode ) )
            {
                mapAssignmentConfig.get( strUserAccessCode ).getAssignedSuffix(  ).add( strSuffix );
            }
            else
            {
                UserAutomaticAssignmentConfig userAssignConf = new UserAutomaticAssignmentConfig(  );
                AdminUser adminUser = AdminUserHome.findUserByLogin( strUserAccessCode );
                userAssignConf.setAdminUser( adminUser );
                userAssignConf.getAssignedSuffix(  ).add( strSuffix );
                mapAssignmentConfig.put( strUserAccessCode, userAssignConf );
            }
        }

        daoUtil.free(  );

        return new ArrayList<UserAutomaticAssignmentConfig>( mapAssignmentConfig.values(  ) );
    }

    @Override
    public List<UserAutomaticAssignmentConfig> initializeAssignementConf( int nIdTask )
    {
        int nSlotNb = AppPropertiesService.getPropertyInt( PROPERTY_AUTOMATIC_ASSIGNMENT_NB_SLOT, 100 );

        for ( int nCpt = 0; nCpt < nSlotNb; nCpt++ )
        {
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT,
                    PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdTask );
            daoUtil.setString( nIndex++,
                StringUtils.leftPad( String.valueOf( nCpt ), String.valueOf( nSlotNb ).length(  ) - 1, "0" ) );
            //no user assignment => set user_access_code to null
            daoUtil.setString( nIndex++, null );
            daoUtil.executeUpdate(  );
            daoUtil.free(  );
        }

        return getAllAutoAssignementConf( nIdTask );
    }

    /**
     * insert full configuration (used for task copy)
     * {@inheritDoc}
     */
    @Override
    public void insert( TaskAutomaticAssignmentConfig config )
    {
        if ( config != null )
        {
            if ( ( config.getAutomaticAssignmentConf(  ) != null ) &&
                    ( config.getAutomaticAssignmentConf(  ).size(  ) > 0 ) )
            {
                for ( UserAutomaticAssignmentConfig userAutoAssignConf : config.getAutomaticAssignmentConf(  ) )
                {
                    if ( ( userAutoAssignConf != null ) && ( userAutoAssignConf.getAssignedSuffix(  ) != null ) &&
                            ( userAutoAssignConf.getAssignedSuffix(  ).size(  ) > 0 ) )
                    {
                        for ( String strSuffix : userAutoAssignConf.getAssignedSuffix(  ) )
                        {
                            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT,
                                    PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
                            int nIndex = 1;
                            daoUtil.setInt( nIndex++, config.getIdTask(  ) );
                            daoUtil.setString( nIndex++, strSuffix );
                            daoUtil.setString( nIndex++,
                                ( userAutoAssignConf.getAdminUser(  ) != null )
                                ? userAutoAssignConf.getAdminUser(  ).getAccessCode(  ) : null );
                            daoUtil.executeUpdate(  );
                            daoUtil.free(  );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void store( TaskAutomaticAssignmentConfig config )
    {
        //unused, dedicated ITaskAutomaticAssignmentDAO methods are used to save config
    }

    /**
     * load full configuration
     * {@inheritDoc}
     */
    @Override
    public TaskAutomaticAssignmentConfig load( int nIdTask )
    {
        TaskAutomaticAssignmentConfig config = new TaskAutomaticAssignmentConfig(  );
        config.setAutomaticAssignmentConf( getAllAutoAssignementConf( nIdTask ) );

        return config;
    }

    /**
     * delete full configuration
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL,
                PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
