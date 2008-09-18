package org.codehaus.plexus.javadoc;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Abstract <code>Taglet</code> for <a href="http://plexus.codehaus.org/"/>Plexus</a> tags.
 *
 * @see <a href="http://plexus.codehaus.org/guides/developer-guide/appendices/javadoc-tags-reference.html">
 * http://plexus.codehaus.org/guides/developer-guide/appendices/javadoc-tags-reference.html</a>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public abstract class AbstractPlexusTaglet
    implements Taglet
{
    /** {@inheritDoc} */
    public String toString( Tag tag )
    {
        if ( tag == null )
        {
            return null;
        }

        String tagText = tag.text();
        MutableAttributeSet att = getAttributes( tagText );

        StringBuffer sb = new StringBuffer();
        if ( ( att == null ) || ( att.getAttributeCount() == 0 ) )
        {
            sb.append( "<DT><B>" ).append( getHeader() ).append( "</B></DT><DD></DD>" );
        }
        else
        {
            sb.append( "<DT><B>" ).append( getHeader() ).append( ":</B></DT>" );
            sb.append( "<DD><TABLE CELLPADDING=\"2\" CELLSPACING=\"0\"><TR><TD>" );

            appendPlexusTag( sb, att );

            sb.append( "</TD></TR></TABLE></DD>" );
        }

        return sb.toString();
    }

    /** {@inheritDoc} */
    public String toString( Tag[] tags )
    {
        if ( tags.length == 0 )
        {
            return null;
        }

        boolean hasParameters = false;
        for ( int i = 0; i < tags.length; i++ )
        {
            String tagText = tags[i].text();
            MutableAttributeSet att = getAttributes( tagText );
            if ( att != null )
            {
                hasParameters = att.getAttributeCount() > 0;
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append( "<DT><B>" ).append( getHeader() ).append( ( hasParameters ? ":" : "" ) ).append( "</B></DT>" );
        sb.append( "<DD><TABLE CELLPADDING=\"2\" CELLSPACING=\"0\"><TR><TD>" );
        for ( int i = 0; i < tags.length; i++ )
        {
            if ( i > 0 )
            {
                sb.append( ", " );
            }

            String tagText = tags[i].text();
            appendPlexusTag( sb, getAttributes( tagText ) );
        }

        sb.append( "</TD></TR></TABLE></DD>" );

        return sb.toString();
    }

    /**
     * @return the header to display
     */
    public abstract String getHeader();

    /**
     * @param text the Tag text returned by {@link Tag#text()}
     * @return a MutableAttributeSet or null if text was null
     */
    private MutableAttributeSet getAttributes( String text )
    {
        if ( text == null || text.trim().length() == 0 )
        {
            return null;
        }

        MutableAttributeSet att = new SimpleAttributeSet();

        StringTokenizer token = new StringTokenizer( text, " " );
        while ( token.hasMoreTokens() )
        {
            String nextToken = token.nextToken();

            StringTokenizer token2 = new StringTokenizer( nextToken, "=" );
            if ( token2.countTokens() != 2 )
            {
                System.err.println( "The annotation '" + getName() + "' has a wrong Plexus annotations: " + text );
                continue;
            }

            String name = token2.nextToken();
            String value = token2.nextToken();

            att.addAttribute( name, value );
        }

        return att;
    }

    /**
     * Append the wanted display in the javadoc.
     *
     * @param sb
     * @param att
     */
    private static void appendPlexusTag( StringBuffer sb, MutableAttributeSet att )
    {
        if ( att == null )
        {
            return;
        }

        if ( att.getAttributeCount() > 0 )
        {
            sb.append( "<DL>" );

            Enumeration names = att.getAttributeNames();

            while ( names.hasMoreElements() )
            {
                Object key = names.nextElement();
                Object value = att.getAttribute( key );

                if ( value instanceof AttributeSet )
                {
                    // ignored
                }
                else
                {
                    sb.append( "<DT><B>" ).append( key ).append( ":</B></DT>" );
                    sb.append( "<DD>" ).append( value ).append( "</DD>" );
                }
            }

            sb.append( "</DL>" );
        }
    }
}
