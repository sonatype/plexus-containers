package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.apache.avalon.framework.context.Context;

import java.util.List;
import java.util.Iterator;

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
    private List lifecycleHandlers;

    private String defaultLifecycleHandlerId;

    public void initialize( LoggerManager loggerManager, Context context, ComponentRepository componentRepository )
        throws Exception
    {
        for ( Iterator iterator = lifecycleHandlers.iterator(); iterator.hasNext(); )
        {
            LifecycleHandler lifecycleHandler = (LifecycleHandler) iterator.next();

            lifecycleHandler.addEntity( LifecycleHandler.LOGGER, loggerManager.getRootLogger() );

            lifecycleHandler.addEntity( LifecycleHandler.CONTEXT, context );

            lifecycleHandler.addEntity( LifecycleHandler.SERVICE_REPOSITORY, componentRepository );

            lifecycleHandler.initialize();
        }
    }

    public LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException
    {
        LifecycleHandler lifecycleHandler = null;

        System.out.println( "lifecycleHandlers = " + lifecycleHandlers );

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
