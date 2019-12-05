package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;
import org.apache.commons.lang.StringUtils;

public class TaskNotifyWaitingTicketConfigDAO implements ITaskConfigDAO<TaskNotifyWaitingTicketConfig>
{


    //language=MySQL
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, sender_name, subject, message FROM workflow_task_ticketing_notify_waiting_ticket_config WHERE id_task = ? ";
    //language=MySQL
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_notify_waiting_ticket_config ( id_task, sender_name, subject, message ) VALUES ( ?,?,?,? ) ";
    //language=MySQL
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_ticketing_notify_waiting_ticket_config SET sender_name = ?, subject = ?, message = ? WHERE id_task = ? ";
    //language=MySQL
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_ticketing_notify_waiting_ticket_config WHERE id_task = ? ";

    @Override
    public void insert( TaskNotifyWaitingTicketConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowTicketingPlugin.getPlugin( ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getIdTask( ) );
        daoUtil.setString( nIndex++, config.getSenderName( )==null?StringUtils.EMPTY:config.getSenderName( ) );
        daoUtil.setString( nIndex++, config.getSubject( )==null?StringUtils.EMPTY:config.getSubject( ) );
        daoUtil.setString( nIndex, config.getMessage( )==null?StringUtils.EMPTY:config.getMessage( ) );


        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public void store( TaskNotifyWaitingTicketConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowTicketingPlugin.getPlugin( ) );

        int nIndex = 1;

        daoUtil.setString( nIndex++, config.getSenderName( )==null?StringUtils.EMPTY:config.getSenderName( ) );
        daoUtil.setString( nIndex++, config.getSubject( )==null?StringUtils.EMPTY:config.getSubject( ) );
        daoUtil.setString( nIndex++, config.getMessage( )==null?StringUtils.EMPTY:config.getMessage( ) );

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
            config.setSenderName( ( daoUtil.getString( nIndex++ ) ) );
            config.setSubject( ( daoUtil.getString( nIndex++ ) ) );
            config.setMessage( ( daoUtil.getString( nIndex ) ) );
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
