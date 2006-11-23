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

/**
 * This ensures a component is only used as a singleton, and is only shutdown when
 * the container shuts down.
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 *
 * @version $Id$
 */
public class KeepAliveSingletonComponentManager
    extends AbstractComponentManager
{
    private Object lock = new Object();

    private Object singleton;

    public void release( Object component )
    {
        synchronized( lock )
        {
            if ( singleton == component )
            {
                decrementConnectionCount();
            }
            else
            {
                getLogger().warn( "Component returned which is not the same manager. Ignored. component=" + component );
            }
        }
    }

    public void dispose()
        throws ComponentLifecycleException
    {
        synchronized( lock )
        {
            if ( singleton != null )
            {
                endComponentLifecycle( singleton );
            }
        }
    }

    public Object getComponent()
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        synchronized( lock )
        {
            if ( singleton == null )
            {
                singleton = createComponentInstance();
            }
    
            incrementConnectionCount();
    
            return singleton;
        }
    }
}
