package org.codehaus.plexus;

import junit.framework.TestCase;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class PlexusTestCase
    extends TestCase
{
    /** Plexus container to run test in. */
    DefaultPlexusContainer container;

    /**
     * Basedir for all file I/O. Important when running tests from
     * the reactor.
     */
    public String basedir = System.getProperty( "basedir" );

    /**
     * Constructor.
     *
     *  @param testName
     */
    public PlexusTestCase( String testName )
    {
        super( testName );
    }

    /**
     * Set up the test-case by starting the container.
     */
    public void setUp()
        throws Exception
    {
        // For testing we want to set the root directory so that context
        // values can retrieved without error.
        if ( basedir == null )
        {
            basedir = new File( "" ).getCanonicalPath();
        }

        File f = new File( basedir, "target/plexus-home" );
        System.setProperty( "plexus.home", f.getAbsolutePath() );

        if ( !f.isDirectory() )
        {
            f.mkdir();
        }

        InputStream configuration = null;

        try
        {
            configuration = getCustomConfiguration();

            if ( configuration == null )
            {
                configuration = getConfiguration();
            }
        }
        catch ( Exception e )
        {
            System.out.println( "Error with configuration:" );
            System.out.println( "configuration = " + configuration );
            fail( e.getMessage() );
        }

        if ( configuration == null )
        {
            throw new IllegalStateException( "The configuration for your plexus test case cannot be null. " );
        }

        container = new DefaultPlexusContainer();
        container.addContextValue( "basedir", basedir );
        container.addContextValue( "plexus.home", System.getProperty( "plexus.home" ) );
        container.setConfigurationResource( new InputStreamReader( configuration ) );
        container.initialize();
        container.start();
    }

    public InputStream getCustomConfiguration()
        throws Exception
    {
        return null;
    }

    /**
     * Tear down the test-case by stopping the container container manager..
     */
    public void tearDown()
        throws Exception
    {
        container.dispose();
        container = null;
    }

    /**
     * Get the container container manager.
     *
     * @return The container manager.
     */
    protected DefaultPlexusContainer getContainer()
    {
        return container;
    }

    /**
     * Get the configuration for this test.
     *
     * @return Configuration for this test.
     *
     * @throws Exception If an error occurs retrieve the container configuration.
     */
    protected InputStream getConfiguration()
        throws Exception
    {
        return getConfiguration( null );
    }

    /**
     * Get the container configuration from an URL.
     *
     * @return The container configuration.
     */
    protected URL getConfigurationUrl()
    {
        String className = getClass().getName();
        String base = className.substring( className.lastIndexOf( "." ) + 1 );
        String config = base + ".xml";

        return getClass().getResource( config );
    }

    /** Retrieve the default Plexus configuration.
     *
     *  @return The configuration.
     */
    protected InputStream getConfiguration( String subname )
        throws Exception
    {
        String className = getClass().getName();
        String base = className.substring( className.lastIndexOf( "." ) + 1 );

        String config = null;

        if ( subname == null
            || subname.equals( "" ) )
        {
            config = base + ".xml";
        }
        else
        {
            config = base + "-" + subname + ".xml";
        }

        InputStream configStream = getResourceAsStream( config );

        return configStream;
    }

    /**
     *  Retrieve a test resource that is in the same package as the test case.
     *
     *  @param resource Resource to find.
     *
     *  @return The input stream or null if the resource couldn't be located.
     */
    protected InputStream getResourceAsStream( String resource )
    {
        return getClass().getResourceAsStream( resource );
    }

    /**
     * Get the classloader used by the testcase.
     *
     * @return The classloader used by test case.
     */
    protected ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    /**
     *
     * @param componentKey
     * @return
     * @throws Exception
     */
    protected Object lookup( String componentKey )
        throws Exception
    {
        return getContainer().getComponentRepository().lookup( componentKey );
    }

    protected Object lookup( String role, String id )
        throws Exception
    {
        return getContainer().getComponentRepository().lookup( role, id );
    }

    /** Retrieve a component by componentKey.
     *
     *  @param componentKey The componentKey.
     *
     *  @return A matching component.
     *
     *  @throws Exception If an error occurs.
     *  @deprecated use lookup( componentKey )
     */
    protected Object getComponent( String componentKey )
        throws Exception
    {
        return lookup( componentKey );
    }

    /**
     *
     * @param componentKey
     * @param id
     * @return
     * @throws Exception
     * @deprecated use lookup( componentKey, id )
     */
    protected Object getComponent( String componentKey, String id )
        throws Exception
    {
        return lookup( componentKey, id );
    }

    protected void release( Object component )
        throws Exception
    {
        container.getComponentRepository().release( component );
    }

    // Some convenience methods for retrieving files in tests.

    /**
     * Get test input file.
     *
     * @param path Path to test input file.
     */
    public String getTestFile( String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }

    /**
     * Get test input file.
     *
     * @param path Path to test input file.
     */
    public String getTestFile( String basedir, String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }
}
