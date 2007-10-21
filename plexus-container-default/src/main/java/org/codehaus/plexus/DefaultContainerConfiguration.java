package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.composition.DefaultComponentComposerManager;
import org.codehaus.plexus.component.composition.FieldComponentComposer;
import org.codehaus.plexus.component.composition.MapOrientedComponentComposer;
import org.codehaus.plexus.component.composition.NoOpComponentComposer;
import org.codehaus.plexus.component.composition.setter.SetterComponentComposer;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.DefaultComponentDiscoverer;
import org.codehaus.plexus.component.discovery.DefaultComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.PlexusXmlComponentDiscoverer;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.factory.DefaultComponentFactoryManager;
import org.codehaus.plexus.component.manager.ClassicSingletonComponentManager;
import org.codehaus.plexus.component.manager.ComponentLookupManagerComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.ContainerComponentManager;
import org.codehaus.plexus.component.manager.DefaultComponentManagerManager;
import org.codehaus.plexus.component.manager.KeepAliveSingletonComponentManager;
import org.codehaus.plexus.component.manager.PerLookupComponentManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.DefaultComponentRepository;
import org.codehaus.plexus.container.initialization.ComponentDiscoveryPhase;
import org.codehaus.plexus.container.initialization.ContainerInitializationPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentComposerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentDiscovererManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentFactoryManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentLookupManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentManagerManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentRepositoryPhase;
import org.codehaus.plexus.container.initialization.InitializeContextPhase;
import org.codehaus.plexus.container.initialization.InitializeLifecycleHandlerManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeLoggerManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeResourcesPhase;
import org.codehaus.plexus.container.initialization.InitializeSystemPropertiesPhase;
import org.codehaus.plexus.container.initialization.RegisterComponentDiscoveryListenersPhase;
import org.codehaus.plexus.container.initialization.StartLoadOnStartComponentsPhase;

import java.net.URL;
import java.util.Map;

