package org.codehaus.plexus.configuration.processor;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.PlexusTestCase;

import java.util.HashMap;
import java.util.Map;

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
