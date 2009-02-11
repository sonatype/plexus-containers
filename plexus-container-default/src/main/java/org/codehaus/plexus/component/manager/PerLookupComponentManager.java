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

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ReferenceMap;
import static com.google.common.base.ReferenceType.WEAK;

/**
 * Creates a new component manager for every lookup
 *
 * @author Jason van Zyl
 *
 * @version $Id$
 */
public class PerLookupComponentManager<T>
    extends AbstractComponentManager<T>
{
    private boolean disposed;
    private final Map<T, T> instances = new ReferenceMap<T, T>( WEAK, WEAK);
    
    public PerLookupComponentManager( MutablePlexusContainer container,
                                      LifecycleHandler lifecycleHandler,
                                      ComponentDescriptor<T> componentDescriptor )
    {
        super( container, lifecycleHandler, componentDescriptor );
    }

    public void dispose() throws ComponentLifecycleException
    {
        Set<T> instances;
        synchronized (this) {
            disposed = true;
            instances = this.instances.keySet();
            this.instances.clear();
        }

        ComponentLifecycleException componentLifecycleException = null;
        for ( T instance : instances )
        {
            try
            {
                // do not call destroyInstance inside of a synchronized block because
                // destroyInstance results in several callbacks to user code which
                // could result in a dead lock
                destroyInstance( instance );
            }
            catch ( ComponentLifecycleException e )
            {
                if (componentLifecycleException == null) {
                    componentLifecycleException = e;
                }
            }
        }

        if (componentLifecycleException == null) {
            throw componentLifecycleException;
        }
    }

    public T getComponent( ) throws ComponentInstantiationException, ComponentLifecycleException
    {
        synchronized (this) {
            if (disposed)
            {
                throw new ComponentLifecycleException("This ComponentManager has already been destroyed");
            }
        }

        // do not call createInstance inside of a synchronized block because
        // createInstance results in several callbacks to user code which
        // could result in a dead lock
        T instance = createInstance();

        synchronized (this) {
            // if this manager has been destroyed during create, destroy newly
            // created component
            if (disposed)
            {
                try
                {
                    destroyInstance( instance );
                }
                catch ( ComponentLifecycleException e )
                {
                    // todo: log ignored exception
                }

                throw new ComponentLifecycleException("This ComponentManager has already been destroyed");
            }

            instances.put(instance, instance);
        }

        return instance;
    }

    public void release( Object component ) throws ComponentLifecycleException
    {
        T instance;
        synchronized (this) {
            instance = instances.remove(component);
        }

        // do not call destroyInstance inside of a synchronized block because
        // destroyInstance results in several callbacks to user code which
        // could result in a dead lock
        if (instance != null ) {
            destroyInstance( component );
        }
    }
}
