/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser;

import org.junit.Assert;
import org.junit.Test;

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.ExternalUser;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.externaluser.ExternalUserComparator;
import junit.framework.TestCase;

/**
 * test FieldAgentUserComparator
 */
public class ExternalUserComparatorTest extends TestCase
{
    /**
     * one to one test
     */
    @Test
    public void testOneToOneComparator( )
    {
        ExternalUserComparator comp = new ExternalUserComparator( );
        ExternalUser user1 = createExternalUser( "3z", "z", "z", "z.z@z.com" );
        ExternalUser user2 = createExternalUser( "ppp", "z", "z", "z.z@z.com" );
        ExternalUser user3 = createExternalUser( "z0z", "z", "z", "z.z@z.com" );
        ExternalUser user4 = createExternalUser( "z01", "z", "z", "z.z@z.com" );
        ExternalUser user5 = createExternalUser( "z01", "a", "z", "z.z@z.com" );
        ExternalUser user6 = createExternalUser( "z01", "z", "a", "z.z@z.com" );
        ExternalUser user7 = createExternalUser( "z01", "z", "z", "a.z@z.com" );
        ExternalUser user8 = createExternalUser( "a0z", "z", "z", "z.z@z.com" );
        ExternalUser user9 = createExternalUser( "a10z", "z", "z", "z.z@z.com" );
        ExternalUser user10 = createExternalUser( "a9z", "z", "z", "z.z@z.com" );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user1 ) + " / " + fieldAgentUserToString( user2 ) + " / " + comp.compare( user1, user2 ) + ">0",
                comp.compare( user1, user2 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user1 ) + " / " + fieldAgentUserToString( user3 ) + " / " + comp.compare( user1, user3 ) + ">0",
                comp.compare( user1, user3 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user4 ) + " / " + fieldAgentUserToString( user3 ) + " / " + comp.compare( user4, user3 ) + ">0",
                comp.compare( user4, user3 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user4 ) + " / " + fieldAgentUserToString( user5 ) + " / " + comp.compare( user4, user5 ) + ">0",
                comp.compare( user4, user5 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user4 ) + " / " + fieldAgentUserToString( user6 ) + " / " + comp.compare( user4, user6 ) + ">0",
                comp.compare( user4, user6 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user4 ) + " / " + fieldAgentUserToString( user7 ) + " / " + comp.compare( user4, user7 ) + ">0",
                comp.compare( user4, user7 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user3 ) + " / " + fieldAgentUserToString( user8 ) + " / " + comp.compare( user3, user8 ) + ">0",
                comp.compare( user3, user8 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user9 ) + " / " + fieldAgentUserToString( user8 ) + " / " + comp.compare( user9, user8 ) + ">0",
                comp.compare( user9, user8 ) > 0 );
        Assert.assertTrue(
                "erreur comparaison " + fieldAgentUserToString( user9 ) + " / " + fieldAgentUserToString( user10 ) + " / " + comp.compare( user9, user10 )
                        + ">0", comp.compare( user9, user10 ) > 0 );
    }

    /**
     * create ExternalUser
     * 
     * @param strEntite
     *            , entite
     * @param strLastname
     *            , lastName
     * @param strFirstname
     *            , firstName
     * @param strEmail
     *            , email
     * @return created ExternalUser
     */
    private ExternalUser createExternalUser( String strEntite, String strLastname, String strFirstname, String strEmail )
    {
        ExternalUser externalUser = new ExternalUser( );
        externalUser.setEntite( strEntite );
        externalUser.setLastname( strLastname );
        externalUser.setFirstname( strFirstname );
        externalUser.setEmail( strEmail );

        return externalUser;
    }

    /**
     *
     * @param externalUser
     *            ExternalUser
     * @return agentUser to String
     */
    private String fieldAgentUserToString( ExternalUser externalUser )
    {
        StringBuilder strLog = new StringBuilder( );
        strLog.append( externalUser.getEntite( ) );
        strLog.append( ";" );
        strLog.append( externalUser.getLastname( ) );
        strLog.append( ";" );
        strLog.append( externalUser.getFirstname( ) );
        strLog.append( ";" );
        strLog.append( externalUser.getEmail( ) );

        return strLog.toString( );
    }
}
