package org.codehaus.plexus.service.repository.instance;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.configuration.DefaultConfiguration;
import org.codehaus.plexus.service.repository.ComponentHousing;
import org.codehaus.plexus.util.SweeperPool;

/**
 * Pools comnponents
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PoolableInstanceManager
    extends AbstractMultipleInstanceManager
{
    private SweeperPool pool;

	private int connections = 0;

    /**
     *
     */
    public PoolableInstanceManager()
    {
        super();
    }

    public void initialize() throws Exception
    {
        pool = newSeeperPool(getConfiguration(), null);
    }
    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        super.configure(configuration);
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#release(java.lang.Object)
     */
    public void release(Object component)
    {
    	ComponentHousing housing = removeHousing( component);
    	if( housing == null )
    	{
    		getLogger().warn("Component attempted to be returned to pool, but this object does no appear to be from this pool. Component class=" + component.getClass());
    		return;
    	}
        pool.put( housing );
		connections --;
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#dispose()
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
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#getComponent()
     */
    public Object getComponent() throws Exception
    {
        ComponentHousing housing = (ComponentHousing)pool.get();
        if( housing == null)
        {
        	housing = newHousingInstance();
        }
        putHousing(housing.getComponent(), housing);
        connections ++;
        return housing.getComponent();
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#getConnections()
     */
    public int getConnections()
    {
        return connections;
    }

    /**
      * Create a new ObjectPool based on the provided configurations. Default
      * hardcoded values are used if neither configurations have a value
      * for a particular setting.
      *
      *
      * @param config the custom configuration for the pool
      * @param defaultConfig the configuration used to fill out any gaps in
      * the custom configuration.
      * @return a new ObjectPool
      * @throws ConfigurationException
      */
    private SweeperPool newSeeperPool(Configuration config, Configuration defaultConfig)
        throws ConfigurationException
    {
    	if( config==null)
    		config = new DefaultConfiguration("");
        if (defaultConfig == null)
            defaultConfig = new DefaultConfiguration("");
        int sweepInterval =
            config.getChild("sweep-interval").getValueAsInteger(
                defaultConfig.getChild("sweep-interval").getValueAsInteger(5));
        int minCapacity =
            config.getChild("min-capacity").getValueAsInteger(
                defaultConfig.getChild("min-capacity").getValueAsInteger(3));
        int maxCapacity =
            config.getChild("max-capacity").getValueAsInteger(
                defaultConfig.getChild("max-capacity").getValueAsInteger(30));
        int triggerSize =
            config.getChild("trigger-size").getValueAsInteger(
                defaultConfig.getChild("trigger-size").getValueAsInteger(15));
        int initialCapacity =
            config.getChild("initial-capacity").getValueAsInteger(
                defaultConfig.getChild("initial-capacity").getValueAsInteger(10));
        return new SweeperPool(
            maxCapacity,
            minCapacity,
            initialCapacity,
            sweepInterval,
            triggerSize);
    }

    class ComponentPool extends SweeperPool
    {

        /**
         * @param maxSize
         * @param minSize
         * @param intialCapacity
         * @param sweepInterval
         * @param triggerSize
         */
        public ComponentPool(
            int maxSize,
            int minSize,
            int intialCapacity,
            int sweepInterval,
            int triggerSize)
        {
            super(maxSize, minSize, intialCapacity, sweepInterval, triggerSize);
        }

        /**
         * @see org.codehaus.plexus.util.SweeperPool#objectDisposed(java.lang.Object)
         */
        public void objectDisposed(Object obj)
        {
               endComponentLifecycle((ComponentHousing) obj);
        }

    }
}
