package org.codehaus.plexus.lifecycle;




/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface LifecycleHandlerManager
{
    void initialize();

    LifecycleHandler getDefaultLifecycleHandler()
        throws UndefinedLifecycleHandlerException;

    LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException;
}
