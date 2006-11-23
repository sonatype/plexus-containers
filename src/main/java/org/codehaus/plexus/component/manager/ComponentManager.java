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
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/**
 * Manages a component manager.
 * Determines when a component is shutdown, and when it's started up. Each
 * manager deals with only one component class, though may handle multiple
 * instances of this class.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentManager
{
    String ROLE = ComponentManager.class.getName();

    ComponentManager copy();

    String getId();

    void setup( MutablePlexusContainer container, LifecycleHandler lifecycleHandler, ComponentDescriptor componentDescriptor );

    void initialize();

    int getConnections();

    LifecycleHandler getLifecycleHandler();

    void dispose()
        throws ComponentLifecycleException;

    void release( Object component )
        throws ComponentLifecycleException;

    void suspend( Object component )
        throws ComponentLifecycleException;

    void resume( Object component )
        throws ComponentLifecycleException;

    Object getComponent()
        throws ComponentInstantiationException, ComponentLifecycleException;

    ComponentDescriptor getComponentDescriptor();

    MutablePlexusContainer getContainer();
}