package org.codehaus.plexus;

import junit.framework.TestCase;
import org.codehaus.plexus.component.factory.ComponentFactory;
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

    /**
     * Test container setup.
     *
     * @throws Exception
     */
    public void testDefaultPlexusContainerSetup()
        throws Exception
    {
        // These are some default components that we used internally. These components don't
        // usually need to be replaced but they can be if the user desires.

        // Java Component factory.
        // Singleton manager manager.
        ComponentFactory jcf = (ComponentFactory) container.getComponentRepository().lookup( ComponentFactory.ROLE + "java" );
        assertNotNull( jcf );

        // ----------------------------------------------------------------------
        //  ServiceDescriptors
        // ----------------------------------------------------------------------

        assertEquals( true, container.getComponentRepository().hasService( ServiceA.ROLE ) );
        assertEquals( true, container.getComponentRepository().hasService( ServiceB.ROLE ) );
        assertEquals( true, container.getComponentRepository().hasService( ServiceC.ROLE + "only-instance" ) );
        assertEquals( true, container.getComponentRepository().hasService( ServiceG.ROLE ) );

        container.getComponentRepository().release( jcf );
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

        // Retrieve an manager of component a.

        container.getComponentRepository().lookup( ServiceA.ROLE );

        DefaultServiceA serviceA = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceA );

        assertEquals( true, serviceA.enableLogging );
        assertEquals( true, serviceA.contextualize );
        assertEquals( true, serviceA.service );
        assertEquals( true, serviceA.configure );
        assertEquals( true, serviceA.initialize );
        assertEquals( true, serviceA.start );

        // Now how do we make sure it has been released and decomissioned
        // properly.
        container.getComponentRepository().release( serviceA );

        // make sure we get the same manager back everytime
        DefaultServiceA a0 = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );
        DefaultServiceA a1 = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );
        DefaultServiceA a2 = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );

        assertTrue( a0.equals( a1 ) );
        assertTrue( a1.equals( a2 ) );
        assertTrue( a2.equals( a0 ) );

        container.getComponentRepository().release( a0 );
        container.getComponentRepository().release( a1 );
        container.getComponentRepository().release( a2 );
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
        DefaultServiceB serviceB1 = (DefaultServiceB) container.getComponentRepository().lookup( ServiceB.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceB1 );

        assertEquals( true, serviceB1.enableLogging );
        assertEquals( true, serviceB1.contextualize );
        assertEquals( true, serviceB1.service );
        assertEquals( true, serviceB1.configure );
        assertEquals( true, serviceB1.initialize );
        assertEquals( true, serviceB1.start );

        assertNotNull( serviceB1.getClassLoader() );

        container.getComponentRepository().release( serviceB1 );

        // Retrieve another
        DefaultServiceB serviceB2 = (DefaultServiceB) container.getComponentRepository().lookup( ServiceB.ROLE );

        assertNotNull( serviceB2 );

        container.getComponentRepository().release( serviceB2 );
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
        DefaultServiceC serviceC1 = (DefaultServiceC) container.getComponentRepository().lookup( ServiceC.ROLE, "only-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        // Retrieve the only manager again from the component repository.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.getComponentRepository().lookup( ServiceC.ROLE, "only-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        // Let's make sure it gave us back the same manager.
        assertSame( serviceC1, serviceC2 );

        container.getComponentRepository().release( serviceC1 );
        container.getComponentRepository().release( serviceC2 );
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
        ServiceD serviceD1 = (ServiceD) container.getComponentRepository().lookup( ServiceD.ROLE );
        assertNotNull( serviceD1 );

        ServiceD serviceD2 = (ServiceD) container.getComponentRepository().lookup( ServiceD.ROLE );
        assertNotNull( serviceD2 );

        ServiceD serviceD3 = (ServiceD) container.getComponentRepository().lookup( ServiceD.ROLE );
        assertNotNull( serviceD3 );

        assertNotSame( serviceD1, serviceD2 );
        assertNotSame( serviceD2, serviceD3 );
        assertNotSame( serviceD1, serviceD3 );

        // Now let's release all the components.

        container.getComponentRepository().release( serviceD1 );
        container.getComponentRepository().release( serviceD2 );
        container.getComponentRepository().release( serviceD3 );

        ServiceD[] ds = new DefaultServiceD[30];

        for ( int h = 0; h < 5; h++ )
        {
            System.out.println( "Consume/Release iteration[ " + h + " ]" );

            // Consume all available components in the pool.

            for ( int i = 0; i < 30; i++ )
            {
                ds[i] = (ServiceD) container.getComponentRepository().lookup( ServiceD.ROLE );
            }

            // Release them all.

            for ( int i = 0; i < 30; i++ )
            {
                container.getComponentRepository().release( ds[i] );
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
        DefaultServiceE serviceE1 = (DefaultServiceE) container.getComponentRepository().lookup( ServiceE.ROLE );

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
        DefaultServiceE serviceE2 = (DefaultServiceE) container.getComponentRepository().lookup( ServiceE.ROLE );

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

        container.getComponentRepository().release( serviceE1 );
        container.getComponentRepository().release( serviceE2 );
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
        ServiceF serviceF = (ServiceF) container.getComponentRepository().lookup( ServiceF.ROLE );

        assertNotNull( serviceF );

        // The configuration for this component has been pulled in using the 'configurations-directory'
        // directive and we want to make sure that context values are interpolated
        // correctly. For the test we are using ${plexus.home} which should be
        // interpolated so no "${" sequence should be present.
        assertFalse( serviceF.getPlexusHome().indexOf( "${" ) > 0 );

        container.getComponentRepository().release( serviceF );
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
        DefaultServiceG serviceG = (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceG );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceG.enableLogging );
        assertEquals( true, serviceG.contextualize );
        assertEquals( true, serviceG.service );
        assertEquals( true, serviceG.configure );
        assertEquals( true, serviceG.initialize );
        assertEquals( true, serviceG.start );

        // Now how do we make sure it has been released and decomissioned
        // properly.
        container.getComponentRepository().release( serviceG );

        // make sure we get the same manager back everytime
        DefaultServiceG g0 = (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );
        DefaultServiceG g1 = (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );
        DefaultServiceG g2 = (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );

        assertTrue( g0.equals( g1 ) );
        assertTrue( g1.equals( g2 ) );
        assertTrue( g2.equals( g0 ) );

        //Now try it again in seperate threads.Make sure the manager is the same for all threads
        TestThreadManager reg = new TestThreadManager( this );
        for ( int i = 0; i < 5; i++ )
        {
            SingletonComponentTestThread st =
                new SingletonComponentTestThread( reg, container, ServiceG.ROLE, g0 );
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

        container.getComponentRepository().release( g0 );
        container.getComponentRepository().release( g1 );
        container.getComponentRepository().release( g2 );
    }


    // ----------------------------------------------------------------------
    // Test using an arbitrary component lifecycle handler
    // ----------------------------------------------------------------------

    public void testArbitraryLifecyclePassageUsingFourArbitraryPhases()
        throws Exception
    {
        // Retrieve an manager of component G.
        DefaultServiceH serviceH = (DefaultServiceH) container.getComponentRepository().lookup( ServiceH.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceH );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceH.eeny );
        assertEquals( true, serviceH.meeny );
        assertEquals( true, serviceH.miny );
        assertEquals( true, serviceH.mo );

        container.getComponentRepository().release( serviceH );
    }

    class SingletonComponentTestThread extends AbstractTestThread
    {
        private Object expectedComponent;
        private Object returnedComponent;
        private PlexusContainer container;
        private String role;

        /**
         *
         */
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
                returnedComponent = container.getComponentRepository().lookup( role );
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
                container.getComponentRepository().release( returnedComponent );
            }
        }

    }

}
