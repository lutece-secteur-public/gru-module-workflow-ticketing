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

import org.apache.commons.lang.StringUtils;

import java.util.Comparator;

/**
 * Comparator for ExternalUser
 */
public class ExternalUserComparator implements Comparator<ExternalUser>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare( ExternalUser o1, ExternalUser o2 )
    {
        int nCompare = 0;
        nCompare = compareAlphaNumeric( o1.getAdditionalAttribute( ), o2.getAdditionalAttribute( ), nCompare );
        nCompare = compareAlphaNumeric( o1.getLastname( ), o2.getLastname( ), nCompare );
        nCompare = compareAlphaNumeric( o1.getFirstname( ), o2.getFirstname( ), nCompare );
        nCompare = compareAlphaNumeric( o1.getEmail( ), o2.getEmail( ), nCompare );

        return nCompare;
    }

    /**
     * return true if the character is numeric
     * 
     * @param cDigit
     *            a character
     * @return true if the character is numeric
     */
    private boolean isDigit( char cDigit )
    {
        return ( cDigit >= 48 ) && ( cDigit <= 57 );
    }

    /**
     * Length of string is passed in for improved efficiency (only need to calculate it once)
     * 
     * @param strCompare
     *            original string
     * @param nStrLength
     *            size of the given string
     * @param nCurrentMarker
     *            current position in the given String
     * @return substring to compare
     */
    private String getChunk( String strCompare, int nStrLength, int nCurrentMarker )
    {
        StringBuilder strChunk = new StringBuilder( );
        int nMarker = nCurrentMarker;
        char c = strCompare.charAt( nMarker );
        strChunk.append( c );
        nMarker++;

        if ( isDigit( c ) )
        {
            while ( nMarker < nStrLength )
            {
                c = strCompare.charAt( nMarker );

                if ( !isDigit( c ) )
                {
                    break;
                }

                strChunk.append( c );
                nMarker++;
            }
        }
        else
        {
            while ( nMarker < nStrLength )
            {
                c = strCompare.charAt( nMarker );

                if ( isDigit( c ) )
                {
                    break;
                }

                strChunk.append( c );
                nMarker++;
            }
        }

        return strChunk.toString( );
    }

    /**
     * compare with natural order two string if nCurrentCompare is equal to 0 null/empty string have to be at 'the bottom' of the list
     * 
     * @param str1
     *            , first string to compare
     * @param str2
     *            , second string to compare
     * @param nCurrentCompare
     *            , current compare value
     * @return comparison value
     */
    private int compareAlphaNumeric( String str1, String str2, int nCurrentCompare )
    {
        if ( nCurrentCompare != 0 )
        {
            return nCurrentCompare;
        }

        int nCompare = 0;

        if ( StringUtils.isEmpty( str1 ) )
        {
            if ( StringUtils.isEmpty( str2 ) )
            {
                return 0;
            }
            else
            {
                nCompare = 1;
            }
        }
        else
            if ( StringUtils.isEmpty( str2 ) )
            {
                nCompare = -1;
            }

        if ( nCompare != 0 )
        {
            return nCompare;
        }

        int nMarker1 = 0;
        int nMarker2 = 0;
        int nLength1 = str1.length( );
        int nLength2 = str2.length( );

        while ( ( nMarker1 < nLength1 ) && ( nMarker2 < nLength2 ) )
        {
            String strChunk1 = getChunk( str1, nLength1, nMarker1 );
            nMarker1 += strChunk1.length( );

            String strChunk2 = getChunk( str2, nLength2, nMarker2 );
            nMarker2 += strChunk2.length( );

            // If both chunks contain numeric characters, sort them numerically
            if ( isDigit( strChunk1.charAt( 0 ) ) && isDigit( strChunk2.charAt( 0 ) ) )
            {
                // Simple chunk comparison by length.
                int thisChunkLength = strChunk1.length( );
                nCompare = thisChunkLength - strChunk2.length( );

                // If equal, the first different number counts
                if ( nCompare == 0 )
                {
                    for ( int i = 0; i < thisChunkLength; i++ )
                    {
                        nCompare = strChunk1.charAt( i ) - strChunk2.charAt( i );

                        if ( nCompare != 0 )
                        {
                            break;
                        }
                    }
                }
            }
            else
                if ( isDigit( strChunk1.charAt( 0 ) ) )
                {
                    // in this case strChunk2 is not numeric
                    nCompare = 1;
                }
                else
                    if ( isDigit( strChunk1.charAt( 0 ) ) )
                    {
                        // in this case strChunk1 is not numeric
                        nCompare = -1;
                    }
                    else
                    {
                        // in this case both are string
                        nCompare = strChunk1.compareTo( strChunk2 );
                    }

            if ( nCompare != 0 )
            {
                return nCompare;
            }
        }

        return nLength1 - nLength2;
    }
}
