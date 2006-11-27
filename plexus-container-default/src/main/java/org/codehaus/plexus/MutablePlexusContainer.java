package org.codehaus.plexus;

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

import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.ClassWorld;

/**
 * @author Jason van Zyl
 */
public interface MutablePlexusContainer
    extends PlexusContainer
{
    // Core Components

    ComponentRepository getComponentRepository();

    void setComponentRepository( ComponentRepository componentRepository );

    LifecycleHandlerManager getLifecycleHandlerManager();

    void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager );

    ComponentManagerManager getComponentManagerManager();

    void setComponentManagerManager( ComponentManagerManager componentManagerManager );

    ComponentDiscovererManager getComponentDiscovererManager();

    void setComponentDiscovererManager( ComponentDiscovererManager componentDiscovererManager );

    ComponentFactoryManager getComponentFactoryManager();

    void setComponentFactoryManager( ComponentFactoryManager componentFactoryManager );

    ComponentLookupManager getComponentLookupManager();

    void setComponentLookupManager( ComponentLookupManager componentLookupManager );

    ComponentComposerManager getComponentComposerManager();

    void setComponentComposerManager( ComponentComposerManager componentComposerManager );

    LoggerManager getLoggerManager();

    void setLoggerManager( LoggerManager loggerManager );

    Logger getLogger();

    // Configuration

    void setConfiguration( PlexusConfiguration configuration );

    PlexusConfiguration getConfiguration();

    // Parent Container

    PlexusContainer getParentContainer();

    ClassRealm getComponentRealm( String realmId );

    ClassWorld getClassWorld();
}
