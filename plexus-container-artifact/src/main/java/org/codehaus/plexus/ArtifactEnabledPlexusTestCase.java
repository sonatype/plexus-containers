/* Created on Aug 25, 2004 */
package org.codehaus.plexus;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author jdcasey
 */
public class ArtifactEnabledPlexusTestCase
    extends PlexusTestCase
{

    protected void setUp() throws Exception
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

        container = new DefaultArtifactEnabledContainer();

        //System.out.println( "Thread.currentThread().getContextClassLoader() =
        // " + Thread.currentThread().getContextClassLoader() );

        container.addContextValue( "basedir", basedir );

        customizeContext();

        boolean hasPlexusHome = container.getContext().contains( "plexus.home" );

        if ( !hasPlexusHome )
        {
            File f = new File( basedir, "target/plexus-home" );

            if ( !f.isDirectory() )
            {
                f.mkdir();
            }

            container.getContext().put( "plexus.home", f.getAbsolutePath() );
        }

        if ( configuration != null )
        {
            container.setConfigurationResource( new InputStreamReader( configuration ) );
        }

        container.initialize();

        container.start();
    }
}