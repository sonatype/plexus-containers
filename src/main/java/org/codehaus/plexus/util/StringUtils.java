/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */
package org.codehaus.plexus.util;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class StringUtils
{
    public static String[] split( String text, String separator )
    {
        return split( text, separator, -1 );
    }

    public static String[] split( String str, String separator, int max )
    {
        StringTokenizer tok = null;
        if ( separator == null )
        {
            // Null separator means we're using StringTokenizer's default
            // delimiter, which comprises all whitespace characters.
            tok = new StringTokenizer( str );
        }
        else
        {
            tok = new StringTokenizer( str, separator );
        }

        int listSize = tok.countTokens();
        if ( max > 0 && listSize > max )
        {
            listSize = max;
        }

        String[] list = new String[listSize];
        int i = 0;
        int lastTokenBegin = 0;
        int lastTokenEnd = 0;
        while ( tok.hasMoreTokens() )
        {
            if ( max > 0 && i == listSize - 1 )
            {
                // In the situation where we hit the max yet have
                // tokens left over in our input, the last list
                // element gets all remaining text.
                String endToken = tok.nextToken();
                lastTokenBegin = str.indexOf( endToken, lastTokenEnd );
                list[i] = str.substring( lastTokenBegin );
                break;
            }
            else
            {
                list[i] = tok.nextToken();
                lastTokenBegin = str.indexOf( list[i], lastTokenEnd );
                lastTokenEnd = lastTokenBegin + list[i].length();
            }
            i++;
        }
        return list;
    }

    public static String replace( String text, String repl, String with )
    {
        return replace( text, repl, with, -1 );
    }

    public static String replace( String text, String repl, String with, int max )
    {
        if ( text == null || repl == null || with == null || repl.length() == 0 )
        {
            return text;
        }

        StringBuffer buf = new StringBuffer( text.length() );
        int start = 0, end = 0;
        while ( ( end = text.indexOf( repl, start ) ) != -1 )
        {
            buf.append( text.substring( start, end ) ).append( with );
            start = end + repl.length();

            if ( --max == 0 )
            {
                break;
            }
        }
        buf.append( text.substring( start ) );
        return buf.toString();
    }

    public static String chopNewline( String str )
    {
        int lastIdx = str.length() - 1;
        char last = str.charAt( lastIdx );
        if ( last == '\n' )
        {
            if ( str.charAt( lastIdx - 1 ) == '\r' )
            {
                lastIdx--;
            }
        }
        else
        {
            lastIdx++;
        }
        return str.substring( 0, lastIdx );
    }

    public static String interpolate( String text, Map namespace )
    {
        Iterator keys = namespace.keySet().iterator();

        while ( keys.hasNext() )
        {
            String key = keys.next().toString();

            Object obj = namespace.get( key );

            String value = obj.toString();

            text = StringUtils.replace( text, "${" + key + "}", value );

            if ( key.indexOf( " " ) == -1 )
            {
                text = StringUtils.replace( text, "$" + key, value );
            }
        }
        return text;
    }

    public static String removeAndHump( String data, String replaceThis )
    {
        String temp = null;

        StringBuffer out = new StringBuffer();

        temp = data;

        StringTokenizer st = new StringTokenizer( temp, replaceThis );

        while ( st.hasMoreTokens() )
        {
            String element = (String) st.nextElement();

            out.append( capitalizeFirstLetter( element ) );
        }

        return out.toString();
    }

    public static String capitalizeFirstLetter( String data )
    {
        String firstLetter = data.substring( 0, 1 ).toUpperCase();

        String restLetters = data.substring( 1 );

        return firstLetter + restLetters;
    }

    public static String lowercaseFirstLetter( String data )
    {
        String firstLetter = data.substring( 0, 1 ).toLowerCase();

        String restLetters = data.substring( 1 );

        return firstLetter + restLetters;
    }

    public static String addAndDeHump( String view )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < view.length(); i++ )
        {
            if ( i != 0 && Character.isUpperCase( view.charAt( i ) ) )
            {
                sb.append( '-' );
            }

            sb.append( view.charAt( i ) );
        }

        return sb.toString().trim().toLowerCase();
    }
}
