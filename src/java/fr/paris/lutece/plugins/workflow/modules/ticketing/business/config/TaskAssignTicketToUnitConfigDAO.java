package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskAssignTicketToUnitConfigDAO implements ITaskConfigDAO<TaskAssignTicketToUnitConfig>
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT id_task, level_1, level_2, level_3 FROM workflow_task_ticketing_assign_unit_config " + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT              = " INSERT INTO workflow_task_ticketing_assign_unit_config ( id_task, level_1, level_2, level_3 ) " + " VALUES ( ?,?,?,? ) ";
    private static final String SQL_QUERY_UPDATE              = " UPDATE workflow_task_ticketing_assign_unit_config SET level_1 = ?, level_2 = ?, level_3 = ?  " + " WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE              = " DELETE FROM workflow_task_ticketing_assign_unit_config WHERE id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskAssignTicketToUnitConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getIdTask( ) );
        daoUtil.setBoolean( nIndex++, config.isLevel1( ) );
        daoUtil.setBoolean( nIndex++, config.isLevel2( ) );
        daoUtil.setBoolean( nIndex, config.isLevel3( ) );

        daoUtil.executeUpdate( );

        daoUtil.free( );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskAssignTicketToUnitConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        int nIndex = 1;

        daoUtil.setBoolean( nIndex++, config.isLevel1( ) );
        daoUtil.setBoolean( nIndex++, config.isLevel2( ) );
        daoUtil.setBoolean( nIndex++, config.isLevel3( ) );

        daoUtil.setInt( nIndex, config.getIdTask( ) );
        daoUtil.executeUpdate( );

        daoUtil.free( );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskAssignTicketToUnitConfig load( int nIdTask )
    {
        TaskAssignTicketToUnitConfig config = null;
        DAOUtil daoUtil = null;

        daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, PluginService.getPlugin( WorkflowTicketingPlugin.PLUGIN_NAME ) );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery( );

        int nIndex = 1;

        if ( daoUtil.next( ) )
        {
            config = new TaskAssignTicketToUnitConfig( );
            config.setIdTask( daoUtil.getInt( nIndex++ ) );
            config.setLevel1( daoUtil.getBoolean( nIndex++ ) );
            config.setLevel2( daoUtil.getBoolean( nIndex++ ) );
            config.setLevel3( daoUtil.getBoolean( nIndex ) );
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

}
