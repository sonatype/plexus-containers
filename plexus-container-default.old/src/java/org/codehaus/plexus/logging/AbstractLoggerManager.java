package org.codehaus.plexus.logging;

import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.configuration.ConfigurationException;


public abstract class AbstractLoggerManager
    implements LoggerManager
{
    public abstract Logger getRootLogger();

    public abstract Logger getLogger( String name );

    public abstract void configure( Configuration configuration )
        throws ConfigurationException;

    public abstract void initialize()
        throws Exception;

    public abstract void start()
        throws Exception;

    public abstract void stop()
        throws Exception;


}
