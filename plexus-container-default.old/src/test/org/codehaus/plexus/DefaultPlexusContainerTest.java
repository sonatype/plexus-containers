package org.codehaus.plexus;

import junit.framework.TestCase;
import org.codehaus.plexus.util.AbstractTestThread;
import org.codehaus.plexus.util.TestThreadManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 *  @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 */
public class DefaultPlexusContainerTest
    extends TestCase
{
    /** Configuration stream to use for default container test. */
    private InputStream configurationStream;

    /** Default container test classloader. */
    private ClassLoader classLoader;

    /** Basedir for default container test. */
    private String basedir;

    /** Default Container. */
    private DefaultPlexusContainer container;

    public DefaultPlexusContainerTest( String name )
    {
        super( name );
    }

    public void setUp()
        throws Exception
    {
        basedir = System.getProperty( "basedir" );

        classLoader = getClass().getClassLoader();

        configurationStream = DefaultPlexusContainerTest.class.getResourceAsStream( "DefaultPlexusContainerTest.xml" );

        // Make sure our testing necessities are alive.
        assertNotNull( configurationStream );

        assertNotNull( classLoader );

        container = new DefaultPlexusContainer();

        container.addContextValue( "basedir", basedir );

        container.addContextValue( "plexus.home", basedir + "/target/plexus-home" );

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

    public void testDefaultPlexusContainerSetup()
        throws Exception
    {
        assertEquals( "bar", System.getProperty( "foo" ) );
    }

    // ----------------------------------------------------------------------
    // Test the native plexus lifecycle. Note that the configuration for
    // this TestCase supplies its own lifecycle, so this test verifies that
    // the native lifecycle is available after configuration merging.
    // ----------------------------------------------------------------------

    public void testNativeLifecyclePassage()
        throws Exception
    {
        DefaultServiceB serviceB = (DefaultServiceB) container.lookup( ServiceB.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceB );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceB.enableLogging );

        assertEquals( true, serviceB.contextualize );

        assertEquals( true, serviceB.initialize );

        assertEquals( true, serviceB.start );

        assertEquals( false, serviceB.stop );

        container.release( serviceB );

        assertEquals( true, serviceB.stop );
    }

    /*
     * Check that we can get references to a single component with a role
     * hint.
     */
    public void testSingleComponentLookupWithRoleHint()
        throws Exception
    {
        // Retrieve an instance of component c.
        DefaultServiceC serviceC1 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "first-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        assertTrue( serviceC1.started );

        assertFalse( serviceC1.stopped );

        // Retrieve a second reference to the same component.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "first-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        // Let's make sure it gave us back the same component.
        assertSame( serviceC1, serviceC2 );

        container.release( serviceC1 );

        // The component should still be alive.
        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        container.release( serviceC2 );

        // The component should now have been stopped.
        assertTrue( serviceC2.started );

        assertTrue( serviceC2.stopped );
    }

    /*
     * Check that distinct components with the same implementation are
     * managed correctly.
     */
    public void testMultipleSingletonComponentInstances()
        throws Exception
    {
        // Retrieve an instance of component c.
        DefaultServiceC serviceC1 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "first-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        assertTrue( serviceC1.started );

        assertFalse( serviceC1.stopped );

        // Retrieve an instance of component c, with a different role hint.
        // This should give us a different component instance.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "second-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        // The components should be distinct.
        assertNotSame( serviceC1, serviceC2 );

        container.release( serviceC1 );

        // The first component should now have been stopped, the second
        // one should still be alive.
        assertTrue( serviceC1.started );

        assertTrue( serviceC1.stopped );

        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        container.release( serviceC2 );

        // The second component should now have been stopped.
        assertTrue( serviceC2.started );

        assertTrue( serviceC2.stopped );
    }

    /**
     * Test poolable instantiation strategy.
     *
     * @throws Exception
     */
    public void testPoolableInstantiationStrategy()
        throws Exception
    {
        // ----------------------------------------------------------------------
        //  ServiceD
        // ----------------------------------------------------------------------

        // Retrieve an manager of component c.
        ServiceD serviceD1 = (ServiceD) container.lookup( ServiceD.ROLE );

        assertNotNull( serviceD1 );

        ServiceD serviceD2 = (ServiceD) container.lookup( ServiceD.ROLE );

        assertNotNull( serviceD2 );

        ServiceD serviceD3 = (ServiceD) container.lookup( ServiceD.ROLE );

        assertNotNull( serviceD3 );

        assertNotSame( serviceD1, serviceD2 );

        assertNotSame( serviceD2, serviceD3 );

        assertNotSame( serviceD1, serviceD3 );

        // Now let's release all the components.

        container.release( serviceD1 );

        container.release( serviceD2 );

        container.release( serviceD3 );

        ServiceD[] ds = new DefaultServiceD[30];

        for ( int h = 0; h < 5; h++ )
        {
            // Consume all available components in the pool.

            for ( int i = 0; i < 30; i++ )
            {
                ds[i] = (ServiceD) container.lookup( ServiceD.ROLE );
            }

            // Release them all.

            for ( int i = 0; i < 30; i++ )
            {
                container.release( ds[i] );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Test using an arbitrary component lifecycle handler
    // ----------------------------------------------------------------------

    public void testArbitraryLifecyclePassageUsingFourArbitraryPhases()
        throws Exception
    {
        // Retrieve an manager of component G.
        DefaultServiceH serviceH = (DefaultServiceH) container.lookup( ServiceH.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceH );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceH.eeny );

        assertEquals( true, serviceH.meeny );

        assertEquals( true, serviceH.miny );

        assertEquals( true, serviceH.mo );

        container.release( serviceH );
    }

    public void testLookupAll()
        throws Exception
    {
        Map components = container.lookupMap( ServiceC.ROLE );

        assertNotNull( components );

        assertEquals( 2, components.size() );

        ServiceC component = (ServiceC) components.get( "first-instance" );

        assertNotNull( component );

        component = (ServiceC) components.get( "second-instance" );

        assertNotNull( component );

        container.releaseAll( components );
    }

    class SingletonComponentTestThread
        extends AbstractTestThread
    {
        private Object expectedComponent;

        private Object returnedComponent;

        private PlexusContainer container;

        private String role;

        public SingletonComponentTestThread( PlexusContainer container, String role, Object expectedComponent )
        {
            super();

            this.expectedComponent = expectedComponent;

            this.container = container;

            this.role = role;
        }

        /**
         * @param registry
         */
        public SingletonComponentTestThread( TestThreadManager registry, PlexusContainer container, String role, Object expectedComponent )
        {
            super( registry );

            this.expectedComponent = expectedComponent;

            this.container = container;

            this.role = role;
        }

        /* (non-Javadoc)
         * @see org.codehaus.plexus.util.AbstractRegisteredThread#doRun()
         */
        public void doRun() throws Throwable
        {
            try
            {
                returnedComponent = container.lookup( role );

                if ( returnedComponent == null )
                {
                    setErrorMsg( "Null component returned" );
                }
                else if ( returnedComponent == expectedComponent )
                {
                    setPassed( true );
                }
                else
                {
                    setErrorMsg( "Returned component was a different manager. Expected=" + expectedComponent + ", got=" + returnedComponent );
                }
            }
            finally
            {
                container.release( returnedComponent );
            }
        }
    }
}
