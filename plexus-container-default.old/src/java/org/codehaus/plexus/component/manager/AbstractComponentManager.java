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
        Object component = container.createComponentInstance( componentDescriptor );

        startComponentLifecycle( component );

        return component;
    }

    protected void startComponentLifecycle( Object component )
        throws Exception
    {
        getLifecycleHandler().start( component, this );
    }

    public void suspend( Object component )
        throws Exception
    {
        getLifecycleHandler().suspend( component, this );
    }

    public void resume( Object component )
        throws Exception
    {
        getLifecycleHandler().resume( component, this );
    }

    protected void endComponentLifecycle( Object component )
        throws Exception
    {
        getLifecycleHandler().end( component, this );

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
