package org.codehaus.plexus.component.manager;


import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

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
    {
        this.container = container;
        this.lifecycleHandler = lifecycleHandler;
        this.componentDescriptor = componentDescriptor;
    }

    public void initialize()
    {
    }

    protected Object createComponentInstance()
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        Object component = container.createComponentInstance( componentDescriptor );

        startComponentLifecycle( component );

        return component;
    }

    protected void startComponentLifecycle( Object component )
        throws ComponentLifecycleException
    {
        try
        {
            getLifecycleHandler().start( component, this );
        }
        catch ( PhaseExecutionException e )
        {
            throw new ComponentLifecycleException( "Error starting component", e );
        }
    }

    public void suspend( Object component )
        throws ComponentLifecycleException
    {
        try
        {
            getLifecycleHandler().suspend( component, this );
        }
        catch ( PhaseExecutionException e )
        {
            throw new ComponentLifecycleException( "Error suspending component", e );
        }
    }

    public void resume( Object component )
        throws ComponentLifecycleException
    {
        try
        {
            getLifecycleHandler().resume( component, this );
        }
        catch ( PhaseExecutionException e )
        {
            throw new ComponentLifecycleException( "Error suspending component", e );
        }
    }

    protected void endComponentLifecycle( Object component )
        throws ComponentLifecycleException
    {
        try
        {
            getLifecycleHandler().end( component, this );
        }
        catch ( PhaseExecutionException e )
        {
            throw new ComponentLifecycleException( "Error ending component lifecycle", e );
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
