package org.codehaus.plexus.test;

import junit.framework.TestCase;
import org.codehaus.plexus.DefaultPlexusContainer;

import java.io.InputStream;
import java.io.InputStreamReader;

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
}
