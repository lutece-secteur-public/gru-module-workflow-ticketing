package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config.MessageDirectionExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;
import org.apache.commons.lang.StringUtils;

public class TaskNotifyWaitingTicketConfigDAO implements ITaskConfigDAO<TaskNotifyWaitingTicketConfig>
{


    //language=MySQL
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, message_direction, id_following_action, id_contact_attribute, default_subject FROM workflow_task_ticketing_email_external_user_config WHERE id_task = ? ";
    //language=MySQL
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_email_external_user_config ( id_task, message_direction, id_following_action, id_contact_attribute, default_subject ) VALUES ( ?,?,?,?,? ) ";
    //language=MySQL
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_ticketing_email_external_user_config SET message_direction = ?, id_following_action = ?, id_contact_attribute = ?, default_subject = ? WHERE id_task = ? ";
    //language=MySQL
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_ticketing_email_external_user_config WHERE id_task = ? ";

    @Override
    public void insert( TaskNotifyWaitingTicketConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowTicketingPlugin.getPlugin( ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getIdTask( ) );
        daoUtil.setInt( nIndex++, config.getMessageDirectionExternalUser( ).ordinal( ) );

        if ( config.getIdFollowingAction( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        }
        else
        {
            daoUtil.setInt( nIndex++, config.getIdFollowingAction( ) );
        }

        if ( config.getIdContactAttribute( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        }
        else
        {
            daoUtil.setInt( nIndex++, config.getIdContactAttribute( ) );
        }

        if ( config.getDefaultSubject( ) == null )
        {
            daoUtil.setString( nIndex, StringUtils.EMPTY );
        }
        else
        {
            daoUtil.setString( nIndex, config.getDefaultSubject( ) );
        }

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public void store( TaskNotifyWaitingTicketConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowTicketingPlugin.getPlugin( ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getMessageDirectionExternalUser( ).ordinal( ) );

        if ( config.getIdFollowingAction( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        }
        else
        {
            daoUtil.setInt( nIndex++, config.getIdFollowingAction( ) );
        }

        if ( config.getIdContactAttribute( ) == null )
        {
            daoUtil.setIntNull( nIndex++ );
        }
        else
        {
            daoUtil.setInt( nIndex++, config.getIdContactAttribute( ) );
        }

        if ( config.getDefaultSubject( ) == null )
        {
            daoUtil.setString( nIndex++, StringUtils.EMPTY );
        }
        else
        {
            daoUtil.setString( nIndex++, config.getDefaultSubject( ) );
        }
        daoUtil.setInt( nIndex, config.getIdTask( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public TaskNotifyWaitingTicketConfig load( int nIdTask )
    {
        TaskNotifyWaitingTicketConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowTicketingPlugin.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery( );

        int nIndex = 1;

        if ( daoUtil.next( ) )
        {
            config = new TaskNotifyWaitingTicketConfig( );
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

            config.setDefaultSubject( daoUtil.getString( nIndex ) );
        }

        daoUtil.free( );

        return config;
    }

    @Override
    public void delete( int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowTicketingPlugin.getPlugin( ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
