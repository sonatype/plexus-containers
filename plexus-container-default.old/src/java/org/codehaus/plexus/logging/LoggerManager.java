package org.codehaus.plexus.logging;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;

public interface LoggerManager
    extends Configurable, Initializable, Startable
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
