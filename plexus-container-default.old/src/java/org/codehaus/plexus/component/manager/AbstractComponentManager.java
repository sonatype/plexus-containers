package org.codehaus.plexus.component.manager;


import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

public abstract class AbstractComponentManager
    implements ComponentManager, Cloneable
{
    private PlexusContainer container;

    private ComponentDescriptor componentDescriptor;

    private LifecycleHandler lifecycleHandler;

    private int connections;

    private String id = null;

    public ComponentManager copy()
    {
        try
        {
            ComponentManager componentManager = (ComponentManager) this.clone();

            // This could serve as a location to initialize component managers.
            // Was thinking here might be a good place to create the instance manager.
            // The place to customize the prototypical component manager.

            return componentManager;
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

    public void setup( PlexusContainer container, LifecycleHandler lifecycleHandler, ComponentDescriptor componentDescriptor )
        throws Exception
    {
        this.container = container;
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
        Object component = container.getClassLoader().loadClass( componentDescriptor.getImplementation() ).newInstance();

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
            container.getLogger().error( "Cannot start component lifecycle with role: " + getComponentDescriptor().getRole(), e );
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
            container.getLogger().error( "Cannot suspend component with role: " + getComponentDescriptor().getRole(), e );
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
            container.getLogger().error( "Cannot resume component with role: " + getComponentDescriptor().getRole(), e );
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
            container.getLogger().error( "Cannot end component lifecycle with role: " + getComponentDescriptor().getRole(), e );
        }
    }

    public PlexusContainer getContainer()
    {
        return container;
    }

    public Logger getLogger()
    {
        return container.getLogger();
    }
}
