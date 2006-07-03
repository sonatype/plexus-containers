package org.codehaus.plexus.component.repository;

import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ComponentProfile
{
    /** Component Factory. */
    private ComponentFactory componentFactory;

    /** Lifecycle Handler. */
    private LifecycleHandler lifecycleHandler;

    /** Component Manager. */
    private ComponentManager componentManager;    
    
    /** Component Composer. */
    private ComponentComposer componentComposer;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public ComponentFactory getComponentFactory()
    {
        return componentFactory;
    }

    public void setComponentFactory( ComponentFactory componentFactory )
    {
        this.componentFactory = componentFactory;
    }

    public LifecycleHandler getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    public void setLifecycleHandler( LifecycleHandler lifecycleHandler )
    {
        this.lifecycleHandler = lifecycleHandler;
    }

    public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    public void setComponentManager( ComponentManager componentManager )
    {
        this.componentManager = componentManager;
    }

    public ComponentComposer getComponentComposer()
    {
        return componentComposer;
    }
    
    public void setComponentComposer( ComponentComposer componentComposer )
    {
        this.componentComposer = componentComposer;
    }

}
