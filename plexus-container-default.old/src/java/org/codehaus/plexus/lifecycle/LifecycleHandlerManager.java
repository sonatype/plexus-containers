package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.logging.LoggerManager;
import org.apache.avalon.framework.context.Context;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface LifecycleHandlerManager
{
    void initialize( LoggerManager lm, Context context, ComponentRepository cr )
        throws Exception;

    LifecycleHandler getDefaultLifecycleHandler()
        throws UndefinedLifecycleHandlerException;

    LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException;
}
