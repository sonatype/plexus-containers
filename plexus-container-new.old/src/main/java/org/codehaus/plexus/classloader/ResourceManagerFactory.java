package org.codehaus.plexus.classloader;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @todo encapsulate all resources related configurations under a single element.
 */
public class ResourceManagerFactory
    extends AbstractPlexusFactory
{
    public static DefaultResourceManager create( PlexusConfiguration configuration,
                                                 LoggerManager loggerManager,
                                                 ClassLoader classLoader )
        throws Exception
    {
        String implementation = configuration.getChild( "resource-manager" ).getChild( "implementation" ).getValue();

        DefaultResourceManager rm = (DefaultResourceManager) getInstance( implementation, classLoader );

        rm.setClassLoader( classLoader );

        rm.enableLogging( loggerManager.getLogger( "resource-manager" ) );

        rm.configure( configuration.getChild( "resources" ) );

        return rm;
    }
}
