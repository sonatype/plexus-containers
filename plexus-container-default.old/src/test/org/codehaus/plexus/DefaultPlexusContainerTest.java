package org.codehaus.plexus;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.codehaus.plexus.service.repository.factory.ComponentFactory;
import org.codehaus.plexus.util.AbstractTestThread;
import org.codehaus.plexus.util.TestThreadManager;

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
        configurationStream = DefaultPlexusContainerTest.class.getResourceAsStream( "configuration.xml" );

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

    public void testSetup()
        throws Exception
    {
        int defaultComponents = 0;
        int testComponents = 7;

        // These are some default components that we used internally. These components don't
        // usually need to be replaced but they can be if the user desires.

        // Java Component factory.
        // Singleton instance manager.
        ComponentFactory jcf = (ComponentFactory) container.getComponentRepository().lookup( ComponentFactory.ROLE + "java" );
        assertNotNull( jcf );
        defaultComponents++;

        // Make sure all our service descriptors are present.
        assertEquals( testComponents + defaultComponents, container.getComponentRepository().configuredComponents() );

        // ----------------------------------------------------------------------
        //  ServiceDescriptors
        // ----------------------------------------------------------------------

        assertEquals( true, container.getComponentRepository().hasService( ServiceA.ROLE ) );
        assertEquals( true, container.getComponentRepository().hasService( ServiceB.ROLE ) );
        assertEquals( true, container.getComponentRepository().hasService( ServiceC.ROLE + "only-instance" ) );
        assertEquals( true, container.getComponentRepository().hasService( ServiceG.ROLE ) );

        // ----------------------------------------------------------------------
        //  ServiceA
        //
        //  Implements all the standard Avalon lifecycle phases.
        // ----------------------------------------------------------------------

        // Retrieve an instance of service a.
        DefaultServiceA serviceA = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );

        // Make sure the service is alive.
        assertNotNull( serviceA );

        // After the above lookup we should have one instantiated service.
        assertEquals( 1 + defaultComponents, container.getComponentRepository().instantiatedComponents() );

        assertEquals( true, serviceA.enableLogging );
        assertEquals( true, serviceA.contextualize );
        assertEquals( true, serviceA.service );
        assertEquals( true, serviceA.configure );
        assertEquals( true, serviceA.initialize );
        assertEquals( true, serviceA.start );

        // Now how do we make sure it has been released and decomissioned
        // properly.
        container.getComponentRepository().release( serviceA );

        // Now we have released the component so there should be no instantiated services.
        //no longer correct! The instance managers may keep an instance alive.
