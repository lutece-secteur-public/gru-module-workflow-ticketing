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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import fr.paris.lutece.plugins.ticketing.business.assignmentparisfamille.AssignmentParisFamille;
import fr.paris.lutece.plugins.ticketing.business.assignmentparisfamille.AssignmentParisFamilleHome;
import fr.paris.lutece.plugins.ticketing.business.assignmentparisfamille.IAssignmentParisFamilleDAO;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.plugin.PluginService;

/**
 *
 * AutomaticAssignmentAgentParisFamilleService
 *
 */
public class AutomaticAssignmentAgentParisFamilleService implements IAutomaticAssignmentAgentParisFamilleService
{
    public static final String         BEAN_NAME = "workflow-ticketing.automaticAssignmentAgentParisFamilleService";

    private static final String        GUID_AGENT_DEFAUT_PARIS_FAMILLE = "ticketing.configuration.agent.paris.famille.defaut";

    @Inject
    private IAssignmentParisFamilleDAO _dao;

    /**
     * constructor
     */
    public AutomaticAssignmentAgentParisFamilleService( )
    {
        super( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminUser getAssignedParisFamilleAgent( String lastName )
    {

        List<AssignmentParisFamille> assignmentsList = _dao.selectAssignmentParisFamillesList( PluginService.getPlugin( "ticketing" ) );
        List<AssignmentParisFamille> assignmentsCompatiblesList = new ArrayList<>( );
        AdminUser agentParisFamille = null;
        AssignmentParisFamille assignment = null;
        String guidAgent = null;
        for ( AssignmentParisFamille assignmentParisFamille : assignmentsList )
        {
            if ( ( assignmentParisFamille.getAssignmentRangeStart( ).compareToIgnoreCase( lastName ) <= 0 ) && ( assignmentParisFamille.getAssignmentRangeEnd( ).compareToIgnoreCase( lastName ) > 0 ) )
            {
                assignmentsCompatiblesList.add( assignmentParisFamille );
            }
        }
        // assignation des 2 plages vides comme defaut si le nom usager ne correspond à aucun intervalle de lettres
        if ( assignmentsCompatiblesList.isEmpty( ) )
        {
            Optional<AssignmentParisFamille> assignmentOptional = assignmentsList.stream( ).filter( a -> a.getAssignmentRangeStart( ).equals( "" ) )
                    .filter( a -> a.getAssignmentRangeEnd( ).equals( "" ) )
                    .findFirst( );

            if ( assignmentOptional.isPresent( ) )
            {
                assignment = assignmentOptional.get( );
            }
        } else
        {
            // assignation trouvee dans un intervalle de lettres
            assignment = AssignmentParisFamilleHome.findByPrimaryKey( assignmentsCompatiblesList.get( 0 ).getId( ) );
        }
        if ( ( null != assignment ) && !assignment.getGuid( ).isEmpty( ) )
        {
            guidAgent = assignment.getGuid( );
        } else
        {
            // agent par defaut en cas de suppression assignation des 2 plages vides
            guidAgent = DatastoreService.getDataValue( GUID_AGENT_DEFAUT_PARIS_FAMILLE, "C3CAA162EC6B11E6A6EDF5019677183C00000000" );
        }

        agentParisFamille = AdminUserHome.findUserByLogin( guidAgent );

        return agentParisFamille;
    }

}
