package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.ServiceA;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Recontextualizable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.logger.Logger;

/** This component implements all phases:
 *
 *  LogEnabled
 *  Contexualize
 *  Serviceable
 *  Configurable
 *  Initializable
 *  Startable
 *
 */
public class AllPhaseService
    extends AbstractLogEnabled
    implements ServiceA, Contextualizable, Serviceable, Configurable,
               Initializable, Startable, Disposable, Reconfigurable,
               Recontextualizable, Suspendable
{
    boolean enableLogging;
    boolean contextualize;
    boolean service;
    boolean configure;
    boolean initialize;
    boolean start;
    boolean stop;
    boolean reconfigure;
    boolean dispose;
    boolean recontextualize;
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

    public void dispose()
    {
        dispose = true;        
    }

    public void reconfigure(Configuration config)
        throws ConfigurationException
    {
        reconfigure = true;
    }

    public void recontextualize(Context context)
        throws ContextException
    {
        recontextualize = true;
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
