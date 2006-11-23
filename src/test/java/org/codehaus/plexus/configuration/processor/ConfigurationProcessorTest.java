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

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.PlexusTestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ConfigurationProcessorTest
    extends PlexusTestCase
{
    private Map variables;

    protected void setUp()
    {
        variables = new HashMap();

        variables.put( "basedir", getBasedir() );

        variables.put( "occupation", "slacker" );
    }

    public void testConfigurationProcessorWhereThereAreNoDirectivesForExternalConfigurations()
        throws Exception
    {
        ConfigurationProcessor p = new ConfigurationProcessor();

        XmlPlexusConfiguration source = new XmlPlexusConfiguration( "configuration" );

        // ----------------------------------------------------------------------

        XmlPlexusConfiguration a = new XmlPlexusConfiguration( "a" );

        a.setValue( "a" );

        source.addChild( a );

        // ----------------------------------------------------------------------

        XmlPlexusConfiguration b = new XmlPlexusConfiguration( "b" );

        b.setValue( "b" );

        source.addChild( b );

        // ----------------------------------------------------------------------

        XmlPlexusConfiguration c = new XmlPlexusConfiguration( "c" );

        c.setValue( "c" );

        source.addChild( c );

        // ----------------------------------------------------------------------
        // Just a check to make the source is of the form we need before testing
        // ----------------------------------------------------------------------

        assertEquals( "a", source.getChild( "a" ).getValue() );

        assertEquals( "b", source.getChild( "b" ).getValue() );

        assertEquals( "c", source.getChild( "c" ).getValue() );

        // ----------------------------------------------------------------------

        PlexusConfiguration processed = p.process( source, variables );

        assertEquals( "a", processed.getChild( "a" ).getValue() );

        assertEquals( "b", processed.getChild( "b" ).getValue() );

        assertEquals( "c", processed.getChild( "c" ).getValue() );
    }

    public void testConfigurationProcessorWithASimpleConfigurationResource()
        throws Exception
    {
        ConfigurationProcessor p = new ConfigurationProcessor();

        ConfigurationResourceHandler handler = new SimpleConfigurationResourceHandler();

        p.addConfigurationResourceHandler( handler );

        // ----------------------------------------------------------------------
        // Create the following:
        //
        // <configuration>
        //   <simple-configuration-resource source="local"/>
        // </configuration>
        //
        // ----------------------------------------------------------------------

        XmlPlexusConfiguration source = new XmlPlexusConfiguration( "configuration" );

        XmlPlexusConfiguration resource = new XmlPlexusConfiguration( handler.getId() );

        String sourceValue = "local";

        resource.setAttribute( "source", sourceValue );

        resource.setAttribute( "occupation", "${occupation}" );

        source.addChild( resource );

        // ----------------------------------------------------------------------
        // So we will now process the configuration and we should end up with
        // the following:
        //
        // <configuration>
        //   <name>local</name>
        // </configuration>
        //
        // The SimpleConfigurationResourceHandler just creates a "name" element
        // where the value is that of the source attribute.
        // ----------------------------------------------------------------------

        PlexusConfiguration processed = p.process( source, variables );

        assertEquals( sourceValue, processed.getChild( "name" ).getValue() );

        // ----------------------------------------------------------------------
        // Check that the interpolated value came through
        // ----------------------------------------------------------------------

        assertEquals( "slacker", processed.getChild( "occupation" ).getValue() );

    }
}
