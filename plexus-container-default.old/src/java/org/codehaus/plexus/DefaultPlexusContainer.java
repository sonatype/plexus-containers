package org.codehaus.plexus;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.discovery.DiscoveryListenerDescriptor;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;
import org.codehaus.plexus.configuration.xml.xstream.PlexusXStream;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @todo clarify configuration handling vis-a-vis user vs default values
 * @todo use classworlds whole hog, plexus' concern is applications.
 * @todo allow setting of a live configuraton so applications that embed plexus
 *       can use whatever configuration mechanism they like. They just have to
 *       adapt it into something plexus can understand.
 */
public class DefaultPlexusContainer
    extends AbstractLogEnabled
    implements PlexusContainer
{
    private PlexusContainer parentContainer;

    private LoggerManager loggerManager;

    private DefaultContext context;

    private PlexusConfiguration configuration;

    private Reader configurationReader;

    private ClassWorld classWorld;

    private ClassRealm classRealm;

    private ClassLoader classLoader;

    // Core components

    private ComponentRepository componentRepository;

    private ComponentManagerManager componentManagerManager;

    private LifecycleHandlerManager lifecycleHandlerManager;

    private ComponentDiscovererManager componentDiscovererManager;

    private ComponentFactoryManager componentFactoryManager;

    private ComponentComposerManager componentComposerManager;

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    public DefaultPlexusContainer()
    {
        context = new DefaultContext();

        loggerManager = new ConsoleLoggerManager( "debug" );

        enableLogging( loggerManager.getLoggerForComponent( PlexusContainer.class.getName() ) );
    }

    // ----------------------------------------------------------------------
    // Container Contract
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Component Lookup
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Try to lookup the component manager for the requested component.
    //
    // component manager exists:
    //   -> return a component from the component manager.
    //
    // component manager doesn't exist;
    //   -> lookup component descriptor for the requested component.
    //   -> instantiate component manager for this component.
    //   -> track the component manager for this component by the component class name.
    //   -> return a component from the component manager.
    // ----------------------------------------------------------------------

    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        Object component = null;

        ComponentManager componentManager = componentManagerManager.findComponentManagerByComponentKey( componentKey );

        // The first time we lookup a component a component manager will not exist so we ask the
        // component manager manager to create a component manager for us.

        if ( componentManager == null )
        {
            ComponentDescriptor descriptor = componentRepository.getComponentDescriptor( componentKey );

            if ( descriptor == null )
            {
                if ( parentContainer != null )
                {
                    return parentContainer.lookup( componentKey );
                }

                getLogger().error( "Non existant component: " + componentKey );

                String message = "Component descriptor cannot be found in the component repository: " + componentKey + ".";

                throw new ComponentLookupException( message );
            }

            componentManager = createComponentManager( descriptor );
        }

        try
        {
            component = componentManager.getComponent();

            componentManagerManager.associateComponentWithComponentManager( component, componentManager );
        }
        catch ( Exception e )
        {
            String message = "Cannot create component for " + componentKey + ".";

            getLogger().error( message, e );

            throw new ComponentLookupException( message, e );
        }

        return component;
    }

    private ComponentManager createComponentManager( ComponentDescriptor descriptor )
        throws ComponentLookupException
    {
        ComponentManager componentManager;

        try
        {
            componentManager = componentManagerManager.createComponentManager( descriptor, this );
        }
        catch ( Exception e )
        {
            String message = "Cannot create component manager for " + descriptor.getComponentKey() + ", so we cannot provide a component instance.";

            getLogger().error( message, e );

            throw new ComponentLookupException( message, e );
        }

        return componentManager;
    }

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        Map components = new HashMap();

        Map componentDescriptors = getComponentDescriptorMap( role );

        if ( componentDescriptors != null )
        {
            // Now we have a map of component descriptors keyed by role hint.

            for ( Iterator i = componentDescriptors.keySet().iterator(); i.hasNext(); )
            {
                String roleHint = (String) i.next();

                Object component = lookup( role, roleHint );

                components.put( roleHint, component );
            }
        }

        return components;
    }

    public List lookupList( String role )
        throws ComponentLookupException
    {
        List components = new ArrayList();

        List componentDescriptors = getComponentDescriptorList( role );

        if ( componentDescriptors != null )
        {
            // Now we have a list of component descriptors.

            for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
            {
                ComponentDescriptor descriptor = (ComponentDescriptor) i.next();

                String roleHint = descriptor.getRoleHint();

                Object component;

                if ( roleHint != null )
                {
                    component = lookup( role, roleHint );
                }
                else
                {
                    component = lookup( role );
                }

                components.add( component );
            }
        }

        return components;
    }


    public Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role + roleHint );
    }

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    public ComponentDescriptor getComponentDescriptor( String componentKey )
    {
        ComponentDescriptor result = componentRepository.getComponentDescriptor( componentKey );

        if ( result == null && parentContainer != null )
        {
            result = parentContainer.getComponentDescriptor( componentKey );
        }

        return result;
    }

    public Map getComponentDescriptorMap( String role )
    {
        Map result = null;

        if ( parentContainer != null )
        {
            result = parentContainer.getComponentDescriptorMap( role );
        }

        Map componentDescriptors = componentRepository.getComponentDescriptorMap( role );

        if ( componentDescriptors != null )
        {
            if ( result != null )
            {
                result.putAll( componentDescriptors );
            }
            else
            {
                result = componentDescriptors;
            }
        }

        return result;
    }

    public List getComponentDescriptorList( String role )
    {
        List result = null;

        Map componentDescriptorsByHint = getComponentDescriptorMap( role );

        if ( componentDescriptorsByHint != null )
        {
            result = new ArrayList( componentDescriptorsByHint.values() );
        }
        else
        {
            result = new ArrayList();
        }

        ComponentDescriptor unhintedDescriptor = getComponentDescriptor( role );

        if ( unhintedDescriptor != null )
        {
            result.add( unhintedDescriptor );
        }

        return result;
    }


    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException
    {
        componentRepository.addComponentDescriptor( componentDescriptor );
    }

    // ----------------------------------------------------------------------
    // Component Release
    // ----------------------------------------------------------------------

    public void release( Object component )
        throws Exception
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = componentManagerManager.findComponentManagerByComponentInstance( component );

        if ( componentManager == null )
        {
            if ( parentContainer != null )
            {
                parentContainer.release( component );
            }
            else
            {
                getLogger().warn( "Component manager not found for returned component. Ignored. component=" + component );
            }
        }
        else
        {
            componentManager.release( component );
        }
    }

    public void releaseAll( Map components )
        throws Exception
    {
        for ( Iterator i = components.values().iterator(); i.hasNext(); )
        {
            Object component = i.next();

            release( component );
        }
    }

    public void releaseAll( List components )
        throws Exception
    {
        for ( Iterator i = components.iterator(); i.hasNext(); )
        {
            Object component = i.next();

            release( component );
        }
    }

    public boolean hasComponent( String componentKey )
    {
        return componentRepository.hasComponent( componentKey );
    }

    public boolean hasComponent( String role, String roleHint )
    {
        return componentRepository.hasComponent( role, roleHint );
    }

    public void suspend( Object component )
        throws Exception
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = componentManagerManager.findComponentManagerByComponentInstance( component );

        componentManager.suspend( component );
    }

    public void resume( Object component )
        throws Exception
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = componentManagerManager.findComponentManagerByComponentInstance( component );

        componentManager.resume( component );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        initializeClassWorlds();

        initializeConfiguration();

        initializeResources();

        initializeCoreComponents();

        initializeLoggerManager();

        initializeContext();

        initializeSystemProperties();
    }

    private void discoverComponents()
        throws Exception
    {
        for ( Iterator i = componentDiscovererManager.getComponentDiscoverers().iterator(); i.hasNext(); )
        {
            ComponentDiscoverer componentDiscoverer = (ComponentDiscoverer) i.next();

            List componentDescriptors = componentDiscoverer.findComponents( getClassLoader() );

            for ( Iterator j = componentDescriptors.iterator(); j.hasNext(); )
            {
                ComponentDescriptor componentDescriptor = (ComponentDescriptor) j.next();

                // If the user has already defined a component descriptor for this particular
                // component then do not let the discovered component descriptor override
                // the user defined one.
                if ( getComponentDescriptor( componentDescriptor.getComponentKey() ) == null )
                {
                    addComponentDescriptor( componentDescriptor );
                }
            }
        }
    }

    // We need to be aware of dependencies between discovered components when the listed component
    // as the discovery listener itself depends on components that need to be discovered.

    public void start()
        throws Exception
    {
        List listeners = componentDiscovererManager.getListenerDescriptors();

        if ( listeners != null )
        {
            for ( Iterator i = listeners.iterator(); i.hasNext(); )
            {
                DiscoveryListenerDescriptor listenerDescriptor = (DiscoveryListenerDescriptor) i.next();

                String role = listenerDescriptor.getRole();

                ComponentDiscoveryListener l = (ComponentDiscoveryListener) lookup( role );

                componentDiscovererManager.registerComponentDiscoveryListener( l );
            }
        }

        discoverComponents();

        loadComponentsOnStart();

        configuration = null;
    }

    public void dispose()
    {
        disposeAllComponents();
    }

    protected void disposeAllComponents()
    {
        for ( Iterator iter = componentManagerManager.getComponentManagers().values().iterator(); iter.hasNext(); )
        {
            try
            {
                ( (ComponentManager) iter.next() ).dispose();
            }
            catch ( Exception e )
            {
                getLogger().error( "Error while disposing component manager. Continuing with the rest", e );
            }
        }

        componentManagerManager.getComponentManagers().clear();
    }

    // ----------------------------------------------------------------------
    // Pre-initialization - can only be called prior to initialization
    // ----------------------------------------------------------------------

    public void setParentPlexusContainer( PlexusContainer parentContainer )
    {
        this.parentContainer = parentContainer;
    }

    public void addContextValue( Object key, Object value )
    {
        context.put( key, value );
    }

    /** @see PlexusContainer#setConfigurationResource(Reader) */
    public void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException
    {
        this.configurationReader = configuration;
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------


    

    protected void loadComponentsOnStart()
        throws Exception
    {
        PlexusConfiguration[] loadOnStartComponents = configuration.getChild( "load-on-start" ).getChildren( "component" );

        getLogger().info( "Found " + loadOnStartComponents.length + " components to load on start" );

        for ( int i = 0; i < loadOnStartComponents.length; i++ )
        {
            String role = loadOnStartComponents[i].getChild( "role" ).getValue();

            String roleHint = loadOnStartComponents[i].getChild( "role-hint" ).getValue();

            getLogger().info( "Loading on start [role,roleHint]: " + "[" + role + "," + roleHint + "]" );

            try
            {
                if ( roleHint == null )
                {
                    lookup( role );
                }
                else
                {
                    lookup( role, roleHint );
                }
            }
            catch ( ComponentLookupException e )
            {
                getLogger().error( "Cannot load-on-start " + role, e );
            }
        }
    }

    // ----------------------------------------------------------------------
    // ClassWorlds, ClassRealms and ClassLoaders
    // ----------------------------------------------------------------------

    // 1. Embedder may set a ClassLoader
    // 2. Classworlds launcher hands us a Classworld

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
        
        addContextValue( "common.classloader", classLoader );
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public ClassRealm getClassRealm()
    {
        return classRealm;
    }
    
    private void initializeClassWorlds()
        throws Exception
    {
        if ( classWorld == null )
        {
            classWorld = new ClassWorld();
        }

        try
        {
            classRealm = classWorld.getRealm( "core" );
        }
        catch ( NoSuchRealmException e )
        {
            if ( classLoader != null && classRealm != null )
            {
                classRealm = classWorld.newRealm( "core", classLoader );
            }
            else
            {
                classRealm = classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );
            }
        }

        setClassLoader( classRealm.getClassLoader() );

        Thread.currentThread().setContextClassLoader( classLoader );
    }

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    public Context getContext()
    {
        return context;
    }

    private void initializeContext()
    {
        addContextValue( PlexusConstants.PLEXUS_KEY, this );
    }

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private void initializeConfiguration()
        throws Exception
    {
        // System userConfiguration

        InputStream is = getClassLoader().getResourceAsStream( "org/codehaus/plexus/plexus.conf" );

        if ( is == null )
        {
            throw new IllegalStateException( "The internal default plexus.conf is missing. " +
                                             "This is highly irregular, your plexus JAR is " +
                                             "most likely corrupt. The class loader being used is: " + getClassLoader() );
        }

        PlexusConfiguration systemConfiguration =
            PlexusTools.buildConfiguration( new InputStreamReader( is ) );

        if ( configurationReader != null )
        {
            // User userConfiguration

            PlexusConfiguration userConfiguration =
                PlexusTools.buildConfiguration( getInterpolationConfigurationReader( configurationReader ) );

            // Merger of systemConfiguration and user userConfiguration

            configuration = PlexusConfigurationMerger.merge( userConfiguration, systemConfiguration );

            processConfigurationsDirectory();
        }
        else
        {
            configuration = systemConfiguration;
        }
    }

    private Reader getInterpolationConfigurationReader( Reader reader )
    {
        InterpolationFilterReader interpolationFilterReader =
            new InterpolationFilterReader( reader, new ContextMapAdapter( context ) );

        return interpolationFilterReader;
    }

    /**
     * Process any additional component configuration files that have been
     * specified. The specified directory is scanned recursively so configurations
     * can be within nested directories to help with component organization.
     */
    private void processConfigurationsDirectory()
        throws Exception
    {
        String s = configuration.getChild( "configurations-directory" ).getValue( null );

        if ( s != null )
        {
            XmlPlexusConfiguration componentsConfiguration =
                (XmlPlexusConfiguration) configuration.getChild( "components" );

            File configurationsDirectory = new File( s );

            if ( configurationsDirectory.exists()
                &&
                configurationsDirectory.isDirectory() )
            {
                List componentConfigurationFiles = FileUtils.getFiles( configurationsDirectory, "**/*.conf", "**/*.xml" );

                for ( Iterator i = componentConfigurationFiles.iterator(); i.hasNext(); )
                {
                    File componentConfigurationFile = (File) i.next();

                    PlexusConfiguration componentConfiguration =
                        PlexusTools.buildConfiguration( getInterpolationConfigurationReader( new FileReader( componentConfigurationFile ) ) );

                    componentsConfiguration.addAllChildren( componentConfiguration.getChild( "components" ) );
                }
            }
        }
    }

    private void initializeLoggerManager()
        throws Exception
    {
        loggerManager = (LoggerManager) lookup( LoggerManager.ROLE );

        enableLogging( loggerManager.getLoggerForComponent( PlexusContainer.class.getName() ) );
    }

    private void initializeCoreComponents()
        throws Exception
    {
        // Component repository

        PlexusXStream builder = new PlexusXStream();

        builder.alias( "listener", DiscoveryListenerDescriptor.class );

        PlexusConfiguration c = configuration.getChild( "component-repository" );

        componentRepository = (ComponentRepository) builder.build( c );

        componentRepository.configure( configuration );

        componentRepository.setClassRealm( classRealm );

        componentRepository.initialize();

        // Lifecycle handler manager

        c = configuration.getChild( "lifecycle-handler-manager" );

        lifecycleHandlerManager = (LifecycleHandlerManager) builder.build( c );

        lifecycleHandlerManager.initialize();

        // Component manager manager

        c = configuration.getChild( "component-manager-manager" );

        componentManagerManager = (ComponentManagerManager) builder.build( c );

        componentManagerManager.setLifecycleHandlerManager( lifecycleHandlerManager );

        // Component discoverer manager

        c = configuration.getChild( "component-discoverer-manager" );

        componentDiscovererManager = (ComponentDiscovererManager) builder.build( c );

        componentDiscovererManager.initialize();

        // Component factory manager

        c = configuration.getChild( "component-factory-manager" );

        componentFactoryManager = (ComponentFactoryManager) builder.build( c );


        // Component factory manager

        c = configuration.getChild( "component-composer-manager" );

        componentComposerManager = ( ComponentComposerManager ) builder.build( c );

    }

    private void initializeSystemProperties()
        throws Exception
    {
        PlexusConfiguration[] systemProperties = configuration.getChild( "system-properties" ).getChildren( "property" );

        for ( int i = 0; i < systemProperties.length; ++i )
        {
            String name = systemProperties[i].getAttribute( "name" );

            String value = systemProperties[i].getAttribute( "value" );

            System.getProperties().setProperty( name, value );

            getLogger().info( "Setting system property: [ " + name + ", " + value + " ]" );
        }
    }

    // ----------------------------------------------------------------------
    // Resource Management
    // ----------------------------------------------------------------------

    public void initializeResources()
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] resourceConfigs = configuration.getChild( "resources" ).getChildren();

        for ( int i = 0; i < resourceConfigs.length; ++i )
        {
            try
            {
                if ( resourceConfigs[i].getName().equals( "jar-repository" ) )
                {
                    addJarRepository( new File( resourceConfigs[i].getValue() ) );
                }
                else if ( resourceConfigs[i].getName().equals( "directory" ) )
                {
                    File directory = new File( resourceConfigs[i].getValue() );

                    if ( directory.exists() && directory.isDirectory() )
                    {
                        classRealm.addConstituent( directory.toURL() );
                    }
                }
            }
            catch ( Exception e )
            {
                System.err.println( "error configuring resource: " + resourceConfigs[i].getValue() );
            }
        }
    }

    public void addJarResource( File jar )
        throws Exception
    {
        classRealm.addConstituent( jar.toURL() );
    }

    public void addJarRepository( File repository )
        throws Exception
    {
        if ( repository.exists() && repository.isDirectory() )
        {
            File[] jars = repository.listFiles();

            for ( int j = 0; j < jars.length; j++ )
            {
                if ( jars[j].getAbsolutePath().endsWith( ".jar" ) )
                {
                    addJarResource( jars[j] );
                }
            }
        }
        else
        {
            throw new Exception( "The specified JAR repository doesn't exist or is not a directory." );
        }
    }

    public Logger getLogger()
    {
        return super.getLogger();
    }

    public Object createComponentInstance( ComponentDescriptor componentDescriptor )
        throws Exception
    {
        String componentFactoryId = componentDescriptor.getComponentFactory();

        ComponentFactory componentFactory = null;

        if ( componentFactoryId != null )
        {
            componentFactory = componentFactoryManager.findComponentFactory( componentFactoryId );
        }
        else
        {
            componentFactory = componentFactoryManager.getDefaultComponentFactory();
        }

        return componentFactory.newInstance( componentDescriptor.getImplementation(), getClassLoader() );
    }

    public void composeComponent( Object component, ComponentDescriptor componentDescriptor )
        throws Exception
    {
        componentComposerManager.assembleComponent( component, componentDescriptor, this );
    }

    // ----------------------------------------------------------------------
    // Discovery
    // ----------------------------------------------------------------------

    public void registerComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        componentDiscovererManager.registerComponentDiscoveryListener( listener );
    }

    public void removeComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        componentDiscovererManager.removeComponentDiscoveryListener( listener );
    }
}