/** @author Jason van Zyl */
public class DefaultContainerConfiguration
    implements ContainerConfiguration
{
    private String name;

    private Map context;

    private ClassWorld classWorld;

    private ClassRealm realm;

    private PlexusContainer parentContainer;

    private String containerConfiguration;

    private URL containerConfigurationURL;

    public ContainerConfiguration setName( String name )
    {
        this.name = name;

        return this;
    }

    public ContainerConfiguration setContext( Map context )
    {
        this.context = context;

        return this;
    }

    public ContainerConfiguration setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;

        return this;
    }

    public ContainerConfiguration setRealm( ClassRealm realm )
    {
        this.realm = realm;

        return this;
    }

    public ContainerConfiguration setParentContainer( PlexusContainer parentContainer )
    {
        this.parentContainer = parentContainer;

        return this;
    }

    public ContainerConfiguration setContainerConfiguration( String containerConfiguration )
    {
        this.containerConfiguration = containerConfiguration;

        return this;
    }

    public String getContainerConfiguration()
    {
        return containerConfiguration;
    }

    public ContainerConfiguration setContainerConfigurationURL( URL containerConfiguration )
    {
        this.containerConfigurationURL = containerConfiguration;

        return this;
    }

    public URL getContainerConfigurationURL()
    {
        return containerConfigurationURL;
    }

    public String getName()
    {
        return name;
    }

    public Map getContext()
    {
        return context;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public PlexusContainer getParentContainer()
    {
        return parentContainer;
    }

    public ClassRealm getRealm()
    {
        return realm;
    }

    // Programmatic Container Initialization and Setup

    public ContainerConfiguration setInitializationPhases( ContainerInitializationPhase[] initializationPhases )
    {
        this.initializationPhases = initializationPhases;

        return this;
    }

    public ContainerInitializationPhase[] getInitializationPhases()
    {
        return initializationPhases;
    }

    private ContainerInitializationPhase[] initializationPhases =
        {
            new InitializeResourcesPhase(),
            new InitializeComponentRepositoryPhase(),
            new InitializeLifecycleHandlerManagerPhase(),
            new InitializeComponentManagerManagerPhase(),
            new InitializeComponentDiscovererManagerPhase(),
            new InitializeComponentFactoryManagerPhase(),
            new InitializeComponentLookupManagerPhase(),
            new InitializeComponentComposerPhase(),
            new InitializeLoggerManagerPhase(),
            new InitializeContextPhase(),
            new InitializeSystemPropertiesPhase(),
            new RegisterComponentDiscoveryListenersPhase(),
            new ComponentDiscoveryPhase(),
            new StartLoadOnStartComponentsPhase(),

        };

    public ComponentLookupManager getComponentLookupManager()
    {
        return new DefaultComponentLookupManager();
    }

    private ComponentDiscovererManager componentDiscovererManager;

    public ComponentDiscovererManager getComponentDiscovererManager()
    {
        if ( componentDiscovererManager == null )
        {
            componentDiscovererManager = new DefaultComponentDiscovererManager();

            componentDiscovererManager.addComponentDiscoverer( new DefaultComponentDiscoverer() );

            componentDiscovererManager.addComponentDiscoverer( new PlexusXmlComponentDiscoverer() );                    
        }

        return componentDiscovererManager;
    }

    public ContainerConfiguration setComponentDiscovererManager( ComponentDiscovererManager componentDiscovererManager )
    {
        this.componentDiscovererManager = componentDiscovererManager;

        return this;
    }

    private ComponentFactoryManager componentFactoryManager;

    public ComponentFactoryManager getComponentFactoryManager()
    {
        if ( componentFactoryManager == null )
        {
            componentFactoryManager = new DefaultComponentFactoryManager();                        
        }

        return componentFactoryManager;
    }

    public ContainerConfiguration setComponentFactoryManager( ComponentFactoryManager componentFactoryManager )
    {
        this.componentFactoryManager = componentFactoryManager;

        return this;
    }

    private ComponentManagerManager componentManagerManager;

    public ComponentManagerManager getComponentManagerManager()
    {
        if ( componentManagerManager == null )
        {
            componentManagerManager = new DefaultComponentManagerManager();

            componentManagerManager.addComponentManager( new PerLookupComponentManager() );

            componentManagerManager.addComponentManager( new ClassicSingletonComponentManager() );

            componentManagerManager.addComponentManager( new KeepAliveSingletonComponentManager() );

            componentManagerManager.addComponentManager( new ContainerComponentManager() );

            componentManagerManager.addComponentManager( new ComponentLookupManagerComponentManager() );
        }

        return componentManagerManager;
    }

    public ContainerConfiguration setComponentManagerManager( ComponentManagerManager componentManagerManager )
    {
        this.componentManagerManager = componentManagerManager;

        return this;
    }

    private ComponentRepository componentRepository;

    public ContainerConfiguration setComponentRepository( ComponentRepository componentRepository )
    {
        this.componentRepository = componentRepository;

        return this;
    }

    public ComponentRepository getComponentRepository()
    {
        if ( componentRepository == null )
        {
            componentRepository = new DefaultComponentRepository();
        }

        return componentRepository;
    }


    private ComponentComposerManager componentComposerManager;

    public ContainerConfiguration setComponentComposerManager( ComponentComposerManager componentComposerManager )
    {
        this.componentComposerManager = componentComposerManager;

        return this;
    }

    public ComponentComposerManager getComponentComposerManager()
    {
        if ( componentComposerManager == null )
        {
            componentComposerManager = new DefaultComponentComposerManager();

            componentComposerManager.addComponentComposer( new FieldComponentComposer() );

            componentComposerManager.addComponentComposer( new SetterComponentComposer() );

            componentComposerManager.addComponentComposer( new MapOrientedComponentComposer() );

            componentComposerManager.addComponentComposer( new NoOpComponentComposer() );            
        }

        return componentComposerManager;
    }
}
