package org.codehaus.plexus.lifecycle;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultLifecycleHandlerManager
    implements LifecycleHandlerManager
{
    private List lifecycleHandlers = null;

    private String defaultLifecycleHandlerId = null;

    public void initialize()
        throws Exception
    {
        for ( Iterator iterator = lifecycleHandlers.iterator(); iterator.hasNext(); )
        {
            LifecycleHandler lifecycleHandler = (LifecycleHandler) iterator.next();

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
