package org.codehaus.plexus.configuration;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ConfigurationMergerTest
    extends TestCase
{
    private PlexusConfiguration user;

    private PlexusConfiguration system;

    public ConfigurationMergerTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        InputStream userStream =
            Thread.currentThread().getContextClassLoader().getResourceAsStream( "org/codehaus/plexus/configuration/avalon.xml" );

        assertNotNull( userStream );

        user = PlexusTools.buildConfiguration( new InputStreamReader( userStream ) );

        InputStream systemStream =
            Thread.currentThread().getContextClassLoader().getResourceAsStream( "org/codehaus/plexus/plexus.conf" );

        assertNotNull( systemStream );

        system = PlexusTools.buildConfiguration( new InputStreamReader( systemStream ) );
    }

    public void testSimpleConfigurationCascading()
        throws Exception
    {
        PlexusConfiguration cc = PlexusConfigurationMerger.merge( user, system );

        assertEquals( "user-conf-dir", cc.getChildren( "configurations-directory" )[0].getValue() );

        assertEquals( "org.codehaus.plexus.personality.avalon.AvalonComponentRepository",
                      cc.getChild( "component-repository" ).getChild( "implementation" ).getValue() );

        assertEquals( "logging-implementation", cc.getChild( "logging" ).getChild( "implementation" ).getValue() );

        PlexusConfiguration lhm = cc.getChild( "lifecycle-handler-manager" );

        assertEquals( "avalon", lhm.getChild( "default-lifecycle-handler-id" ).getValue() );

        PlexusConfiguration lh = lhm.getChild( "lifecycle-handlers" ).getChildren( "lifecycle-handler" )[0];

        assertEquals( "avalon", lh.getChild( "id" ).getValue() );

        PlexusConfiguration[] bs = lh.getChild( "begin-segment" ).getChildren( "phase" );

        assertEquals( "org.codehaus.plexus.personality.avalon.lifecycle.phase.LogEnablePhase", bs[0].getAttribute( "implementation" ) );

        assertEquals( "org.codehaus.plexus.personality.avalon.lifecycle.phase.ContextualizePhase", bs[1].getAttribute( "implementation" ) );

        assertEquals( "org.codehaus.plexus.personality.avalon.lifecycle.phase.ServicePhase", bs[2].getAttribute( "implementation" ) );

        assertEquals( "org.codehaus.plexus.personality.avalon.lifecycle.phase.ComposePhase", bs[3].getAttribute( "implementation" ) );

        assertEquals( "org.codehaus.plexus.personality.avalon.lifecycle.phase.ConfigurePhase", bs[4].getAttribute( "implementation" ) );

        assertEquals( "org.codehaus.plexus.personality.avalon.lifecycle.phase.InitializePhase", bs[5].getAttribute( "implementation" ) );

        assertEquals( "org.codehaus.plexus.personality.avalon.lifecycle.phase.StartPhase", bs[6].getAttribute( "implementation" ) );

        PlexusConfiguration componentMM = cc.getChild( "component-manager-manager" );

        assertEquals( "singleton", componentMM.getChild( "default-component-manager-id" ).getValue() );

        PlexusConfiguration[] components = cc.getChild( "components" ).getChildren( "component" );

        // There are now three internal components defined which come before the user components
        // are processed.

        assertEquals( "org.codehaus.plexus.ServiceA", components[3].getChild( "role" ).getValue() );

        // Test the merging of the <resources> elements.

        PlexusConfiguration[] resources = cc.getChild( "resources" ).getChildren();

        assertEquals( 2, resources.length );

        assertEquals( "jar-resource", resources[0].getName() );

        assertEquals( "${foo.home}/jars", resources[0].getValue() );

        assertEquals( "my-resource", resources[1].getName() );

        assertEquals( "${my.home}/resources", resources[1].getValue() );
    }
}
