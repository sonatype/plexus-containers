package org.codehaus.plexus.logging;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.factory.AbstractPlexusFactory;

public class LoggerManagerFactory
    extends AbstractPlexusFactory
{
    private static final String IMPLEMENTATION_TAG = "implementation";

    public static LoggerManager create( PlexusConfiguration configuration, ClassLoader classLoader )
        throws Exception
    {
        String implementation = configuration.getChild( IMPLEMENTATION_TAG ).getValue( null );

        LoggerManager lm = (LoggerManager) getInstance( implementation, classLoader );

        lm.configure( configuration );

        lm.initialize();

        lm.start();

        return lm;
    }
}
