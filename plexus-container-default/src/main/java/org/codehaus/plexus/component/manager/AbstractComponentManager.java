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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractComponentManager<T>
    implements ComponentManager<T>
{
    protected final MutablePlexusContainer container;

    private final ClassRealm realm;

    protected final ComponentDescriptor<T> componentDescriptor;

    private final Class<? extends T> type;

    private final String role;
    
    private final String roleHint;

    protected final ComponentBuilder<T> builder = new XBeanComponentBuilder<T>(this);


    private final LifecycleHandler lifecycleHandler;

    /**
     * Contains a mapping from singleton instances to the realms
     * they were used to configure with. This realm will be used to
     * call all lifecycle methods.
     * @return a synchronized map, make sure to synchronize the map when iterating.
     */
    protected final Map<Object, ClassRealm> componentContextRealms = Collections.synchronizedMap(new HashMap<Object, ClassRealm>());

    private int connections;

    private long startId;

    public AbstractComponentManager( MutablePlexusContainer container,
                       LifecycleHandler lifecycleHandler,
                       ComponentDescriptor<T> componentDescriptor,
                       String role,
                       String roleHint)
    {
        if ( container == null )
        {
            throw new NullPointerException( "container is null" );
        }
        this.container = container;

        if ( lifecycleHandler == null )
        {
            throw new NullPointerException( "lifecycleHandler is null" );
        }
        this.lifecycleHandler = lifecycleHandler;

        if ( componentDescriptor == null )
        {
            throw new NullPointerException( "componentDescriptor is null" );
        }
        this.componentDescriptor = componentDescriptor;

        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }
        this.role = role;

        if ( roleHint == null )
        {
            throw new NullPointerException( "roleHint is null" );
        }
        this.roleHint = roleHint;

        this.realm = componentDescriptor.getRealm();

        this.type = componentDescriptor.getImplementationClass();
    }

    public ComponentDescriptor<T> getComponentDescriptor()
    {
        return componentDescriptor;
    }

    public Class<? extends T> getType()
    {
        return type;
    }

    public ClassRealm getRealm()
    {
        return realm;
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

    public void start( Object component ) throws PhaseExecutionException
    {
        startId = NEXT_START_ID.getAndIncrement();
        getLifecycleHandler().start( component,  this, componentDescriptor.getRealm() );
    }

    /**
     * @deprecated for internal use only.. will be removed
     */
    public long getStartId()
    {
        return startId;
    }

    protected T createComponentInstance()
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        return builder.build(componentDescriptor, realm, new AbstractComponentBuildListener() {
            public void componentCreated( ComponentDescriptor<?> componentDescriptor, Object component, ClassRealm realm) {
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
