package org.codehaus.plexus;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.logger.Logger;

/** This component implements all the start phases:
 *
 *  LogEnabled
 *  Contexualize
 *  Serviceable
 *  Configurable
 *  Initializable
 *  Startable
 *
 */
public class DefaultServiceG
    extends AbstractLogEnabled
    implements ServiceG, Contextualizable, Serviceable, Configurable, Initializable, Startable, Suspendable
{
    boolean enableLogging;
    boolean contextualize;
    boolean service;
    boolean configure;
    boolean initialize;
    boolean start;
    boolean stop;
    boolean suspend;
    boolean resume;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void enableLogging( Logger logger )
    {
        enableLogging = true;
    }

    public void contextualize( Context context )
    {
        contextualize = true;
    }

    public void service( ServiceManager serviceManager )
    {
        service = true;
    }

    public void configure( Configuration configuration )
    {
        configure = true;
    }

    public void initialize()
        throws Exception
    {
        initialize = true;
    }

    public void start()
        throws Exception
    {
        start = true;
    }

    public void stop()
        throws Exception
    {
        stop = true;
    }

    public void suspend()
    {
        suspend = true;
    }

    public void resume()
    {
        resume = true;
    }
}
