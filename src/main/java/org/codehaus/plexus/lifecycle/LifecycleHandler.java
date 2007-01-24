package org.codehaus.plexus.lifecycle;

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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public interface LifecycleHandler
{
    String getId();

    /**
     * @deprecated
     */
    void start( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    void start( Object component, ComponentManager manager, ClassRealm realm )
        throws PhaseExecutionException;

    void suspend( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    void resume( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    /**
     * @deprecated
     */
    void end( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    /**
     *
     * @param component
     * @param manager
     * @param componentContextRealm the realm used to create the component, which may not be the component's realm; this
     *            component could have requirements that were satisfied using components from this realm. It could be
     *            used to lookup the same manager components that were used to start the component.
     * @throws PhaseExecutionException
     */
    void end( Object component, ComponentManager manager, ClassRealm componentContextRealm )
        throws PhaseExecutionException;

    void initialize();
}
