package org.codehaus.plexus;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.composition.DefaultComponentComposer;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.DefaultComponentConfigurator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.DefaultComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.ComponentRepositoryFactory;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.configuration.builder.XmlPullConfigurationBuilder;
import org.codehaus.plexus.configuration.xstream.XStreamTool;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.lifecycle.DefaultLifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.LoggerManagerFactory;
import org.codehaus.plexus.personality.plexus.PlexusLifecycleHandler;
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
 */
public class DefaultPlexusContainer
    extends AbstractLogEnabled
    implements PlexusContainer
{
    private LoggerManager loggerManager;

    private DefaultContext context;

    private ComponentRepository componentRepository;

    private PlexusConfiguration configuration;

    private PlexusConfiguration defaultConfiguration;

    private PlexusConfiguration mergedConfiguration;

    private Reader configurationReader;

    private ClassWorld classWorld;

    private ClassLoader classLoader;

    private XmlPullConfigurationBuilder builder;

    private ComponentConfigurator componentConfigurator;

    private ComponentComposer componentComposer;

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    public DefaultPlexusContainer()
    {
        builder = new XmlPullConfigurationBuilder();
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

        ComponentManager componentManager = getComponentManager( componentKey );

        if ( componentManager == null )
        {
            ComponentDescriptor descriptor = componentRepository.getComponentDescriptor( componentKey );

            if ( descriptor == null )
            {
                getLogger().error( "Non existant component: " + componentKey );

                String message = "Component descriptor cannot be found in the component repository: " + componentKey + ".";

                throw new ComponentLookupException( message );
            }

            try
            {
                componentManager = instantiateComponentManager( descriptor );
            }
            catch ( Exception e )
            {
                String message = "Cannot create component manager for " + componentKey + ", so we cannot provide a component instance. ";

                getLogger().error( message, e );

                throw new ComponentLookupException( message, e );
            }
            try
            {
                component = componentManager.getComponent();
            }
            catch ( Exception e )
            {
                String message = "Cannot create component for " + componentKey + ".";

                getLogger().error( message, e );

                throw new ComponentLookupException( message, e );
            }

            componentManagersByComponentClass.put( component.getClass().getName(), componentManager );
        }
        else
        {
            try
            {
                component = componentManager.getComponent();
            }
            catch ( Exception e )
            {
                String message = "Cannot create component for " + componentKey + ".";

                throw new ComponentLookupException( message, e );
            }
        }

        return component;
    }

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        Map components = new HashMap();

        Map componentDescriptors = componentRepository.getComponentDescriptorMap( role );

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

        Map componentDescriptors = componentRepository.getComponentDescriptorMap( role );

        if ( componentDescriptors != null )
        {
            // Now we have a map of component descriptors keyed by role hint.

            for ( Iterator i = componentDescriptors.keySet().iterator(); i.hasNext(); )
            {
                String roleHint = (String) i.next();

                Object component = lookup( role, roleHint );

                components.add( component );
            }
        }

        return components;
    }


    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        return lookup( role + id );
    }

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    public Map getComponentDescriptorMap( String role )
    {
        return componentRepository.getComponentDescriptorMap( role );
    }

    public ComponentDescriptor getComponentDescriptor( String role )
    {
        return componentRepository.getComponentDescriptor( role );
    }

    // ----------------------------------------------------------------------
    // Component Release
    // ----------------------------------------------------------------------

    public void releaseAll( Map components )
    {
        for ( Iterator i = components.values().iterator(); i.hasNext(); )
        {
            Object component = i.next();

            release( component );
        }
    }

    public void releaseAll( List components )
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

    public boolean hasComponent( String role, String id )
    {
        return componentRepository.hasComponent( role, id );
    }

    public void suspend( Object component )
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = findComponentManager( component );

        componentManager.suspend( component );
    }

    public void resume( Object component )
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = findComponentManager( component );

        componentManager.resume( component );
    }

    public void release( Object component )
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = findComponentManager( component );

        componentManager.release( component );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        initializeClassLoader();

        initializeConfiguration();

        initializeLoggerManager();

        initializeResources();

        initializeComponentRepository();

        initializeComponentConfigurator();

        initializeComponentComposer();

        initializeLifecycleHandlerManager();

        initializeComponentManagerManager();

        initializeContext();

        initializeSystemProperties();
    }

    public void start()
        throws Exception
    {
        loadComponentsOnStart();

        configuration = null;

        defaultConfiguration = null;

        mergedConfiguration = null;
    }

    public void dispose()
    {
        disposeAllComponents();
    }

    protected void disposeAllComponents()
    {
        for ( Iterator iter = getComponentManagers().values().iterator(); iter.hasNext(); )
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

        componentManagers.clear();
    }

    // ----------------------------------------------------------------------
    // Pre-initialization - can only be called prior to initialization
    // ----------------------------------------------------------------------

    public void addContextValue( Object key, Object value )
    {
        getContext().put( key, value );
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public void setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;
    }

    /** @see PlexusContainer#setConfigurationResource(Reader) */
    public void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException
    {
        this.configurationReader = configuration;
    }

    // ----------------------------------------------------------------------
    // Post-initialization - can only be called post initialization
    // ----------------------------------------------------------------------

    public ClassLoader getClassLoader()
    {
        if ( classLoader == null )
        {
            try
            {
                classLoader = getClassWorld().getRealm( "core" ).getClassLoader();
            }
            catch ( NoSuchRealmException e )
            {
                throw new IllegalStateException( "There must be a core ClassWorlds realm." );
            }
        }

        return classLoader;
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    /**
     *  Load specifies roles during server startup.
     */
    protected void loadComponentsOnStart()
        throws Exception
    {
        PlexusConfiguration[] loadOnStartServices = configuration.getChild( "load-on-start" ).getChildren( "service" );

        for ( int i = 0; i < loadOnStartServices.length; i++ )
        {
            String role = loadOnStartServices[i].getAttribute( "role" );

            String id = loadOnStartServices[i].getAttribute( "id", "" );

            getLogger().info( "Loading on start [role,id]: " + "[" + role + "," + id + "]" );

            try
            {
                if ( id.length() == 0 )
                {
                    lookup( role );
                }
                else
                {
                    lookup( role, id );
                }
            }
            catch ( ComponentLookupException e )
            {
                getLogger().error( "Cannot load-on-start " + role, e );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Initialization Implementation
    // ----------------------------------------------------------------------

    private void initializeClassLoader()
        throws Exception
    {
        if ( getClassWorld() != null )
        {
            try
            {
                classLoader = getClassWorld().getRealm( "core" ).getClassLoader();
            }
            catch ( NoSuchRealmException e )
            {
            }
        }
        else
        {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
    }

    private void initializeContext()
    {
        addContextValue( PlexusConstants.PLEXUS_KEY, this );

        addContextValue( PlexusConstants.COMMON_CLASSLOADER, getClassLoader() );
    }

    private void initializeConfiguration()
        throws Exception
    {
        // System configuration

        InputStream is = getClassLoader().getResourceAsStream( "org/codehaus/plexus/plexus.conf" );

        if ( is == null )
        {
            throw new IllegalStateException( "The internal default plexus.conf is missing. " +
                                             "This is highly irregular, your plexus JAR is " +
                                             "most likely corrupt." );
        }

        defaultConfiguration = builder.parse( new InputStreamReader( is ) );

        // User configuration

        configuration = builder.parse( getInterpolationConfigurationReader( configurationReader ) );

        processConfigurationsDirectory();

        // Merger of system and user configuration

        mergedConfiguration = getMergedConfiguration();
    }

    private Reader getInterpolationConfigurationReader( Reader reader )
    {
        InterpolationFilterReader interpolationFilterReader =
            new InterpolationFilterReader( reader, new ContextMapAdapter( getContext() ) );

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
            DefaultPlexusConfiguration componentsConfiguration =
                (DefaultPlexusConfiguration) configuration.getChild( "components" );

            File configurationsDirectory = new File( s );

            if ( configurationsDirectory.exists()
                &&
                configurationsDirectory.isDirectory() )
            {
                List confs = FileUtils.getFiles( configurationsDirectory, "**/*.conf", "**/*.xml" );

                for ( Iterator i = confs.iterator(); i.hasNext(); )
                {
                    File conf = (File) i.next();

                    PlexusConfiguration c = builder.parse( getInterpolationConfigurationReader( new FileReader( conf ) ) );

                    componentsConfiguration.addAllChildren( c.getChild( "components" ) );
                }
            }
        }
    }

    private PlexusConfiguration getMergedConfiguration()
        throws Exception
    {
        return PlexusConfigurationMerger.merge( configuration, defaultConfiguration );
    }

    private void initializeLoggerManager()
        throws Exception
    {
        loggerManager = LoggerManagerFactory.create( mergedConfiguration.getChild( "logging" ), getClassLoader() );

        enableLogging( loggerManager.getRootLogger() );
    }

    private void initializeComponentRepository()
        throws Exception
    {
        componentRepository = ComponentRepositoryFactory.create( mergedConfiguration, getClassLoader() );
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
    // Internal Accessors
    // ----------------------------------------------------------------------

    private DefaultContext getContext()
    {
        if ( context == null )
        {
            context = new DefaultContext();
        }

        return context;
    }

    private ClassWorld getClassWorld()
    {
        if ( classWorld == null )
        {
            classWorld = new ClassWorld();

            try
            {
                classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );
            }
            catch ( DuplicateRealmException e )
            {
            }

            Thread.currentThread().setContextClassLoader( getClassLoader() );
        }
        return classWorld;
    }

    // ----------------------------------------------------------------------
    // Component Configurator
    // ----------------------------------------------------------------------

    private void initializeComponentConfigurator()
        throws Exception
    {
        componentConfigurator = new DefaultComponentConfigurator();
    }

    // ----------------------------------------------------------------------
    // Component Configurator
    // ----------------------------------------------------------------------

    private void initializeComponentComposer()
        throws Exception
    {
        componentComposer = new DefaultComponentComposer();
    }

    // ----------------------------------------------------------------------
    // Component Managers
    // ----------------------------------------------------------------------

    private ComponentManagerManager componentManagerManager;

    private Map componentManagers = new HashMap();

    private Map componentManagersByComponentClass = new HashMap();

    private void initializeComponentManagerManager()
        throws Exception
    {
        XStreamTool builder = new XStreamTool();

        builder.alias( "component-manager-manager", DefaultComponentManagerManager.class );

        PlexusConfiguration c = mergedConfiguration.getChild( "component-manager-manager" );

        componentManagerManager = (ComponentManagerManager) builder.build( c, DefaultComponentManagerManager.class );
    }

    public ComponentManager instantiateComponentManager( ComponentDescriptor descriptor )
        throws Exception
    {
        String lifecycleHandlerId = descriptor.getLifecycleHandler();

        LifecycleHandler lifecycleHandler;

        if ( lifecycleHandlerId == null )
        {
            lifecycleHandler = lifecycleHandlerManager.getDefaultLifecycleHandler();
        }
        else
        {
            lifecycleHandler = lifecycleHandlerManager.getLifecycleHandler( lifecycleHandlerId );
        }

        String instantiationId = descriptor.getInstantiationStrategy();

        ComponentManager componentManager;

        if ( instantiationId == null )
        {
            componentManager = componentManagerManager.getDefaultComponentManager();
        }
        else
        {
            componentManager = componentManagerManager.getComponentManager( instantiationId );
        }

        componentManager.setup( loggerManager.getLogger( "component-manager" ),
                                getClassLoader(),
                                lifecycleHandler,
                                descriptor );

        componentManager.initialize();

        //make the ComponentManager available for future requests
        componentManagers.put( descriptor.getComponentKey(), componentManager );

        return componentManager;
    }

    Map getComponentManagers()
    {
        return componentManagers;
    }

    ComponentManager getComponentManager( String componentKey )
    {
        return (ComponentManager) getComponentManagers().get( componentKey );
    }

    protected ComponentManager findComponentManager( Object component )
    {
        return (ComponentManager) componentManagersByComponentClass.get( component.getClass().getName() );
    }

    // ----------------------------------------------------------------------
    // Lifecycle Handlers
    // ----------------------------------------------------------------------

    private LifecycleHandlerManager lifecycleHandlerManager;

    private void initializeLifecycleHandlerManager()
        throws Exception
    {
        XStreamTool builder = new XStreamTool();

        builder.alias( "lifecycle-handler-manager", DefaultLifecycleHandlerManager.class );

        PlexusConfiguration c = mergedConfiguration.getChild( "lifecycle-handler-manager" );

        lifecycleHandlerManager = (LifecycleHandlerManager) builder.build( c, DefaultLifecycleHandlerManager.class );

        lifecycleHandlerManager.addEntity( LifecycleHandler.LOGGER, loggerManager.getRootLogger() );

        lifecycleHandlerManager.addEntity( LifecycleHandler.CONTEXT, context );

        lifecycleHandlerManager.addEntity( LifecycleHandler.COMPONENT_REPOSITORY, componentRepository );

        lifecycleHandlerManager.addEntity( LifecycleHandler.PLEXUS_CONTAINER, this );

        lifecycleHandlerManager.addEntity( PlexusLifecycleHandler.COMPONENT_CONFIGURATOR, componentConfigurator );

        lifecycleHandlerManager.addEntity( "componentComposer", componentComposer );

        lifecycleHandlerManager.initialize();
    }

    // ----------------------------------------------------------------------
    // Resource Management
    // ----------------------------------------------------------------------

    public void initializeResources()
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] resourceConfigs = mergedConfiguration.getChild( "resources" ).getChildren();

        for ( int i = 0; i < resourceConfigs.length; ++i )
        {
            try
            {
                if ( resourceConfigs[i].getName().equals( "jar-repository" ) )
                {
                    addJarRepository( new File( resourceConfigs[i].getValue() ) );
                }
            }
            catch ( Exception e )
            {
                getLogger().error( "error configuring resource: " + resourceConfigs[i].getValue(), e );
            }
        }
    }

    public void addJarResource( File jar )
        throws Exception
    {
        classWorld.getRealm( "core" ).addConstituent( jar.toURL() );
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
}
