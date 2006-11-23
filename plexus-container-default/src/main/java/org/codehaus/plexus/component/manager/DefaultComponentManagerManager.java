package org.codehaus.plexus.component.manager;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.util.StringUtils;

import java.util.Collections;
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

    private Map componentManagersByComponentHashCode = Collections.synchronizedMap( new HashMap() );

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

        throw new UndefinedComponentManagerException( "Specified component manager cannot be found: " + id );
    }

    public ComponentManager createComponentManager( ComponentDescriptor descriptor, MutablePlexusContainer container,
                                                    String componentKey )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException
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

        if ( StringUtils.equals( componentKey, descriptor.getComponentKey() ) )
        {
            activeComponentManagers.put( descriptor.getComponentKey(), componentManager );
        }
        else
        {
            activeComponentManagers.put( componentKey, componentManager );
        }
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

    public void unassociateComponentWithComponentManager( Object component )
    {
        componentManagersByComponentHashCode.remove( new Integer( component.hashCode() ) );
    }
}
