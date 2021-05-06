package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class TaskAssignTicketToUnitConfig extends TaskConfig
{
    @NotNull
    @Min( 1 )
    private int _nIdLevel;

    /**
     * Set the id of the level
     *
     * @param nIdLevel
     *            the id of the level
     */
    public void setIdLevel( int nIdLevel )
    {
        _nIdLevel = nIdLevel;
    }

    /**
     * Get the id of the level
     *
     * @return the id of the level
     */
    public int getIdLevel( )
    {
        return _nIdLevel;
    }
}
