package org.codehaus.plexus.component.repository;

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

import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ComponentProfile
{
    /** Component Factory. */
    private ComponentFactory componentFactory;

    /** Lifecycle Handler. */
    private LifecycleHandler lifecycleHandler;

    /** Component Manager. */
    private ComponentManager componentManager;    
    
    /** Component Composer. */
    private ComponentComposer componentComposer;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public ComponentFactory getComponentFactory()
    {
        return componentFactory;
    }

    public void setComponentFactory( ComponentFactory componentFactory )
    {
        this.componentFactory = componentFactory;
    }

    public LifecycleHandler getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    public void setLifecycleHandler( LifecycleHandler lifecycleHandler )
    {
        this.lifecycleHandler = lifecycleHandler;
    }

    public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    public void setComponentManager( ComponentManager componentManager )
    {
        this.componentManager = componentManager;
    }

    public ComponentComposer getComponentComposer()
    {
        return componentComposer;
    }
    
    public void setComponentComposer( ComponentComposer componentComposer )
    {
        this.componentComposer = componentComposer;
    }

}
