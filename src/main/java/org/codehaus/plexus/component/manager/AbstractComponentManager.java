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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.factory.UndefinedComponentFactoryException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.classworlds.ClassRealmAdapter;

import java.lang.reflect.Method;

public abstract class AbstractComponentManager
    implements ComponentManager, Cloneable
{
    protected MutablePlexusContainer container;

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

    public void setup( MutablePlexusContainer container,
                       LifecycleHandler lifecycleHandler,
                       ComponentDescriptor componentDescriptor )
    {
        this.container = container;

        this.lifecycleHandler = lifecycleHandler;

        this.componentDescriptor = componentDescriptor;
    }

    public void initialize()
    {
    }

    protected Object createComponentInstance( ClassRealm realm )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        Object component = createComponentInstance( componentDescriptor, realm );



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

    public MutablePlexusContainer getContainer()
    {
        return container;
    }

    public Logger getLogger()
    {
        return container.getLogger();
    }

    protected Object createComponentInstance( ComponentDescriptor componentDescriptor,
                                              ClassRealm realm )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        String componentFactoryId = componentDescriptor.getComponentFactory();

        ComponentFactory componentFactory;

        Object component;

        try
        {
            if ( componentFactoryId != null )
            {
                componentFactory = container.getComponentFactoryManager().findComponentFactory( componentFactoryId );
            }
            else
            {
                componentFactory = container.getComponentFactoryManager().getDefaultComponentFactory();
            }

            ClassRealm componentRealm;

            if ( realm == null )
            {
                componentRealm = container.getComponentRealm( componentDescriptor.getRealmId() );
            }
            else
            {
                componentRealm = realm;
            }

            try
            {
                component = componentFactory.newInstance( componentDescriptor, componentRealm, container );
            }
            catch ( AbstractMethodError e )
            {
                // ----------------------------------------------------------------------------
                // For compatibility with old ComponentFactories that use old ClassWorlds
                // ----------------------------------------------------------------------------

                org.codehaus.classworlds.ClassRealm cr = ClassRealmAdapter.getInstance( componentRealm );

                Method method;

                try
                {
                    method = componentFactory.getClass().getMethod( "newInstance", new Class[]{
                        ComponentDescriptor.class, org.codehaus.classworlds.ClassRealm.class, PlexusContainer.class} );

                    component = method.invoke( componentFactory, new Object[]{componentDescriptor, cr, container} );
                }
                catch ( Exception mnfe )
                {
                    throw new ComponentInstantiationException(
                        "Unable to create component as factory '" + componentFactoryId + "' could not be found", e );
                }
            }
        }
        catch ( UndefinedComponentFactoryException e )
        {
            throw new ComponentInstantiationException(
                "Unable to create component as factory '" + componentFactoryId + "' could not be found", e );
        }

        return component;
    }

    public Object getComponent()
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        return getComponent( null );
    }
}
