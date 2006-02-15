package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;

public class ContainerComponentManager extends AbstractComponentManager
{
    public void dispose()
        throws ComponentLifecycleException
    {
    }

    public void release( Object component )
    {
    }

    public Object getComponent()
        throws ComponentLifecycleException
    {
        return getContainer();
    }
}
