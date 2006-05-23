package org.codehaus.plexus;

import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

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
}
