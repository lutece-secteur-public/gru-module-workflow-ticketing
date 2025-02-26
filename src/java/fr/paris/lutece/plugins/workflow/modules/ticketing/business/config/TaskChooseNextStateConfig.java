/*
 * Copyright (c) 2002-2025, City of Paris
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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class TaskChooseNextStateConfig extends TaskConfig
{

    @NotNull
    @Min( 1 )
    private int _nIdStateOK;

    @NotNull
    @Min( 1 )
    private int _nIdStateKO;

    private String _controllerName = "";

    /**
     * @return the controllerName
     */
    public String getControllerName( )
    {
        return _controllerName;
    }

    /**
     * @param controllerName
     *            the controllerName to set
     */
    public void setControllerName( String controllerName )
    {
        _controllerName = controllerName;
    }

    /**
     * @return the _nIdStateOK
     */
    public int getIdStateOK( )
    {
        return _nIdStateOK;
    }

    /**
     * @param idStateKO
     *            the _nIdStateOK to set
     */
    public void setIdStateOK( int idStateOK )
    {
        _nIdStateOK = idStateOK;
    }

    /**
     * @return the _nIdStateKO
     */
    public int getIdStateKO( )
    {
        return _nIdStateKO;
    }

    /**
     * @param idStateKO
     *            the _nIdStateKO to set
     */
    public void setIdStateKO( int idStateKO )
    {
        _nIdStateKO = idStateKO;
    }
}
