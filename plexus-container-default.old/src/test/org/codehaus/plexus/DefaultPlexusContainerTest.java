package org.codehaus.plexus;

import junit.framework.TestCase;
import org.codehaus.plexus.util.AbstractTestThread;
import org.codehaus.plexus.util.TestThreadManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

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

    /**
     * Constructor for the PlexusTest object
     *
     * @param name
     */
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

        // ----------------------------------------------------------------------
        //  ServiceDescriptors
        // ----------------------------------------------------------------------

        assertEquals( true, container.hasService( ServiceA.ROLE ) );

        assertEquals( true, container.hasService( ServiceB.ROLE ) );

        assertEquals( true, container.hasService( ServiceC.ROLE + "only-instance" ) );

        assertEquals( true, container.hasService( ServiceG.ROLE ) );
    }

    /**
     * Test passage through standard avalon lifecycle.
     *
     * @throws Exception
     */
    public void testAvalonLifecyclePassage()
        throws Exception
    {
        // ----------------------------------------------------------------------
        //  ServiceA
        //
        //  Implements all the standard Avalon lifecycle phases.
        // ----------------------------------------------------------------------

        // Retrieve an instance of component a.

        DefaultServiceA serviceA = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceA );

        assertEquals( true, serviceA.enableLogging );

        assertEquals( true, serviceA.contextualize );

        assertEquals( true, serviceA.service );

        assertEquals( true, serviceA.configure );

        assertEquals( true, serviceA.initialize );

        assertEquals( true, serviceA.start );

        container.release( serviceA );

        assertEquals( true, serviceA.stop );

        assertEquals( true, serviceA.dispose );

        // make sure we get the same manager back everytime
        DefaultServiceA a0 = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        DefaultServiceA a1 = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        DefaultServiceA a2 = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        assertTrue( a0.equals( a1 ) );

        assertTrue( a1.equals( a2 ) );

        assertTrue( a2.equals( a0 ) );

	    // make sure that the component wasn't recycled
        assertFalse( serviceA.equals( a0 ) );

        container.release( a0 );

        container.release( a1 );

        container.release( a2 );
    }

    /**
     * Test passage through arbitrary component lifecycle.
     *
     * @throws Exception
     */
    public void testArbitraryLifecylePassage()
        throws Exception
    {
        // ----------------------------------------------------------------------
        //  ServiceB
        //
        //  Implements the special Plexus contextualize and component phases with
        //  the rest being the standard Avalon ones.
        // ----------------------------------------------------------------------

        // Retrieve an manager of component b.
        DefaultServiceB serviceB1 = (DefaultServiceB) container.lookup( ServiceB.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceB1 );

        assertEquals( true, serviceB1.enableLogging );

        assertEquals( true, serviceB1.contextualize );

        assertEquals( true, serviceB1.service );

        assertEquals( true, serviceB1.configure );

        assertEquals( true, serviceB1.initialize );

        assertEquals( true, serviceB1.start );

        assertNotNull( serviceB1.getClassLoader() );

        container.release( serviceB1 );

        // Retrieve another
        DefaultServiceB serviceB2 = (DefaultServiceB) container.lookup( ServiceB.ROLE );

        assertNotNull( serviceB2 );

        container.release( serviceB2 );
    }

    /**
     * Test component lookup for a component that has a role hint.
     *
     * @throws Exception
     */
    public void testComponentLookupWithRoleHint()
        throws Exception
    {
        // ----------------------------------------------------------------------
        //  ServiceC
        // ----------------------------------------------------------------------

        // Retrieve an manager of component c.
        DefaultServiceC serviceC1 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "only-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        // Retrieve the only manager again from the component repository.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "only-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        // Let's make sure it gave us back the same manager.
        assertSame( serviceC1, serviceC2 );

        container.release( serviceC1 );

        container.release( serviceC2 );
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

    /**
     * Test per-lookup instantiation strategy.
     *
     * @throws Exception
     */
    public void testPerLookupInstantiationStrategy()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Per-lookup component
        // ----------------------------------------------------------------------

        // Retrieve an manager of component e.
        DefaultServiceE serviceE1 = (DefaultServiceE) container.lookup( ServiceE.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceE1 );

        // Check the lifecycle
        assertEquals( true, serviceE1.enableLogging );

        assertEquals( true, serviceE1.contextualize );

        assertEquals( true, serviceE1.service );

        assertEquals( true, serviceE1.configure );

        assertEquals( true, serviceE1.initialize );

        assertEquals( true, serviceE1.start );

        // Retrieve another
        DefaultServiceE serviceE2 = (DefaultServiceE) container.lookup( ServiceE.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceE2 );

        // Check the lifecycle
        assertEquals( true, serviceE2.enableLogging );

        assertEquals( true, serviceE2.contextualize );

        assertEquals( true, serviceE2.service );

        assertEquals( true, serviceE2.configure );

        assertEquals( true, serviceE2.initialize );

        assertEquals( true, serviceE2.start );

        assertNotSame( serviceE1, serviceE2 );

        container.release( serviceE1 );

        container.release( serviceE2 );
    }

    /**
     * Test the loading of component configurations from ancillary configuration
     * files.
     *
     * @throws Exception
     */
    public void testLoadingOfAdditionalConfigurations()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Service F
        // ----------------------------------------------------------------------

        // The configuration for this component comes from a configuration using
        // the 'configurations-directory' directive in plexus.conf.
        ServiceF serviceF = (ServiceF) container.lookup( ServiceF.ROLE );

        assertNotNull( serviceF );

        // The configuration for this component has been pulled in using the 'configurations-directory'
        // directive and we want to make sure that context values are interpolated
        // correctly. For the test we are using ${plexus.home} which should be
        // interpolated so no "${" sequence should be present.
        assertFalse( serviceF.getPlexusHome().indexOf( "${" ) > 0 );

        container.release( serviceF );
    }

    /**
     * Test the SingletonKeepAlive instantiation strategy.
     *
     * @throws Exception
     */
    public void testSingletonKeepAliveInstantiationStrategy()
        throws Exception
    {

        // ----------------------------------------------------------------------
        //  ServiceG - singleton-keep-alive
        //
        //  Implements all the standard Avalon lifecycle phases.
        // ----------------------------------------------------------------------

        // Retrieve an manager of component G.
        DefaultServiceG serviceG = (DefaultServiceG) container.lookup( ServiceG.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceG );

        // Make sure the component went through all the lifecycle phases
        assertTrue( serviceG.enableLogging );

        assertTrue( serviceG.contextualize );

        assertTrue( serviceG.service );

        assertTrue( serviceG.configure );

        assertTrue( serviceG.initialize );

        assertTrue( serviceG.start );

        container.suspend( serviceG );

        assertTrue( serviceG.suspend );

        container.resume( serviceG );

        assertTrue( serviceG.resume );

        // Now how do we make sure it has been released and decomissioned
        // properly.
        container.release( serviceG );

        // make sure we get the same manager back everytime
        DefaultServiceG g0 = (DefaultServiceG) container.lookup( ServiceG.ROLE );

        DefaultServiceG g1 = (DefaultServiceG) container.lookup( ServiceG.ROLE );

        DefaultServiceG g2 = (DefaultServiceG) container.lookup( ServiceG.ROLE );

        assertTrue( g0.equals( g1 ) );

        assertTrue( g1.equals( g2 ) );

        assertTrue( g2.equals( g0 ) );

        //Now try it again in seperate threads.Make sure the manager is the same for all threads
        TestThreadManager reg = new TestThreadManager( this );

        for ( int i = 0; i < 5; i++ )
        {
            SingletonComponentTestThread st = new SingletonComponentTestThread( reg, container, ServiceG.ROLE, g0 );

            reg.registerThread( st );
        }

        reg.runTestThreads();

		//now wait for the threads to finish..
		synchronized (this)
		{
			try
			{
				if( reg.isStillRunningThreads() )
				{
					wait();
				}
			}
            catch ( InterruptedException e )
            {
            }
     	}

        assertEquals( "Expected 5 test threads to of run", reg.getRunThreads().size(), 5 );

        //now test if any components were returned which was not the same manager
        if ( reg.hasFailedThreads() )
        {
            //collect all failed tests

            StringBuffer out = new StringBuffer();

            String nl = System.getProperty( "line.separator" );

            for ( Iterator iter = reg.getFailedTests().iterator(); iter.hasNext(); )
            {
                out.append( nl );

                out.append( ( (SingletonComponentTestThread) iter.next() ).getErrorMsg() );
            }

            fail(
                "Singleton component 'ServiceG' being instantiated multiple times. Failed test threads: "
                + out );
        }

        container.release( g0 );

        container.release( g1 );

        container.release( g2 );
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
