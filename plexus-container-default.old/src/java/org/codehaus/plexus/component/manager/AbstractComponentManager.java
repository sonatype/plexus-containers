package org.codehaus.plexus.component.manager;


import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.logging.Logger;

public abstract class AbstractComponentManager
    implements ComponentManager, Cloneable
{
    private ComponentDescriptor componentDescriptor;

    private ClassLoader classLoader;

    private LifecycleHandler lifecycleHandler;

    private Logger logger;

    private int connections;

    private String id;

    public AbstractComponentManager()
    {
    }

    public ComponentManager copy()
    {
        try
        {
            return (ComponentManager) this.clone();
        }
        catch ( CloneNotSupportedException e )
        {
        }

        return null;
    }

    public ComponentDescriptor getComponentDescriptor()
    {
        return componentDescriptor;
    }

    public String getId()
    {
        return id;
    }

    public LifecycleHandler getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

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

    public void setup( Logger logger,
                       ClassLoader classLoader,
                       LifecycleHandler lifecycleHandler,
                       ComponentDescriptor componentDescriptor )
        throws Exception
    {
        this.logger = logger;
        this.classLoader = classLoader;
        this.lifecycleHandler = lifecycleHandler;
        this.componentDescriptor = componentDescriptor;
    }

    public void initialize()
        throws Exception
    {
    }

    protected Object createComponentInstance()
        throws Exception
    {
        Object component = getClassLoader().loadClass( componentDescriptor.getImplementation() ).newInstance();

        startComponentLifecycle( component );

        return component;
    }

    protected void startComponentLifecycle( Object component )
    {
        try
        {
            getLifecycleHandler().start( component, this );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot start component lifecycle with role : " + getComponentDescriptor().getRole(), e );
        }
    }

    public void suspend( Object component )
    {
        try
        {
            getLifecycleHandler().suspend( component, this );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot suspend component with role : " + getComponentDescriptor().getRole(), e );
        }
    }

    public void resume( Object component )
    {
        try
        {
            getLifecycleHandler().resume( component, this );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot suspend component with role : " + getComponentDescriptor().getRole(), e );
        }
    }

    protected void endComponentLifecycle( Object component )
    {
        try
        {
            getLifecycleHandler().end( component, this );
        }
        catch ( Exception e )
        {
            getLogger().error( "Cannot start component lifecycle with role : " + getComponentDescriptor().getRole(), e );
        }
    }
}
