package org.codehaus.plexus.service.repository.instance;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;
import org.codehaus.plexus.service.repository.ComponentManager;

/**
 * Base InstanceManager
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractInstanceManager
    implements InstanceManager
{
    /** ClassLoader used to load the component */
    private ClassLoader classLoader;

    /** Component configuration */
    private Configuration configuration;

    /** Component implementation */
    private String implementation;

    /** ComponantManager which handles the component */
    private ComponentManager componentManager;

    /** Lifecycle handler for this component type */
    private LifecycleHandler lifecycleHandler;

    private Logger logger;

    /**
     *
     */
    public AbstractInstanceManager()
    {
        super();
    }

    /**
     * This currently does nothing. Subclasses should still call this if they override this method
     * as it may doing something useful in future.
     *
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#initialize()
     */
    public void initialize()
        throws Exception
    {
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /**
     * make sure to call this if overriding
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        this.configuration = configuration;
        implementation = configuration.getChild( "implementation" ).getValue();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( Configuration configuration )
    {
        this.configuration = configuration;
    }

    public String getImplementation()
    {
        return implementation;
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#setLifecycleHandler(org.codehaus.plexus.lifecycle.LifecycleHandler)
     */
    public void setLifecycleHandler( LifecycleHandler handler )
    {
        this.lifecycleHandler = handler;
    }

    public void setComponentImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    public void setComponentManager( ComponentManager componentManager )
    {
        this.componentManager = componentManager;
    }

    protected LifecycleHandler getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    /**
     * @return
     */
    protected Logger getLogger()
    {
        return logger;
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    protected void startComponentLifecycle( ComponentHousing housing )
    {
        try
        {
            getLifecycleHandler().endLifecycle( housing );
        }
        catch ( Exception e )
        {
            getLogger().error(
                "Cannot start component lifecycle with role : "
                + getComponentManager().getComponentDescriptor().getRole(),
                e );
        }
    }

    /** End a component's lifecycle.
     *
     */
    protected void endComponentLifecycle( ComponentHousing housing )
    {
        try
        {
            getLifecycleHandler().endLifecycle( housing );
        }
        catch ( Exception e )
        {
            getLogger().error(
                "Cannot start component lifecycle with role : "
                + getComponentManager().getComponentDescriptor().getRole(),
                e );
        }
    }

    /**
     * Create a new Component  instance,and start it's lifecycle
     */
    protected ComponentHousing newHousingInstance()
        throws Exception
    {
        ComponentHousing housing = new ComponentHousing();
        housing.setComponentManager( getComponentManager() );
        housing.setComponent( getClassLoader().loadClass( getImplementation() ).newInstance() );
        getLifecycleHandler().startLifecycle( housing );
        return housing;
    }

    public abstract void release( Object component );


}
