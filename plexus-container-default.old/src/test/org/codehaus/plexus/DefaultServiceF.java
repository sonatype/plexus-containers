package org.codehaus.plexus;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
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
public class DefaultServiceF
    extends AbstractLogEnabled
    implements ServiceF, Contextualizable, Serviceable, Configurable, Initializable, Startable
{
    boolean enableLogging;
    boolean contextualize;
    boolean service;
    boolean configure;
    boolean initialize;
    boolean start;
    boolean stop;

    String plexusHome;

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
        throws ConfigurationException
    {
        configure = true;

        plexusHome = configuration.getChild( "plexus-home" ).getValue();
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

    public String getPlexusHome()
    {
        return plexusHome;
    }
}
