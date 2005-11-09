package org.codehaus.plexus.component.manager;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

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

    public ComponentManager createComponentManager( ComponentDescriptor descriptor, PlexusContainer container )
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

    public void unassociateComponentWithComponentManager( Object component )
    {
        componentManagersByComponentHashCode.remove( new Integer( component.hashCode() ) );
    }
}
