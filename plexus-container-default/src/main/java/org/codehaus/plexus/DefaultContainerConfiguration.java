package org.codehaus.plexus;

import java.net.URL;
import java.util.Map;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.discovery.DefaultComponentDiscoverer;
import org.codehaus.plexus.component.discovery.DefaultComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.PlexusXmlComponentDiscoverer;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.factory.DefaultComponentFactoryManager;
import org.codehaus.plexus.component.manager.ClassicSingletonComponentManager;
import org.codehaus.plexus.component.manager.ComponentLookupManagerComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.DefaultComponentManagerManager;
import org.codehaus.plexus.component.manager.KeepAliveSingletonComponentManager;
import org.codehaus.plexus.component.manager.PerLookupComponentManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.DefaultComponentRepository;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.source.ConfigurationSource;
import org.codehaus.plexus.container.initialization.ComponentDiscoveryPhase;
import org.codehaus.plexus.container.initialization.ContainerInitializationPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentComposerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentDiscovererManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentFactoryManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentLookupManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentManagerManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeComponentRepositoryPhase;
import org.codehaus.plexus.container.initialization.InitializeContainerConfigurationSourcePhase;
import org.codehaus.plexus.container.initialization.InitializeContextPhase;
import org.codehaus.plexus.container.initialization.InitializeLifecycleHandlerManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeLoggerManagerPhase;
import org.codehaus.plexus.container.initialization.InitializeResourcesPhase;
import org.codehaus.plexus.container.initialization.InitializeSystemPropertiesPhase;
import org.codehaus.plexus.container.initialization.InitializeUserConfigurationSourcePhase;
import org.codehaus.plexus.container.initialization.StartLoadOnStartComponentsPhase;
import org.codehaus.plexus.lifecycle.BasicLifecycleHandler;
import org.codehaus.plexus.lifecycle.DefaultLifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ConfigurablePhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ContextualizePhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.DisposePhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializePhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.LogDisablePhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.LogEnablePhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceablePhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StopPhase;

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

    private ConfigurationSource configurationSource;

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
            new InitializeComponentFactoryManagerPhase(),
            new InitializeComponentLookupManagerPhase(),
            new InitializeComponentComposerPhase(),
            new InitializeContainerConfigurationSourcePhase(),
            new InitializeLoggerManagerPhase(),
            new InitializeContextPhase(),
            new InitializeSystemPropertiesPhase(),
            new InitializeComponentDiscovererManagerPhase(),
            new ComponentDiscoveryPhase(),
            new InitializeUserConfigurationSourcePhase(),
            new StartLoadOnStartComponentsPhase(),

        };

    public ComponentLookupManager getComponentLookupManager()
    {
        return new DefaultComponentLookupManager();
    }

    // Component discoverer

    private ComponentDiscovererManager componentDiscovererManager;

    public ContainerConfiguration addComponentDiscoveryListener( ComponentDiscoveryListener componentDiscoveryListener )
    {
        getComponentDiscovererManager().registerComponentDiscoveryListener( componentDiscoveryListener );

        return this;
    }

    public ContainerConfiguration addComponentDiscoverer( ComponentDiscoverer componentDiscoverer )
    {
        ((DefaultComponentDiscovererManager)getComponentDiscovererManager()).addComponentDiscoverer( componentDiscoverer );

        return this;
    }

    public ContainerConfiguration setComponentDiscovererManager( ComponentDiscovererManager componentDiscovererManager )
    {
        this.componentDiscovererManager = componentDiscovererManager;

        return this;
    }

    public ComponentDiscovererManager getComponentDiscovererManager()
    {
        if ( componentDiscovererManager == null )
        {
            componentDiscovererManager = new DefaultComponentDiscovererManager();

            ((DefaultComponentDiscovererManager)componentDiscovererManager).addComponentDiscoverer( new DefaultComponentDiscoverer() );

            ((DefaultComponentDiscovererManager)componentDiscovererManager).addComponentDiscoverer( new PlexusXmlComponentDiscoverer() );
        }

        return componentDiscovererManager;
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

    /**
     * @deprecated ComponentComposerManager is no longer used
     */
    public ContainerConfiguration setComponentComposerManager( ComponentComposerManager componentComposerManager )
    {
        return this;
    }

    /**
     * @deprecated ComponentComposerManager is no longer used
     */
    public ComponentComposerManager getComponentComposerManager()
    {
        return new ComponentComposerManager() {
            public void addComponentComposer(ComponentComposer componentComposer) {
                throw new UnsupportedOperationException("ComponentComposerManager is no longer used");
            }

            public void assembleComponent(Object component, ComponentDescriptor componentDescriptor, PlexusContainer container) throws CompositionException, UndefinedComponentComposerException {
                throw new UnsupportedOperationException("ComponentComposerManager is no longer used");
            }

            public void assembleComponent(Object component, ComponentDescriptor componentDescriptor, PlexusContainer container, ClassRealm lookupRealm) throws CompositionException, UndefinedComponentComposerException {
                throw new UnsupportedOperationException("ComponentComposerManager is no longer used");
            }
        };
    }

    // Lifecycle handler manager

    private LifecycleHandlerManager lifecycleHandlerManager;

    public ContainerConfiguration addLifecycleHandler( LifecycleHandler lifecycleHandler )
    {
        getLifecycleHandlerManager().addLifecycleHandler( lifecycleHandler );

        return this;
    }

    public ContainerConfiguration setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.lifecycleHandlerManager = lifecycleHandlerManager;

        return this;
    }

    public LifecycleHandlerManager getLifecycleHandlerManager()
    {
        if ( lifecycleHandlerManager == null )
        {
            lifecycleHandlerManager = new DefaultLifecycleHandlerManager();

            // Plexus
            LifecycleHandler plexus = new BasicLifecycleHandler( "plexus" );
            // Begin
            plexus.addBeginSegment( new LogEnablePhase() );
//            plexus.addBeginSegment( new CompositionPhase() );
            plexus.addBeginSegment( new ContextualizePhase() );
//            plexus.addBeginSegment( new AutoConfigurePhase() );
            plexus.addBeginSegment( new ServiceablePhase() );
            plexus.addBeginSegment( new InitializePhase() );
            plexus.addBeginSegment( new StartPhase() );
            // End
            plexus.addEndSegment( new StopPhase() );
            plexus.addEndSegment( new DisposePhase() );
            plexus.addEndSegment( new LogDisablePhase() );
            lifecycleHandlerManager.addLifecycleHandler( plexus );

            // Basic
            LifecycleHandler basic = new BasicLifecycleHandler( "basic" );
            // Begin
            basic.addBeginSegment( new LogEnablePhase() );
            basic.addBeginSegment( new ContextualizePhase() );
//            basic.addBeginSegment( new AutoConfigurePhase() );
            basic.addBeginSegment( new InitializePhase() );
            basic.addBeginSegment( new StartPhase() );
            // End
            basic.addEndSegment( new StopPhase() );
            basic.addEndSegment( new DisposePhase() );
            basic.addEndSegment( new LogDisablePhase() );
            lifecycleHandlerManager.addLifecycleHandler( basic );

            // Plexus configurable
            LifecycleHandler plexusConfigurable = new BasicLifecycleHandler( "plexus-configurable" );
            // Begin
            plexusConfigurable.addBeginSegment( new LogEnablePhase() );
            plexusConfigurable.addBeginSegment( new ContextualizePhase() );
            plexusConfigurable.addBeginSegment( new ConfigurablePhase() );
            plexusConfigurable.addBeginSegment( new ServiceablePhase() );
            plexusConfigurable.addBeginSegment( new InitializePhase() );
            plexusConfigurable.addBeginSegment( new StartPhase() );
            // End
            plexusConfigurable.addEndSegment( new StopPhase() );
            plexusConfigurable.addEndSegment( new DisposePhase() );
            plexusConfigurable.addEndSegment( new LogDisablePhase() );
            lifecycleHandlerManager.addLifecycleHandler( plexusConfigurable );

            // Passive
            LifecycleHandler passive = new BasicLifecycleHandler( "passive" );
            lifecycleHandlerManager.addLifecycleHandler( passive );

            // Bootstrap
            LifecycleHandler bootstrap = new BasicLifecycleHandler( "bootstrap" );
//            bootstrap.addBeginSegment( new CompositionPhase() );
            bootstrap.addBeginSegment( new ContextualizePhase() );
            lifecycleHandlerManager.addLifecycleHandler( bootstrap );
        }

        return lifecycleHandlerManager;
    }

    // Configuration Sources

    public ContainerConfiguration setConfigurationSource( ConfigurationSource configurationSource )
    {
        this.configurationSource = configurationSource;

        return this;
    }

    public ConfigurationSource getConfigurationSource()
    {
        return configurationSource;
    }
}
