package org.codehaus.plexus.configuration.processor;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;

import java.util.Map;
import java.util.HashMap;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Take a normal PlexusConfiguration and look for directives within it
 * that allow the inlining of external configuration sources.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ConfigurationProcessor
{
    protected Map handlers;

    public ConfigurationProcessor()
    {
        handlers = new HashMap();
    }

    protected void addConfigurationResourceHandler( ConfigurationResourceHandler handler )
    {
        handlers.put( handler.getId(), handler );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public PlexusConfiguration process( PlexusConfiguration configuration, Map variables )
        throws ConfigurationResourceNotFoundException, ConfigurationProcessingException
    {
        XmlPlexusConfiguration pc = new XmlPlexusConfiguration( "configuration" );

        PlexusConfiguration[] children = configuration.getChildren();

        for ( int i = 0; i < children.length; i++ )
        {
            PlexusConfiguration child = children[i];

            String elementName = child.getName();

            // ----------------------------------------------------------------------
            // Check to see if this element name matches the id of any of our
            // configuration resource handlers.
            // ----------------------------------------------------------------------

            if ( handlers.containsKey( elementName ) )
            {
                ConfigurationResourceHandler handler = (ConfigurationResourceHandler) handlers.get( elementName );

                PlexusConfiguration[] configurations = handler.handleRequest( createHandlerParameters( child, variables ) );

                for ( int j = 0; j < configurations.length; j++ )
                {
                    pc.addChild( configurations[j] );
                }
            }
            else
            {
                pc.addChild( child );
            }
        }

        return pc;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected Map createHandlerParameters( PlexusConfiguration c, Map variables )
    {
        Map parameters = new HashMap();

        String[] parameterNames = c.getAttributeNames();

        for ( int i = 0; i < parameterNames.length; i++ )
        {
            String key = parameterNames[i];

            String value = StringUtils.interpolate( c.getAttribute( key, null ), variables );

            parameters.put( key, value );
        }

        return parameters;
    }

}
