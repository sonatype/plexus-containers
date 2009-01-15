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
import org.codehaus.plexus.component.builder.ComponentBuilder;
import org.codehaus.plexus.component.builder.XBeanComponentBuilder;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public abstract class AbstractComponentManager<T> implements ComponentManager<T>
{
    private final MutablePlexusContainer container;

    private final ComponentDescriptor<T> componentDescriptor;

    private final ComponentBuilder<T> builder = new XBeanComponentBuilder<T>( this );

    private final LifecycleHandler lifecycleHandler;

    public AbstractComponentManager( MutablePlexusContainer container,
                                     LifecycleHandler lifecycleHandler,
                                     ComponentDescriptor<T> componentDescriptor )
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
    }

    public ComponentDescriptor<T> getComponentDescriptor()
    {
        return componentDescriptor;
    }

    public LifecycleHandler getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    protected T createInstance() throws ComponentInstantiationException, ComponentLifecycleException
    {
        return builder.build( componentDescriptor, componentDescriptor.getRealm(), null );
    }

    protected void destroyInstance( Object component ) throws ComponentLifecycleException
    {
        try
        {
            lifecycleHandler.end( component, this, componentDescriptor.getRealm() );
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
}
