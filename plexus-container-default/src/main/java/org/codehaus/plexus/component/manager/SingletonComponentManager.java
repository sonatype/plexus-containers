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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;

/**
 * This ensures a component is only used as a singleton, and is only shutdown when the container
 * shuts down.
 * 
 * @author Jason van Zyl
 */
public class SingletonComponentManager
    extends AbstractComponentManager
{
    private Object singleton;

    public String getId()
    {
        return "singleton";
    }

    public synchronized void release( Object component )
        throws ComponentLifecycleException
    {
        if ( singleton == component )
        {
            dispose();
        }
    }

    public synchronized void dispose()
        throws ComponentLifecycleException
    {
        if ( singleton != null )
        {
            endComponentLifecycle( singleton );
            singleton = null;
        }
    }

    public synchronized Object getComponent( ClassRealm realm )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        if ( singleton == null )
        {
            singleton = createComponentInstance( realm );
        }

        incrementConnectionCount();

        return singleton;
    }
}
