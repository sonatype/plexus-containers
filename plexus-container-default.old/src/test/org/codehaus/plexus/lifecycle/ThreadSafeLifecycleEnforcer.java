package org.codehaus.plexus.lifecycle;

import junit.framework.AssertionFailedError;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * @author kaz
 */
public class ThreadSafeLifecycleEnforcer
    implements
        LifecycleEnforcer,
        ThreadSafe,
        LogEnabled,
        Contextualizable,
        Serviceable,
        Configurable,
        //Parameterizable,
        Initializable,
        Startable,
        Disposable
{
    private boolean logged;
    private boolean contextualized;
    private boolean serviced;
    private boolean configured;
    //private boolean parameterized;
    private boolean initialized;
    private boolean started;
    private boolean stopped;
    private boolean disposed;

    // NOTE: Do not change the AssertionFailedErrors to any type of 
    // Exception as they are caught throughout Plexus and thus will
    // not surface during the test.

    public void enableLogging(Logger log)
    {
        if (logged
            || contextualized
            || serviced
            || configured
         // || parameterized
            || initialized
            || started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        logged = true;
    }

    public void contextualize(Context arg0) throws ContextException
    {
        if (!logged
            || contextualized
            || serviced
            || configured
         // || parameterized
            || initialized
            || started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        contextualized = true;
    }

    public void service(ServiceManager broker) throws ServiceException
    {
        if (!logged
            || !contextualized
            || serviced
            || configured
         // || parameterized
            || initialized
            || started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        serviced = true;
    }

    public void configure(Configuration arg0) throws ConfigurationException
    {
        if (!logged
            || !contextualized
            || !serviced
            || configured
         // || parameterized
            || initialized
            || started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        configured = true;
    }

    public void parameterize(Parameters params)
    {
        if (!logged
            || !contextualized
            || !serviced
            || !configured
         // || parameterized
            || initialized
            || started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        //parameterized = true;
    }

    public void initialize() throws Exception
    {
        if (!logged
            || !contextualized
            || !serviced
            || !configured
         // || !parameterized
            || initialized
            || started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        initialized = true;
    }

    public void start() throws Exception
    {
        if (!logged
            || !contextualized
            || !serviced
            || !configured
         // || !parameterized
            || !initialized
            || started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        started = true;
    }

    public void stop() throws Exception
    {
        if (!logged
            || !contextualized
            || !serviced
            || !configured
         // || !parameterized
            || !initialized
            || !started
            || stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        stopped = true;
    }

    public void dispose()
    {
        if (!logged
            || !contextualized
            || !serviced
            || !configured
         // || !parameterized
            || !initialized
            || !started
            || !stopped
            || disposed)
        {
            throw new AssertionFailedError("Lifecyle improperly invoked");
        }
        disposed = true;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isLogEnabled()
     */
    public boolean isLogged()
    {
        return logged;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isContextualized()
     */
    public boolean isContextualized()
    {
        return contextualized;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isServiced()
     */
    public boolean isServiced()
    {
        return serviced;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isConfigured()
     */
    public boolean isConfigured()
    {
        return configured;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isInitialized()
     */
    public boolean isInitialized()
    {
        return initialized;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isStarted()
     */
    public boolean isStarted()
    {
        return started;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isStopped()
     */
    public boolean isStopped()
    {
        return stopped;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.lifecycle.LifecycleEnforcer#isDisposed()
     */
    public boolean isDisposed()
    {
        return disposed;
    }
}
