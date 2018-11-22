/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implementation for the TaskTicketEmailExternalUserConfig DAO
 */
public class TaskTicketEmailExternalUserConfigDAO implements ITaskConfigDAO<TaskTicketEmailExternalUserConfig>
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, message_direction, id_following_action, id_contact_attribute, default_subject FROM workflow_task_ticketing_email_external_user_config WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT              = " INSERT INTO workflow_task_ticketing_email_external_user_config ( id_task, message_direction, id_following_action, id_contact_attribute, default_subject ) VALUES ( ?,?,?,?,? ) ";
    private static final String SQL_QUERY_UPDATE              = "UPDATE workflow_task_ticketing_email_external_user_config SET message_direction = ?, id_following_action = ?, id_contact_attribute = ?, default_subject = ? WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE              = " DELETE FROM workflow_task_ticketing_email_external_user_config WHERE id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskTicketEmailExternalUserConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowTicketingPlugin.getPlugin( ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getIdTask( ) );
        daoUtil.setInt( nIndex++, config.getMessageDirectionExternalUser( ).ordinal( ) );

        if ( config.getIdFollowingAction( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        } else
        {
            daoUtil.setInt( nIndex++, config.getIdFollowingAction( ) );
        }

        if ( config.getIdContactAttribute( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        } else
        {
            daoUtil.setInt( nIndex++, config.getIdContactAttribute( ) );
        }

        if ( config.getDefaultSubject( ) == null )
        {
            daoUtil.setString( nIndex++, StringUtils.EMPTY );
        } else
        {
            daoUtil.setString( nIndex++, config.getDefaultSubject( ) );
        }

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskTicketEmailExternalUserConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowTicketingPlugin.getPlugin( ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getMessageDirectionExternalUser( ).ordinal( ) );

        if ( config.getIdFollowingAction( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        } else
        {
            daoUtil.setInt( nIndex++, config.getIdFollowingAction( ) );
        }

        if ( config.getIdContactAttribute( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        } else
        {
            daoUtil.setInt( nIndex++, config.getIdContactAttribute( ) );
        }

        if ( config.getDefaultSubject( ) == null )
        {
            daoUtil.setString( nIndex++, StringUtils.EMPTY );
        } else
        {
            daoUtil.setString( nIndex++, config.getDefaultSubject( ) );
        }
        daoUtil.setInt( nIndex++, config.getIdTask( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskTicketEmailExternalUserConfig load( int nIdTask )
    {
        TaskTicketEmailExternalUserConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowTicketingPlugin.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery( );

        int nIndex = 1;

        if ( daoUtil.next( ) )
        {
            config = new TaskTicketEmailExternalUserConfig( );
            config.setIdTask( daoUtil.getInt( nIndex++ ) );
            config.setMessageDirectionExternalUser( MessageDirectionExternalUser.valueOf( daoUtil.getInt( nIndex++ ) ) );

            String strIdFollowingAction = daoUtil.getString( nIndex++ );

            if ( StringUtils.isNotEmpty( strIdFollowingAction ) )
            {
                config.setIdFollowingAction( Integer.parseInt( strIdFollowingAction ) );
            }

            String strIdContactAttribute = daoUtil.getString( nIndex++ );

            if ( StringUtils.isNotEmpty( strIdContactAttribute ) )
            {
                config.setIdContactAttribute( Integer.parseInt( strIdContactAttribute ) );
            }

            config.setDefaultSubject( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free( );

        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowTicketingPlugin.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
