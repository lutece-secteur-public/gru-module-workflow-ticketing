package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

/**
 * The Class TaskAssignTicketToUnitConfig.
 */
public class TaskAssignTicketToUnitConfig extends TaskConfig
{

    private boolean _bLevel1;
    private boolean _bLevel2;
    private boolean _bLevel3;

    /**
     * Set level1.
     *
     * @param bLevel1            the id of the level
     */
    public void setLevel1( boolean bLevel1 )
    {
        _bLevel1 = bLevel1;
    }

    /**
     * Get level1.
     *
     * @return true if level1
     */
    public boolean isLevel1( )
    {
        return _bLevel1;
    }

    /**
     * Set level2.
     *
     * @param bLevel2 the new level 2
     */
    public void setLevel2( boolean bLevel2 )
    {
        _bLevel2 = bLevel2;
    }

    /**
     * Get level2.
     *
     * @return true if level2
     */
    public boolean isLevel2( )
    {
        return _bLevel2;
    }

    /**
     * Set level3.
     *
     * @param bLevel3 the new level 3
     */
    public void setLevel3( boolean bLevel3 )
    {
        _bLevel3 = bLevel3;
    }

    /**
     * Get level3.
     *
     * @return true if level3
     */
    public boolean isLevel3( )
    {
        return _bLevel3;
    }

    /**
     * Gets the level list.
     *
     * @return the level list
     */
    public List<Integer> getLevelList( )
    {
        List<Integer> levelList = new ArrayList<>( );
        if ( _bLevel1 )
        {
            levelList.add( 1 );
        }
        if ( _bLevel2 )
        {
            levelList.add( 2 );
        }
        if ( _bLevel3 )
        {
            levelList.add( 3 );
        }

        return levelList;
    }
}
