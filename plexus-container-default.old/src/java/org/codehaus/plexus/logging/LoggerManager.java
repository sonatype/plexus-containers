package org.codehaus.plexus.logging;

import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.configuration.ConfigurationException;

public interface LoggerManager
{
    Logger getRootLogger();

    Logger getLogger( String name );

    void configure( Configuration configuration )
        throws ConfigurationException;

    void initialize()
        throws Exception;

    void start()
        throws Exception;

    void stop()
        throws Exception;
}
