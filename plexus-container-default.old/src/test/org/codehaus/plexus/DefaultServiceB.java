package org.codehaus.plexus;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/** This service implements all the start phases:
 *
 *  LogEnabled
 *  Plexus Contexualize
 *  Plexus Serviceable
 *  Configurable
 *  Initializable
 *  Startable
 *
 */
public class DefaultServiceB
    extends AbstractLogEnabled
    implements ServiceB, Contextualizable, Serviceable, Configurable, Initializable, Startable
{
    boolean enableLogging;
    boolean contextualize;
    boolean service;
    boolean configure;
    boolean initialize;
    boolean start;
    boolean stop;

    ClassLoader classLoader;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void enableLogging( Logger logger )
    {
        enableLogging = true;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        contextualize = true;

        classLoader = (ClassLoader) context.get( "common.classloader" );
    }

    public void service( ServiceManager serviceManager )
        throws ServiceException
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

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }
}

