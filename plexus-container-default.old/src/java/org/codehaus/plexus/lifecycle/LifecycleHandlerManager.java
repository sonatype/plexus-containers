package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.LoggerManager;


/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface LifecycleHandlerManager
{
    void initialize( LoggerManager loggerManager,
                     Context context,
                     ComponentRepository componentRepository,
                     ComponentConfigurator componentConfigurator )
        throws Exception;

    LifecycleHandler getDefaultLifecycleHandler()
        throws UndefinedLifecycleHandlerException;

    LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException;
}
