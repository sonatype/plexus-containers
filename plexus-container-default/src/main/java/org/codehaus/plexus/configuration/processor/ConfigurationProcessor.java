package org.codehaus.plexus.configuration.processor;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Take a normal PlexusConfiguration and look for directives within it
 * that allow the inlining of external configuration sources.
 *
 * @todo could this be amalgamated with the expression handling in the component configurator? It cannot be used here,
 *       as it requires actual objects to be returned, which cannot be stored back into a configuration object.
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

    public void addConfigurationResourceHandler( ConfigurationResourceHandler handler )
    {
        handlers.put( handler.getId(), handler );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public PlexusConfiguration process( PlexusConfiguration configuration, Map variables )
        throws ConfigurationResourceNotFoundException, ConfigurationProcessingException
    {
        XmlPlexusConfiguration processed = new XmlPlexusConfiguration( "configuration" );

        walk( configuration, processed, variables );

        return processed;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void walk( PlexusConfiguration source, PlexusConfiguration processed, Map variables )
        throws ConfigurationResourceNotFoundException, ConfigurationProcessingException
    {
        PlexusConfiguration[] children = source.getChildren();

        for ( int i = 0; i < children.length; i++ )
        {
            PlexusConfiguration child = children[i];

            int count = child.getChildCount();

            if ( count > 0 )
            {
                // ----------------------------------------------------------------------
                // If we have a child with children itself then we must make a configuration
                // with the name of the child, add that child to the processed configuration
                // and walk the child.
                //
                // <configuration>
                //   <child>
                //     <entity>
                //       <foo>bar</foo>
                //     </entity>
                //   </child>
                // </configuration>
                //
                // ----------------------------------------------------------------------

                XmlPlexusConfiguration processedChild = new XmlPlexusConfiguration( child.getName() );

                copyAttributes( child, processedChild );

                processed.addChild( processedChild );

                walk( child, processedChild, variables );
            }
            else
            {
                String elementName = child.getName();

                // ----------------------------------------------------------------------
                // Check to see if this element name matches the id of any of our
                // source resource handlers.
                // ----------------------------------------------------------------------

                if ( handlers.containsKey( elementName ) )
                {
                    ConfigurationResourceHandler handler = (ConfigurationResourceHandler) handlers.get( elementName );

                    PlexusConfiguration[] configurations = handler.handleRequest( createHandlerParameters( child, variables ) );

                    for ( int j = 0; j < configurations.length; j++ )
                    {
                        processed.addChild( configurations[j] );
                    }
                }
                else
                {
                    processed.addChild( child );
                }
            }
        }
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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void copyAttributes( PlexusConfiguration source, XmlPlexusConfiguration target )
    {
        String[] names = source.getAttributeNames();

        for ( int i = 0; i < names.length; i++ )
        {
            target.setAttribute( names[i], source.getAttribute( names[i], null ) );
        }
    }
}
