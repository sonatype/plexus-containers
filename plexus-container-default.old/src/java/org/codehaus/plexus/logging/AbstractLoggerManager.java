package org.codehaus.plexus.logging;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;


public abstract class AbstractLoggerManager
    implements LoggerManager
{
    public abstract Logger getRootLogger();

    public abstract Logger getLogger( String name );

    public abstract void configure( PlexusConfiguration configuration )
        throws PlexusConfigurationException;

    public abstract void initialize()
        throws Exception;

    public abstract void start()
        throws Exception;

    public abstract void stop()
        throws Exception;


}
