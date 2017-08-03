package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class TaskMarkAsUnreadConfig extends TaskConfig 
{
    @NotNull
    @Min( 1 )
    private int _nIdMarking;
    
    /**
     * Set the id of the marking
     * 
     * @param nIdMarking
     *            the id of the marking
     */
    public void setIdMarking( int nIdMarking )
    {
    	_nIdMarking = nIdMarking;
    }

    /**
     * Get the id of the marking
     * 
     * @return the id of the marking
     */
    public int getIdMarking( )
    {
        return _nIdMarking;
    }
}
