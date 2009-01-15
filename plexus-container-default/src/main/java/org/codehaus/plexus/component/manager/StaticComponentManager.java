package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.MutablePlexusContainer;

public class StaticComponentManager<T> implements ComponentManager<T>
{
    private final MutablePlexusContainer container;
    private T instance;
    private final ComponentDescriptor<T> descriptor;
    private boolean disposed;

    public StaticComponentManager( MutablePlexusContainer container, T instance, String role, String roleHint)
    {
        this.container = container;
        this.instance = instance;

        descriptor = new ComponentDescriptor<T>();
        descriptor.setRole( role );
        descriptor.setRoleHint( roleHint );
        descriptor.setImplementationClass( (Class<? extends T>) instance.getClass() );
    }

    public LifecycleHandler getLifecycleHandler()
    {
        return null;
    }

    public synchronized T getComponent() throws ComponentInstantiationException, ComponentLifecycleException
    {
        if (disposed)
        {
            throw new ComponentLifecycleException("This ComponentManager has already been destroyed");
        }
        return instance;
    }

    public synchronized void dispose() throws ComponentLifecycleException
    {
        disposed = true;
        instance = null;
    }

    public synchronized void release( Object component ) throws ComponentLifecycleException
    {
        instance = null;
    }

    public ComponentDescriptor<T> getComponentDescriptor()
    {
        return descriptor;
    }

    public MutablePlexusContainer getContainer()
    {
        return container;
    }

    public synchronized String toString()
    {
        return "StaticComponentManager[" + instance == null ? getComponentDescriptor().getImplementationClass().getName() : instance + "]";
    }

}
