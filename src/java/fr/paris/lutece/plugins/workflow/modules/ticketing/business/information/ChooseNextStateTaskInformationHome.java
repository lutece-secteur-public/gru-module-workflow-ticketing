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
