/*
 * Copyright (c) 2002-2024, City of Paris
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
     * @param bLevel1
     *            the id of the level
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
     * @param bLevel2
     *            the new level 2
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
     * @param bLevel3
     *            the new level 3
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
