package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentManagerManager
    implements ComponentManagerManager
{
    private Map activeComponentManagers = new HashMap();

    private List componentManagers = null;

    private String defaultComponentManagerId = null;

    private LifecycleHandlerManager lifecycleHandlerManager;

    private Map componentManagersByComponentHashCode = new HashMap();

    public void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.lifecycleHandlerManager = lifecycleHandlerManager;
    }

    private ComponentManager copyComponentManager( String id )
        throws UndefinedComponentManagerException
    {
        ComponentManager componentManager = null;

        for ( Iterator iterator = componentManagers.iterator(); iterator.hasNext(); )
        {
            componentManager = (ComponentManager) iterator.next();

            if ( id.equals( componentManager.getId() ) )
            {
                return componentManager.copy();
            }
        }

        throw new UndefinedComponentManagerException( "Specified lifecycle handler cannot be found: " + id );
    }

    public ComponentManager createComponentManager( ComponentDescriptor descriptor, PlexusContainer container )
        throws Exception
    {
        String componentManagerId = descriptor.getInstantiationStrategy();

        ComponentManager componentManager;

        if ( componentManagerId == null )
        {
            componentManagerId = defaultComponentManagerId;
        }

        componentManager = copyComponentManager( componentManagerId );

        componentManager.setup( container, findLifecycleHandler( descriptor ), descriptor );

        componentManager.initialize();

        activeComponentManagers.put( descriptor.getComponentKey(), componentManager );

        return componentManager;
    }

    public ComponentManager findComponentManagerByComponentInstance( Object component )
    {
        return (ComponentManager) componentManagersByComponentHashCode.get( new Integer( component.hashCode() ) );
    }

    public ComponentManager findComponentManagerByComponentKey( String componentKey )
    {
        ComponentManager componentManager = (ComponentManager) activeComponentManagers.get( componentKey );

        return componentManager;
    }

    // ----------------------------------------------------------------------
    // Lifecycle handler manager handling
    // ----------------------------------------------------------------------

    private LifecycleHandler findLifecycleHandler( ComponentDescriptor descriptor )
        throws UndefinedLifecycleHandlerException
    {
        String lifecycleHandlerId = descriptor.getLifecycleHandler();

        LifecycleHandler lifecycleHandler;

        if ( lifecycleHandlerId == null )
        {
            lifecycleHandler = lifecycleHandlerManager.getDefaultLifecycleHandler();
        }
        else
        {
            lifecycleHandler = lifecycleHandlerManager.getLifecycleHandler( lifecycleHandlerId );
        }

        return lifecycleHandler;
    }

    // ----------------------------------------------------------------------
    // Component manager handling
    // ----------------------------------------------------------------------

    public Map getComponentManagers()
    {
        return activeComponentManagers;
    }

    public void associateComponentWithComponentManager( Object component, ComponentManager componentManager )
    {
        componentManagersByComponentHashCode.put( new Integer( component.hashCode() ), componentManager );
    }
}


