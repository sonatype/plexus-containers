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
import org.codehaus.plexus.ComponentLookupManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/** @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a> */
public class ComponentLookupManagerComponentManager
    extends AbstractComponentManager<ComponentLookupManager>
{

    public ComponentLookupManagerComponentManager( MutablePlexusContainer container,
                                                   LifecycleHandler lifecycleHandler,
                                                   ComponentDescriptor<ComponentLookupManager> componentDescriptor,
                                                   String role,
                                                   String roleHint )
        throws UndefinedComponentManagerException
    {
        super( container, lifecycleHandler, componentDescriptor, role, roleHint );
    }

    public void dispose()
        throws ComponentLifecycleException
    {
    }

    public void release( Object component )
    {
    }

    public ComponentLookupManager getComponent()
        throws ComponentLifecycleException
    {
        return getContainer().getComponentLookupManager();
    }
}
