package org.codehaus.plexus.logging;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

public interface LoggerManager
{
    Logger getRootLogger();

    Logger getLogger( String name );

    void configure( PlexusConfiguration configuration )
        throws PlexusConfigurationException;

    void initialize()
        throws Exception;

    void start()
        throws Exception;

    void stop()
        throws Exception;
}
