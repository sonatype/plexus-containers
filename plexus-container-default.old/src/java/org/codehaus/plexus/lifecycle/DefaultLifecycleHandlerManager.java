package org.codehaus.plexus.lifecycle;

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
{
    private List lifecycleHandlers;

    private String defaultLifecycleHandler;

    public LifecycleHandler getLifecycleHandler( String id )
    {
        LifecycleHandler lifecycleHandler = null;

        for ( Iterator iterator = lifecycleHandlers.iterator(); iterator.hasNext(); )
        {
            lifecycleHandler = (LifecycleHandler) iterator.next();

            if ( id.equals( lifecycleHandler.getId() ) )
            {
                break;
            }
        }

        return lifecycleHandler;
    }

    public String getDefaultLifecycleHandler()
    {
        return defaultLifecycleHandler;
    }
}
