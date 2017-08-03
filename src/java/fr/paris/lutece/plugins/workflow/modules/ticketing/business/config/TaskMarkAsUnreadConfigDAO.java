package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskMarkAsUnreadConfigDAO implements ITaskConfigDAO<TaskMarkAsUnreadConfig>
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, id_marking FROM workflow_task_ticketing_mark_unread_config "
            + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_ticketing_mark_unread_config ( id_task, id_marking ) " + " VALUES ( ?,? ) ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_ticketing_mark_unread_config SET id_marking = ? " + " WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM workflow_task_ticketing_mark_unread_config WHERE id_task = ? ";

    private static final String SQL_QUERY_FIND_BY_MARKING_ID = " SELECT id_task FROM workflow_task_ticketing_mark_unread_config " + " WHERE id_marking = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskMarkAsUnreadConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getIdTask( ) );
        daoUtil.setInt( nIndex++, config.getIdMarking( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskMarkAsUnreadConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getIdMarking( ) );

        daoUtil.setInt( nIndex++, config.getIdTask( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskMarkAsUnreadConfig load( int nIdTask )
    {
        TaskMarkAsUnreadConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery( );

        int nIndex = 1;

        if ( daoUtil.next( ) )
        {
            config = new TaskMarkAsUnreadConfig( );
            config.setIdTask( daoUtil.getInt( nIndex++ ) );
            config.setIdMarking( daoUtil.getInt( nIndex++ ) );
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
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    public List<Integer> loadTicketIdListByMarkingId( int nIdMarking, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_MARKING_ID, plugin );
        daoUtil.setInt( 1, nIdMarking );
        daoUtil.executeQuery( );

        List<Integer> listIdTask = new ArrayList<Integer>( );

        while ( daoUtil.next( ) )
        {
            listIdTask.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listIdTask;
    }
}
