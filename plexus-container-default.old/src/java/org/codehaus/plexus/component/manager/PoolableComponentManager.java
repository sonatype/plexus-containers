package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.util.SweeperPool;

/**
 * Pools comnponents
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PoolableComponentManager
    extends AbstractComponentManager
{
    private SweeperPool pool;

    private int maxCapacity = 30;
    private int minCapacity = 3;
    private int initialCapacity = 10;
    private int sweepInterval = 5;
    private int triggerSize = 15;

    public PoolableComponentManager()
    {
        super();
    }

    public void initialize()
        throws Exception
    {
        super.initialize();
        pool = new SweeperPool( maxCapacity, minCapacity, initialCapacity, sweepInterval, triggerSize );
    }

    /**
     * @see ComponentManager#release(Object)
     */
    public void release( Object component )
    {
        pool.put( component );
    }

    /**
     * @see org.codehaus.plexus.component.manager.ComponentManager#dispose()
     */
    public void dispose()
    {
        //@todo really need to wait for all components to be returned.
        //however blocking on this call may prevent plexus servicing
        //other requests and hence prevent cleanup. Have to look
        //at this. For now just assume all connections have been
        //released.
        pool.dispose();

    }

    /**
     * @see org.codehaus.plexus.component.manager.ComponentManager#getComponent()
     */
    public Object getComponent()
        throws Exception
    {
        Object component = pool.get();

        if ( component == null )
        {
            component = createComponentInstance();
        }

        return component;
    }
}
