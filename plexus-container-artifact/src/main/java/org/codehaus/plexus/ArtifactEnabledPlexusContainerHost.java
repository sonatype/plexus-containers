package org.codehaus.plexus;

import org.codehaus.classworlds.ClassWorld;

/**
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ArtifactEnabledPlexusContainerHost
    extends PlexusContainerHost
{
    // ----------------------------------------------------------------------
    // Customizing Container Host
    // ----------------------------------------------------------------------

    protected DefaultPlexusContainer getPlexusContainer()
    {
        return new DefaultArtifactEnabledContainer();
    }

    /**
     *  Main entry-point.
     *
     *  @param args Command-line arguments.
     */
    public static void main( String[] args, ClassWorld classWorld )
    {
        if ( args.length != 1 )
        {
            System.err.println( "usage: plexus <plexus.conf>" );
            System.exit( 1 );
        }

        try
        {
            ArtifactEnabledPlexusContainerHost host = new ArtifactEnabledPlexusContainerHost();
            host.start( classWorld, args[0] );

            host.waitForContainerShutdown();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( 2 );
        }
    }
}
