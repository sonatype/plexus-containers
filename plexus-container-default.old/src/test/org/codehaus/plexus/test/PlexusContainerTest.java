package org.codehaus.plexus.test;

import junit.framework.TestCase;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.test.map.ActivityManager;
import org.codehaus.plexus.test.map.Activity;
import org.codehaus.plexus.test.list.Pipeline;
import org.codehaus.plexus.test.list.Valve;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 *  @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 */
public class PlexusContainerTest
    extends TestCase
{
    /** Configuration stream to use for default container test. */
    private InputStream configurationStream;

    /** Default container test classloader. */
    private ClassLoader classLoader;

    /** Default Container. */
    private DefaultPlexusContainer container;

    /**
     * Constructor for the PlexusTest object
     *
     * @param name
     */
    public PlexusContainerTest( String name )
    {
        super( name );
    }

    public void setUp()
        throws Exception
    {
        classLoader = getClass().getClassLoader();

        configurationStream = PlexusContainerTest.class.getResourceAsStream( "PlexusContainerTest.xml" );

        assertNotNull( configurationStream );

        assertNotNull( classLoader );

        container = new DefaultPlexusContainer();

        container.setConfigurationResource( new InputStreamReader( configurationStream ) );

        container.initialize();

        container.start();
    }

    public void tearDown()
        throws Exception
    {
        container.dispose();
        container = null;
    }

    public void testAutomatedComponentConfigurationUsingXStreamPoweredComponentConfigurator()
        throws Exception
    {
        Component component = (Component) container.lookup( Component.ROLE );

        assertNotNull( component );

        assertNotNull( component.getActivity() );

        assertEquals( "localhost", component.getHost() );

        assertEquals( 10000, component.getPort() );
    }

    public void testAutomatedComponentComposition()
        throws Exception
    {
        ComponentA componentA = (ComponentA) container.lookup( ComponentA.ROLE );

        assertNotNull( componentA );

        assertEquals( "localhost", componentA.getHost() );

        assertEquals( 10000, componentA.getPort() );

        ComponentB componentB = componentA.getComponentB();

        assertNotNull( componentB );

        ComponentC componentC = componentA.getComponentC();

        assertNotNull( componentC );

        ComponentD componentD = componentC.getComponentD();

        assertNotNull( componentD );

        assertEquals( "jason", componentD.getName() );
    }

    public void testComponentCompositionWhereTargetFieldIsAMap()
        throws Exception
    {
        ActivityManager am = (ActivityManager) container.lookup( ActivityManager.ROLE );

        Activity one = am.getActivity( "one" );

        assertNotNull( one );

        assertFalse( one.getState() );

        am.execute( "one" );

        assertTrue( one.getState() );

        Activity two = am.getActivity( "two" );

        assertNotNull( two );

        assertFalse( two.getState() );

        am.execute( "two" );

        assertTrue( two.getState() );
    }

    public void testComponentCompositionWhereTargetFieldIsAList()
        throws Exception
    {
        Pipeline pipeline = (Pipeline) container.lookup( Pipeline.ROLE );

        List valves = pipeline.getValves();

        assertFalse( ( (Valve) valves.get( 0 ) ).getState() );

        assertFalse( ( (Valve) valves.get( 1 ) ).getState() );

        pipeline.execute();

        assertTrue( ( (Valve) valves.get( 0 ) ).getState() );

        assertTrue( ( (Valve) valves.get( 1 ) ).getState() );
    }

    public void testLookupOfInternallyDefinedComponentConfigurator()
        throws Exception
    {
        ComponentConfigurator cc = (ComponentConfigurator) container.lookup( ComponentConfigurator.ROLE );
    }
}
