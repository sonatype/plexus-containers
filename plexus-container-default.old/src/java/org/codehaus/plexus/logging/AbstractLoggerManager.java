package org.codehaus.plexus.logging;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;

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
