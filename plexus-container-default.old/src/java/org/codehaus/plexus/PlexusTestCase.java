package org.codehaus.plexus;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.codehaus.plexus.context.Context;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusTestCase
    extends TestCase
{
    protected PlexusContainer container;

    /**
     * @deprecated Use getBasedir(); instead of accessing this variable directly.
     */
    protected String basedir;

    public PlexusTestCase()
    {
    }

    /**
     * @deprecated Use the no arg contstructor.
     */
    public PlexusTestCase( String testName )
    {
        super( testName );
    }

    protected void setUp()
        throws Exception
    {
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

        basedir = System.getProperty( "basedir" );

        if ( basedir == null )
        {
            basedir = new File( "" ).getAbsolutePath();
        }

//        container = new DefaultPlexusContainer();
        container = getContainerInstance();

        //System.out.println( "Thread.currentThread().getContextClassLoader() = " + Thread.currentThread().getContextClassLoader() );

        container.addContextValue( "basedir", basedir );

        customizeContext();

        boolean hasPlexusHome = getContext().contains( "plexus.home" );

        if ( !hasPlexusHome )
        {
            File f = new File( basedir, "target/plexus-home" );

            if ( !f.isDirectory() )
            {
                f.mkdir();
            }

            getContext().put( "plexus.home", f.getAbsolutePath() );
        }

        if ( configuration != null )
        {
            container.setConfigurationResource( new InputStreamReader( configuration ) );
        }

        container.initialize();

        container.start();
    }

    protected PlexusContainer getContainerInstance()
    {
        return new DefaultPlexusContainer();
    }

    private Context getContext()
    {
        return container.getContext();
    }

    //!!! this should probably take a context as a parameter so that the
    //    user is not forced to do getContainer().addContextValue(..)
    //    this would require a change to PlexusContainer in order to get
    //    hold of the context ...
    protected void customizeContext()
        throws Exception
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

    protected PlexusContainer getContainer()
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

        if ( subname == null || subname.equals( "" ) )
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

    // ----------------------------------------------------------------------
    // Container access
    // ----------------------------------------------------------------------

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

    // ----------------------------------------------------------------------
    // Helper methods for sub classes
    // ----------------------------------------------------------------------

    public File getTestFile( String path )
    {
        return new File( basedir, path );
    }

    public File getTestFile( String basedir, String path )
    {
        return new File( basedir, path );
    }

    public String getTestPath( String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }

    public String getTestPath( String basedir, String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }

    public String getBasedir()
    {
        if ( basedir == null )
        {
            fail( "'basedir' isn't set." );
        }

        return basedir;
    }
}
