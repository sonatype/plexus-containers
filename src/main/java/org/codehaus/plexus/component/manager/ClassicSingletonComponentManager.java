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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This ensures only a single manager of a a component exists. Once no
 * more connections for this component exists it is disposed.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 *
 * @version $Id$
 */
public class ClassicSingletonComponentManager
    extends AbstractComponentManager
{
    private Object lock;

    private Map singletonMap;

    public void release( Object component )
        throws ComponentLifecycleException
    {
        synchronized( lock )
        {
            if ( findSingleton( component ) == component )
            {
                decrementConnectionCount();

                if ( !connected() )
                {
                    dispose();
                }
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
            for ( Iterator i = singletonMap.values().iterator(); i.hasNext(); )
            {
                Object singleton = i.next();

                endComponentLifecycle( singleton );
            }
        }
    }

    public Object getComponent( ClassRealm realm )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        synchronized( lock )
        {
            Object singleton = findSingleton( realm );

            if ( singleton == null )
            {
                singleton = createComponentInstance( realm );

                if ( getLogger() != null )
                {
                    getLogger().debug( "Registering at " + componentDescriptor.getRealmId() + ": "
                        + singleton.getClass().getName() + " (object realm: " + singleton.getClass().getClassLoader()
                        + "), lookuprealm=" + realm.getId() );
                }
                singletonMap.put( componentDescriptor.getRealmId(), singleton );
            }

            incrementConnectionCount();

            return singleton;
        }
    }

    protected Object findSingleton( ClassRealm realm )
    {
        while ( realm != null )
        {
            Object o = singletonMap.get( realm.getId() );
            if ( o != null )
                return o;
            realm = realm.getParentRealm();
        }
        return null;
    }

    protected Object findSingleton( Object component )
    {
        ClassRealm cr = DefaultPlexusContainer.getLookupRealm( component );

        return singletonMap.get( cr == null ? container.getContainerRealm().getId() : cr.getId() );
    }

    // ----------------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------------

    public void initialize()
    {
        singletonMap = new HashMap();

        lock = new Object();
    }
}