//        assertEquals( 0 + defaultComponents, container.getComponentRepository().instantiatedComponents() );

        // Make sure the number of configured components is still 3.
        assertEquals( testComponents + defaultComponents, container.getComponentRepository().configuredComponents() );

        // make sure we get the same instance back everytime
        DefaultServiceA a0 = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );
        DefaultServiceA a1 = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );
        DefaultServiceA a2 = (DefaultServiceA) container.getComponentRepository().lookup( ServiceA.ROLE );

        assertTrue( a0.equals( a1 ) );
        assertTrue( a1.equals( a2 ) );
        assertTrue( a2.equals( a0 ) );

        // ----------------------------------------------------------------------
        //  ServiceB
        //
        //  Implements the special Plexus contextualize and service phases with
        //  the rest being the standard Avalon ones.
        // ----------------------------------------------------------------------

        // Retrieve an instance of service b.
        DefaultServiceB serviceB1 = (DefaultServiceB) container.getComponentRepository().lookup( ServiceB.ROLE );

        // Make sure the service is alive.
        assertNotNull( serviceB1 );

        assertEquals( true, serviceB1.enableLogging );
        assertEquals( true, serviceB1.contextualize );
        assertEquals( true, serviceB1.service );
        assertEquals( true, serviceB1.configure );
        assertEquals( true, serviceB1.initialize );
        assertEquals( true, serviceB1.start );

        assertNotNull( serviceB1.getClassLoader() );

        // Retrieve another
        DefaultServiceB serviceB2 = (DefaultServiceB) container.getComponentRepository().lookup( ServiceB.ROLE );

        assertNotNull( serviceB2 );

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

        // Retrieve an instance of service c.
        DefaultServiceC serviceC1 =
            (DefaultServiceC) container.getComponentRepository().lookup( ServiceC.ROLE, "only-instance" );

        // Make sure the service is alive.
        assertNotNull( serviceC1 );

        // Retrieve the only instance again from the service repository.
        DefaultServiceC serviceC2 =
            (DefaultServiceC) container.getComponentRepository().lookup( ServiceC.ROLE, "only-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        // Let's make sure it gave us back the same instance.
        assertSame( serviceC1, serviceC2 );

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

        // Retrieve an instance of service c.
        ServiceD serviceD1 = (ServiceD) container.getComponentRepository().lookup( ServiceD.ROLE );
        assertNotNull( serviceD1 );

        ServiceD serviceD2 = (ServiceD) container.getComponentRepository().lookup( ServiceD.ROLE );
        assertNotNull( serviceD2 );

        ServiceD serviceD3 = (ServiceD) container.getComponentRepository().lookup( ServiceD.ROLE );
        assertNotNull( serviceD3 );

        assertNotSame( serviceD1, serviceD2 );
        assertNotSame( serviceD2, serviceD3 );
        assertNotSame( serviceD1, serviceD3 );
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

        // Retrieve an instance of service e.
        DefaultServiceE serviceE1 = (DefaultServiceE) container.getComponentRepository().lookup( ServiceE.ROLE );

        // Make sure the service is alive.
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

        // Make sure the service is alive.
        assertNotNull( serviceE2 );

        // Check the lifecycle
        assertEquals( true, serviceE2.enableLogging );
        assertEquals( true, serviceE2.contextualize );
        assertEquals( true, serviceE2.service );
        assertEquals( true, serviceE2.configure );
        assertEquals( true, serviceE2.initialize );
        assertEquals( true, serviceE2.start );

        assertNotSame( serviceE1, serviceE2 );

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

        // The configuration for this service has been pulled in using the 'configurations-directory'
        // directive and we want to make sure that context values are interpolated
        // correctly. For the test we are using ${plexus.home} which should be
        // interpolated so no "${" sequence should be present.
        assertFalse( serviceF.getPlexusHome().indexOf( "${" ) > 0 );
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

        // Retrieve an instance of service G.
        DefaultServiceG serviceG =
            (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );

        // Make sure the service is alive.
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

        // make sure we get the same instance back everytime
        DefaultServiceG g0 =
            (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );
        DefaultServiceG g1 =
            (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );
        DefaultServiceG g2 =
            (DefaultServiceG) container.getComponentRepository().lookup( ServiceG.ROLE );

        assertTrue( g0.equals( g1 ) );
        assertTrue( g1.equals( g2 ) );
        assertTrue( g2.equals( g0 ) );

        //Now try it again in seperate threads.Make sure the instance is the same for all threads
        TestThreadManager reg = new TestThreadManager( this );
        for ( int i = 0; i < 5; i++ )
        {
            SingletonComponentTestThread st =
                new SingletonComponentTestThread( reg, container, ServiceG.ROLE, g0 );
            reg.registerThread( st );
        }
        reg.runTestThreads();

        while ( reg.isStillRunningThreads() )
        {
            //wait until all threads have finished execution
            synchronized ( this )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {
                }
            }
        }

        assertEquals( "Expected 5 test threads to of run", reg.getRunThreads().size(), 5 );
        //now test if any components were returned which was not the same instance
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
                    setErrorMsg( "Returned component was a different instance. Expected=" + expectedComponent + ", got=" + returnedComponent );
                }
            }
            finally
            {
                container.getComponentRepository().release( returnedComponent );
            }
        }

    }

}
