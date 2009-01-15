package org.codehaus.plexus.component.manager;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/**
 * This ensures a component is only used as a singleton, and is only shutdown when the container
 * shuts down.
 * 
 * @author Jason van Zyl
 */
public class SingletonComponentManager<T> extends AbstractComponentManager<T>
{
    private boolean disposed;
    private Future<T> singletonFuture;

    public SingletonComponentManager( MutablePlexusContainer container,
                                      LifecycleHandler lifecycleHandler,
                                      ComponentDescriptor<T> componentDescriptor )
    {
        super( container, lifecycleHandler, componentDescriptor );
    }

    public synchronized void dispose() throws ComponentLifecycleException
    {
        T singleton;
        synchronized ( this )
        {
            disposed = true;
            singleton = getExistingInstance(true);
        }

        // do not call destroyInstance inside of a synchronized block because
        // destroyInstance results in several callbacks to user code which
        // could result in a dead lock
        if ( singleton != null )
        {
            destroyInstance( singleton );
        }
    }

    public T getComponent( ) throws ComponentInstantiationException, ComponentLifecycleException
    {
        FutureTask<T> singletonFuture;
        synchronized (this) {
            if (disposed)
            {
                throw new ComponentLifecycleException("This ComponentManager has already been destroyed");
            }

            // if singleton already created, simply return the existing singleton
            T singleton = getExistingInstance( false );
            if (singleton != null) {
                return singleton;
            }

            // no existing singleton, create a new one
            singletonFuture = new FutureTask<T>(new CreateInstance());
            this.singletonFuture = singletonFuture;
        }

        // do not call CreateInstance.get() inside of a synchronized block because createInstance results in
        // several callbacks to user code which could result in a dead lock
        if ( singletonFuture != null )
        {
            singletonFuture.run();
        }

        // try to get the future instance
        try
        {
            return singletonFuture.get();
        }
        catch ( Exception e )
        {
            // creation failed... clear future reference
            synchronized ( this )
            {
                // only clear if still refering to this method's future
                if ( this.singletonFuture == singletonFuture )
                {
                    this.singletonFuture = null;
                }
            }

            Throwable cause = e.getCause();
            if ( cause == null )
            {
                cause = e;
            }
            if ( cause instanceof ComponentLifecycleException )
            {
                throw (ComponentLifecycleException) cause;
            }
            throw new ComponentLifecycleException( "Unexpected error obtaining singleton instance", cause );
        }
    }

    public void release( Object component ) throws ComponentLifecycleException
    {
        T singleton = getExistingInstance(true);

        // do not call destroyInstance inside of a synchronized block because
        // destroyInstance results in several callbacks to user code which
        // could result in a dead lock
        if ( singleton != null )
        {
            destroyInstance( singleton );
        }
    }

    public synchronized String toString()
    {
        T singleton = getExistingInstance(false);
        return "SingletonComponentManager[" + singleton == null ? getComponentDescriptor().getImplementationClass().getName() : singleton + "]";
    }

    private T getExistingInstance(boolean clearFuture) {
        synchronized (this) {
            try {
                return singletonFuture.get();
            } catch (Exception e) {
                // ignored - exception will have been reported in the createInstance method
            } finally {
                if (clearFuture) {
                    singletonFuture = null;
                }
            }
        }
        return null;
    }

    private class CreateInstance implements Callable<T> {
        public T call() throws Exception {
            return createInstance();
        }
    }
}
