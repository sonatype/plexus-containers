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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.builder.AbstractComponentBuildListener;
import org.codehaus.plexus.component.builder.ComponentBuilder;
import org.codehaus.plexus.component.builder.XBeanComponentBuilder;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public abstract class AbstractComponentManager
    implements ComponentManager, Cloneable
{
    protected MutablePlexusContainer container;

    protected ComponentDescriptor componentDescriptor;

    private String role;
    
    private String roleHint;

    protected ComponentBuilder builder;

    private LifecycleHandler lifecycleHandler;

    /**
     * Contains a mapping from singleton instances to the realms
     * they were used to configure with. This realm will be used to
     * call all lifecycle methods.
     * @return a synchronized map, make sure to synchronize the map when iterating.
     */
    protected final Map<Object, ClassRealm> componentContextRealms = Collections.synchronizedMap(new HashMap<Object, ClassRealm>());

    private int connections;

    protected AbstractComponentManager() {
        builder = createComponentBuilder();
    }

    protected ComponentBuilder createComponentBuilder() {
        return new XBeanComponentBuilder(this);
        // return new DefaultComponentBuilder(this);
    }

    public ComponentManager copy()
    {
        try
        {
            // todo replace with a copy constructor... clone sucks
            AbstractComponentManager componentManager = (AbstractComponentManager) clone();
            componentManager.builder = componentManager.createComponentBuilder();
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

    public String getRole()
    {
        return role;
    }

    public String getRoleHint()
    {
        return roleHint;
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
                       ComponentDescriptor componentDescriptor,
                       String role,
                       String roleHint)
    {
        this.container = container;

        this.lifecycleHandler = lifecycleHandler;

        this.componentDescriptor = componentDescriptor;

        this.role = role;

        this.roleHint = roleHint;
    }

    public void initialize()
    {
    }

    protected Object createComponentInstance( ClassRealm realm )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        return builder.build(componentDescriptor, realm, new AbstractComponentBuildListener() {
            public void componentCreated(ComponentDescriptor componentDescriptor, Object component, ClassRealm realm) {
                componentContextRealms.put( component, realm );
            }
        });
    }

    protected void endComponentLifecycle( Object component )
        throws ComponentLifecycleException
    {
        ClassRealm contextRealm = componentContextRealms.remove( component );
        if ( contextRealm == null )
        {
            contextRealm = container.getLookupRealm( component );
        }

        try
        {
            getLifecycleHandler().end( component, this, contextRealm );
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

    public Object getComponent()
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        return getComponent( container.getLookupRealm() );
    }

    public void dissociateComponentRealm( ClassRealm realm )
        throws ComponentLifecycleException
    {
        synchronized ( componentContextRealms )
        {
            for ( Iterator<Entry<Object, ClassRealm>> iterator = componentContextRealms.entrySet().iterator(); iterator.hasNext(); )
            {
                Entry<Object, ClassRealm> entry = iterator.next();
                ClassRealm componentRealm = entry.getValue();

                if ( componentRealm.getId().equals( realm.getId() ) )
                {
                    iterator.remove();
                }
            }
        }
    }
}
