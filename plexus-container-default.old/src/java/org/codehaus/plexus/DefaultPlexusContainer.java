package org.codehaus.plexus;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.composition.DefaultComponentComposer;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.DefaultComponentConfigurator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.DefaultComponentManagerManager;
import org.codehaus.plexus.component.manager.InstanceManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
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

    private ComponentRepository componentRepository;

    private PlexusConfiguration configuration;

    private Reader configurationReader;

    private ClassWorld classWorld;

    private ClassRealm classRealm;

    private ClassLoader classLoader;

    private XmlPullConfigurationBuilder builder;

    private ComponentConfigurator componentConfigurator;

    private ComponentComposer componentComposer;

    private ComponentManagerManager componentManagerManager;

    private Map componentManagers = new HashMap();

    private Map instanceManagers = new HashMap();

    private LifecycleHandlerManager lifecycleHandlerManager;

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    public DefaultPlexusContainer()
    {
        builder = new XmlPullConfigurationBuilder();

        context = new DefaultContext();
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
                if ( parentContainer != null )
                {
                    return parentContainer.lookup( componentKey );
                }

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

        String componentClass = component.getClass().getName();

        InstanceManager instanceManager = getInstanceManager( componentClass );

        if ( instanceManager == null )
        {
            instanceManager = componentManager.createInstanceManager();

            instanceManagers.put( componentClass, instanceManager );
        }

        instanceManager.register( component, componentManager );

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

    public void release( Object component )
    {
        if ( component == null )
        {
            return;
        }

        InstanceManager instanceManager = getInstanceManager( component.getClass().getName() );

        ComponentManager componentManager = null;

        if ( instanceManager != null )
        {
            componentManager = instanceManager.findComponentManager( component );
        }

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
            if ( componentManager.release( component ) )
            {    
                instanceManager.release( component );
            }
        }
    }

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

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        initializeClassWorlds();

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
    // ClassWorlds, ClassRealms and ClassLoaders
    // ----------------------------------------------------------------------

    // 1. Embedder may set a ClassLoader
    // 2. Classworlds launcher hands us a Classworld

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;
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
            if ( classLoader != null && 
                 classRealm != null )
            {
                classRealm = classWorld.newRealm( "core", classLoader );
            }
            else
            {
                classRealm = classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );
            }
        }
        
        classLoader = classRealm.getClassLoader();

        Thread.currentThread().setContextClassLoader( classLoader );
    }

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    private void initializeContext()
    {
        addContextValue( PlexusConstants.PLEXUS_KEY, this );

        addContextValue( PlexusConstants.COMMON_CLASSLOADER, getClassLoader() );
    }

    private void initializeConfiguration()
        throws Exception
    {
        // System userConfiguration

        InputStream is = getClassLoader().getResourceAsStream( "org/codehaus/plexus/plexus.conf" );

        if ( is == null )
        {
            throw new IllegalStateException( "The internal default plexus.conf is missing. " +
                                             "This is highly irregular, your plexus JAR is " +
                                             "most likely corrupt." );
        }

        PlexusConfiguration systemConfiguration = builder.parse( new InputStreamReader( is ) );

        if ( configurationReader != null )
        {
            // User userConfiguration

            PlexusConfiguration userConfiguration = builder.parse( getInterpolationConfigurationReader( configurationReader ) );

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
            DefaultPlexusConfiguration componentsConfiguration =
                (DefaultPlexusConfiguration) configuration.getChild( "components" );

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
                        builder.parse( getInterpolationConfigurationReader( new FileReader( componentConfigurationFile ) ) );

                    componentsConfiguration.addAllChildren( componentConfiguration.getChild( "components" ) );
                }
            }
        }
    }

    private void initializeLoggerManager()
        throws Exception
    {
        String implementation = configuration.getChild( "logging" ).getChild( "implementation" ).getValue( null );

        loggerManager = (LoggerManager) classLoader.loadClass( implementation ).newInstance();

        loggerManager.configure( configuration );

        loggerManager.initialize();

        loggerManager.start();

        enableLogging( loggerManager.getRootLogger() );
    }

    private void initializeComponentRepository()
        throws Exception
    {
        String implementation = configuration.getChild( "component-repository" ).getChild( "implementation" ).getValue();

        componentRepository = (ComponentRepository) classLoader.loadClass( implementation ).newInstance();

        componentRepository.configure( configuration );

        componentRepository.setClassRealm( classRealm );

        componentRepository.initialize();
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

    private void initializeComponentManagerManager()
        throws Exception
    {
        XStreamTool builder = new XStreamTool();

        builder.alias( "component-manager-manager", DefaultComponentManagerManager.class );

        PlexusConfiguration c = configuration.getChild( "component-manager-manager" );

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
        InstanceManager instanceManager = getInstanceManager( component.getClass().getName() );

        if ( instanceManager != null )
        {
            return instanceManager.findComponentManager( component );
        }

        return null;
    }

    // ----------------------------------------------------------------------
    // Instance Managers
    // ----------------------------------------------------------------------

    InstanceManager getInstanceManager( String componentClass )
    {
        return (InstanceManager) instanceManagers.get( componentClass );
    }

    // ----------------------------------------------------------------------
    // Lifecycle Handlers
    // ----------------------------------------------------------------------

    private void initializeLifecycleHandlerManager()
        throws Exception
    {
        XStreamTool builder = new XStreamTool();

        builder.alias( "lifecycle-handler-manager", DefaultLifecycleHandlerManager.class );

        PlexusConfiguration c = configuration.getChild( "lifecycle-handler-manager" );

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
                getLogger().error( "error configuring resource: " + resourceConfigs[i].getValue(), e );
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
                    System.out.println( "jars[j] = " + jars[j] );

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
