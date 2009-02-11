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
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages a component manager.
 * Determines when a component is shutdown, and when it's started up. Each
 * manager deals with only one component class, though may handle multiple
 * instances of this class.
 *
 * @author Jason van Zyl
 *
 * @version $Id$
 */
public interface ComponentManager<T>
{
    String ROLE = ComponentManager.class.getName();

    /**
     * @deprecated for internal use only.. will be removed
     */
    AtomicLong NEXT_START_ID = new AtomicLong( 1 );

    /**
     * @deprecated use start instead
     */
    LifecycleHandler getLifecycleHandler();

    void dispose() throws ComponentLifecycleException;

    void release( Object component ) throws ComponentLifecycleException;

    T getComponent() throws ComponentInstantiationException, ComponentLifecycleException;

    ComponentDescriptor<T> getComponentDescriptor();

    MutablePlexusContainer getContainer();

    void start(Object component) throws PhaseExecutionException;

    /**
     * @deprecated for internal use only.. will be removed
     */
    long getStartId();
}