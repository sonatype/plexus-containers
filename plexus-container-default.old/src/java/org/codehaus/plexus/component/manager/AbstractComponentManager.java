package org.codehaus.plexus.component.manager;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * Base InstanceManager
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractComponentManager
    implements ComponentManager
{
    /** Component Descriptor. */
    private ComponentDescriptor componentDescriptor;

    /** ClassLoader used to load the component */
    private ClassLoader classLoader;

    /** Component configuration */
    private Configuration configuration;

    /** Component implementation */
    private String implementation;

    /** Lifecycle handler for this component type */
    private LifecycleHandler lifecycleHandler;

    /** Logger. */
    private Logger logger;

    /** Client connections. */
    private int connections;

    public AbstractComponentManager()
    {
        super();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public ComponentDescriptor getComponentDescriptor()
    {
        return componentDescriptor;
    }

    public void setComponentDescriptor( ComponentDescriptor componentDescriptor )
    {
        this.componentDescriptor = componentDescriptor;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public String getImplementation()
    {
        return implementation;
    }

    public void setComponentImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    /**
     * @see ComponentManager#setLifecycleHandler(org.codehaus.plexus.lifecycle.LifecycleHandler)
     */
    public void setLifecycleHandler( LifecycleHandler handler )
    {
        this.lifecycleHandler = handler;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public LifecycleHandler getLifecycleHandler()
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

    protected void incrementConnectionCount()
    {
        connections++;
    }

    protected void decrementConnectionCount()
    {
        connections--;
    }

    protected boolean connected()
    {
        return connections > 0;
    }

    public int getConnections()
    {
        return connections;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /**
     * This currently does nothing. Subclasses should still call this if they override this method
     * as it may doing something useful in future.
     *
     * @see org.codehaus.plexus.component.manager.ComponentManager#initialize()
     */
    public void initialize()
        throws Exception
    {
        implementation = getComponentDescriptor().getImplementation();
    }

    /**
     * make sure to call this if overriding
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        this.configuration = configuration;
    }


    /**
     * @see org.codehaus.plexus.component.manager.ComponentManager#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    protected Object createComponentInstance()
        throws Exception
    {
        Object component = getClassLoader().loadClass( getImplementation() ).newInstance();

        startComponentLifecycle( component );

        return component;
    }

    protected void startComponentLifecycle( Object component )
    {
        try
        {
            getLifecycleHandler().startLifecycle( component, this );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot start component lifecycle with role : " + getComponentDescriptor().getRole(), e );
        }
    }

    /** End a component's lifecycle.
     *
     */
    protected void endComponentLifecycle( Object component )
    {
        try
        {
            getLifecycleHandler().endLifecycle( component, this );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot start component lifecycle with role : " + getComponentDescriptor().getRole(), e );
        }
    }

    public abstract void release( Object component );

}
