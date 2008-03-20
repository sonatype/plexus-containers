package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.configuration.source.ConfigurationSource;
import org.codehaus.plexus.container.initialization.ContainerInitializationPhase;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;

import java.net.URL;
import java.util.Map;

/**
 * @author Jason van Zyl
 */
public interface ContainerConfiguration
{
    ContainerConfiguration setName( String name );

    String getName();

    ContainerConfiguration setContext( Map context );

    Map getContext();

    ContainerConfiguration setClassWorld( ClassWorld classWorld );

    ClassWorld getClassWorld();

    ContainerConfiguration setParentContainer( PlexusContainer container );

    PlexusContainer getParentContainer();

    ContainerConfiguration setContainerConfiguration( String configuration );

    String getContainerConfiguration();

    ContainerConfiguration setContainerConfigurationURL( URL configuration );

    URL getContainerConfigurationURL();

    ContainerConfiguration setRealm( ClassRealm realm );

    ClassRealm getRealm();

    // Programmatic Container Initialization and Setup

    // Much of this setup and initialization can be completely hidden. It's probably not likely
    // someone will need to change these core components, but rather adding things like different
    // factories, and component managers.

    // Container initialization phases

    ContainerInitializationPhase[] getInitializationPhases();

    // Component lookup manager

    ComponentLookupManager getComponentLookupManager();

    // Component discoverer manager

    ContainerConfiguration addComponentDiscoverer( ComponentDiscoverer componentDiscoverer );

    ContainerConfiguration addComponentDiscoveryListener( ComponentDiscoveryListener componentDiscoveryListener );

    ContainerConfiguration setComponentDiscovererManager( ComponentDiscovererManager componentDiscovererManager );

    ComponentDiscovererManager getComponentDiscovererManager();

    // Component factory manager

    ContainerConfiguration setComponentFactoryManager( ComponentFactoryManager componentFactoryManager );

    ComponentFactoryManager getComponentFactoryManager();

    // Component manager manager

    ContainerConfiguration setComponentManagerManager( ComponentManagerManager componentManagerManager );        

    ComponentManagerManager getComponentManagerManager();

    // Component repository

    ContainerConfiguration setComponentRepository( ComponentRepository componentRepository );

    ComponentRepository getComponentRepository();

    // Component composer

    /**
     * @deprecated ComponentComposerManager is no longer used
     */
    ContainerConfiguration setComponentComposerManager( ComponentComposerManager componentComposerManager );

    /**
     * @deprecated ComponentComposerManager is no longer used
     */
    ComponentComposerManager getComponentComposerManager();

    // Lifecycle handler manager

    ContainerConfiguration addLifecycleHandler( LifecycleHandler lifecycleHandler );

    ContainerConfiguration setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager );

    LifecycleHandlerManager getLifecycleHandlerManager();

    // Configuration Sources

    ContainerConfiguration setConfigurationSource( ConfigurationSource configurationSource );

    ConfigurationSource getConfigurationSource();
}

