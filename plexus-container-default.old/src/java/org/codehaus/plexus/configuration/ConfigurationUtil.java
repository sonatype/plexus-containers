/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.codehaus.plexus.configuration;

import java.util.ArrayList;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * General utility supporting static operations for generating string
 * representations of a configuration suitable for debugging.
 * @author Stephen McConnell <mcconnell@osm.net>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class ConfigurationUtil
{
    /**
     * Returns a simple string representation of the the supplied configuration.
     * @param config a configuration
     * @return a simplified text representation of a configuration suitable
     *     for debugging
     */
    public static String list( Configuration config )
    {
        final StringBuffer buffer = new StringBuffer();
        list( buffer, "  ", config );
        buffer.append( "\n" );
        return buffer.toString();
    }

    private static void list( StringBuffer buffer, String lead, Configuration config )
    {

        buffer.append( "\n" + lead + "<" + config.getName() );
        String[] names = config.getAttributeNames();
        if( names.length > 0 )
        {
            for( int i = 0; i < names.length; i++ )
            {
                buffer.append( " "
                               + names[ i ] + "=\""
                               + config.getAttribute( names[ i ], "???" ) + "\"" );
            }
        }
        Configuration[] children = config.getChildren();
        if( children.length > 0 )
        {
            buffer.append( ">" );
            for( int j = 0; j < children.length; j++ )
            {
                list( buffer, lead + "  ", children[ j ] );
            }
            buffer.append( "\n" + lead + "</" + config.getName() + ">" );
        }
        else
        {
            if( config.getValue( null ) != null )
            {
                buffer.append( ">" + config.getValue( "" ) + "</" + config.getName() + ">" );
            }
            else
            {
                buffer.append( "/>" );
            }
        }
    }

    /**
     * Return all occurance of a configuration child containing the supplied attribute name.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter (null will match any attribute name)
     * @return an array of configuration instances matching the query
     */
    public static Configuration[] match( final Configuration config,
                                         final String element,
                                         final String attribute )
    {
        return match( config, element, attribute, null );
    }

    /**
     * Return occurance of a configuration child containing the supplied attribute name and value.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter (null will match any attribute name )
     * @param value the attribute value to match (null will match any attribute value)
     * @return an array of configuration instances matching the query
     */
    public static Configuration[] match( final Configuration config,
                                         final String element,
                                         final String attribute,
                                         final String value )
    {
        final ArrayList list = new ArrayList();
        final Configuration[] children = config.getChildren( element );

        for( int i = 0; i < children.length; i++ )
        {
            if( null == attribute )
            {
                list.add( children[ i ] );
            }
            else
            {
                String v = children[ i ].getAttribute( attribute, null );

                if( v != null )
                {
                    if( ( value == null ) || v.equals( value ) )
                    {
                        // it's a match
                        list.add( children[ i ] );
                    }
                }
            }
        }

        return (Configuration[])list.toArray( new Configuration[ list.size() ] );
    }

    /**
     * Return the first occurance of a configuration child containing the supplied attribute name
     * and value or create a new empty configuration if no match found.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter
     * @param value the attribute value to match (null will match any attribute value)
     * @return a configuration instances matching the query or empty configuration
     */
    public static Configuration matchFirstOccurance(
        Configuration config, String element, String attribute, String value )
    {
        return matchFirstOccurance( config, element, attribute, value, true );
    }

    /**
     * Return the first occurance of a configuration child containing the supplied attribute
     * name and value.  If the supplied creation policy if TRUE and no match is found, an
     * empty configuration manager is returned, otherwise a null will returned.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter
     * @param value the attribute value to match (null will match any attribute value)
     * @param create the creation policy if no match
     * @return a configuration instances matching the query
     */
    public static Configuration matchFirstOccurance(
        Configuration config, String element, String attribute, String value, boolean create )
    {
        Configuration[] children = config.getChildren( element );
        for( int i = 0; i < children.length; i++ )
        {
            String v = children[ i ].getAttribute( attribute, null );
            if( v != null )
            {
                if( ( value == null ) || v.equals( value ) )
                {
                    // it's a match
                    return children[ i ];
                }
            }
        }

        return create ? new DefaultConfiguration( element, null ) : null;
    }
}
