package org.codehaus.plexus;

import junit.framework.TestCase;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.plexus.context.Context;

public class PlexusTestCase
    extends TestCase
{
    private DefaultPlexusContainer container;

    public String basedir = System.getProperty( "basedir" );

    public PlexusTestCase( String testName )
    {
        super( testName );
    }

    protected void setUp()
        throws Exception
    {
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

        customizeContext();

        container.setConfigurationResource( new InputStreamReader( configuration ) );

        container.initialize();

        container.start();
    }

    //!!! this should probably take a context as a parameter so that the
    //    user is not forced to do getContainer().addContextValue(..)
    //    this would require a change to PlexusContainer in order to get
    //    hold of the context ...
    protected void customizeContext()
    {
    }

    protected InputStream getCustomConfiguration()
        throws Exception
    {
        return null;
    }

    protected void tearDown()
        throws Exception
    {
        container.dispose();
        container = null;
    }

    protected DefaultPlexusContainer getContainer()
    {
        return container;
    }

    protected InputStream getConfiguration()
        throws Exception
    {
        return getConfiguration( null );
    }

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

    protected InputStream getResourceAsStream( String resource )
    {
        return getClass().getResourceAsStream( resource );
    }

    protected ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    protected Object lookup( String componentKey )
        throws Exception
    {
        return getContainer().lookup( componentKey );
    }

    protected Object lookup( String role, String id )
        throws Exception
    {
        return getContainer().lookup( role, id );
    }

    protected void release( Object component )
        throws Exception
    {
        getContainer().release( component );
    }

    public String getTestFile( String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }

    public String getTestFile( String basedir, String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }
}
