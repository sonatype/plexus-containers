package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.personality.plexus.PlexusLifecycleHandler;

import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultLifecycleHandlerManager
    implements LifecycleHandlerManager
{
    private List lifecycleHandlers = null;

    private String defaultLifecycleHandlerId = null;

    public void initialize( LoggerManager loggerManager,
                            Context context,
                            ComponentRepository componentRepository,
                            ComponentConfigurator componentConfigurator )
        throws Exception
    {
        for ( Iterator iterator = lifecycleHandlers.iterator(); iterator.hasNext(); )
        {
            LifecycleHandler lifecycleHandler = (LifecycleHandler) iterator.next();

            lifecycleHandler.addEntity( LifecycleHandler.LOGGER, loggerManager.getRootLogger() );

            lifecycleHandler.addEntity( LifecycleHandler.CONTEXT, context );

            lifecycleHandler.addEntity( LifecycleHandler.SERVICE_REPOSITORY, componentRepository );

            lifecycleHandler.addEntity( PlexusLifecycleHandler.COMPONENT_CONFIGURATOR, componentConfigurator );

            lifecycleHandler.initialize();
        }
    }

    public LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException
    {
        LifecycleHandler lifecycleHandler = null;

        for ( Iterator iterator = lifecycleHandlers.iterator(); iterator.hasNext(); )
        {
            lifecycleHandler = (LifecycleHandler) iterator.next();

            if ( id.equals( lifecycleHandler.getId() ) )
            {
                return lifecycleHandler;
            }
        }

        throw new UndefinedLifecycleHandlerException( "Specified lifecycle handler cannot be found: " + id );
    }

    public LifecycleHandler getDefaultLifecycleHandler()
        throws UndefinedLifecycleHandlerException
    {
        return getLifecycleHandler( defaultLifecycleHandlerId );
    }
}
