package org.codehaus.plexus;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.classloader.DefaultResourceManager;
import org.codehaus.plexus.classloader.ResourceManagerFactory;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.DefaultComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.ComponentRepositoryFactory;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.DefaultComponentConfigurator;
import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.configuration.ConfigurationMerger;
import org.codehaus.plexus.configuration.ConfigurationResourceException;
import org.codehaus.plexus.configuration.DefaultConfiguration;
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
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** The main Plexus container component.
 *
 *  @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 *
 *  @todo Make ClassWorlds optional so we can make the runtime tiny.
 */
public class DefaultPlexusContainer
    extends AbstractLogEnabled
    implements PlexusContainer
{
    // ----------------------------------------------------------------------
    //  Instance Members
    // ----------------------------------------------------------------------

    /** Logger Manager used for this container. */
    private LoggerManager loggerManager;

    /** Context used for this container. */
    private DefaultContext context;

    /** Service Repository used for this container. */
    private ComponentRepository componentRepository;

    /** Configuration for this container. */
    private Configuration configuration;

    private Configuration defaultConfiguration;

    private Configuration mergedConfiguration;

    /** The configuration resource. */
    private Reader configurationReader;

    private ClassWorld classWorld;

    /** Class loader used for this container if a class world is not available. */
    private ClassLoader classLoader;

    private DefaultResourceManager resourceManager;

    /** Default Configuration Builder. */
    private XmlPullConfigurationBuilder builder;

    /** XML element used to start the logging configuration block. */
    public static final String LOGGING_TAG = "logging";

    private ComponentConfigurator componentConfigurator;

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

    public Map lookupAll( String role )
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

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        return lookup( role + id );
    }

    public void releaseAll( Map components )
    {
        for ( Iterator i = components.values().iterator(); i.hasNext(); )
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

        initializeDefaultConfiguration();

        initializeConfiguration();

        initializeLoggerManager();

        initializeComponentRepository();

        initializeLifecycleHandlerManager();

        initializeComponentManagerManager();

        initializeComponentConfigurator();

        initializeResourceManager();

        initializeContext();

        initializeSystemProperties();
    }

    public void start()
        throws Exception
    {
        loadOnStart();
    }

    public void dispose()
    {
        getLogger().info( "Disposing ComponentRepository..." );

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

        getLogger().info( "ComponentRepository disposed." );
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
        throws ConfigurationResourceException
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
            throw new IllegalStateException( "This container must be assigned a ClassLoader." );
        }

        return classLoader;
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    /**
     *  Load specifies roles during server startup.
     */
    protected void loadOnStart()
        throws Exception
    {
        Configuration[] loadOnStartServices = configuration.getChild( "load-on-start" ).getChildren( "service" );

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

        addContextValue( PlexusConstants.RESOURCE_MANAGER_KEY, resourceManager );

        addContextValue( PlexusConstants.COMMON_CLASSLOADER, getClassLoader() );
    }

    private void initializeDefaultConfiguration()
        throws Exception
    {
        InputStream is = getClassLoader().getResourceAsStream( "org/codehaus/plexus/plexus.conf" );

        if ( is == null )
        {
            throw new IllegalStateException( "The internal default plexus.conf is missing. " +
                                             "This is highly irregular, your plexus JAR is " +
                                             "most likely corrupt." );
        }

        setDefaultConfiguration( builder.parse( new InputStreamReader( is ) ) );
    }

    private void initializeConfiguration()
        throws Exception
    {
        setConfiguration( builder.parse( getInterpolationConfigurationReader( getConfigurationReader() ) ) );

        processConfigurationsDirectory();
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
        String s = getConfiguration().getChild( "configurations-directory" ).getValue( null );

        if ( s != null )
        {
            DefaultConfiguration componentsConfiguration =
                (DefaultConfiguration) getConfiguration().getChild( "components" );

            File configurationsDirectory = new File( s );

            if ( configurationsDirectory.exists()
                &&
                configurationsDirectory.isDirectory() )
            {
                DirectoryScanner scanner = new DirectoryScanner();

                scanner.setBasedir( configurationsDirectory );

                scanner.setIncludes( new String[]{"**/*.conf", "**/*.xml"} );

                scanner.scan();

                String[] confs = scanner.getIncludedFiles();

                for ( int i = 0; i < confs.length; i++ )
                {
                    File conf = new File( configurationsDirectory, confs[i] );

                    Configuration c = builder.parse( getInterpolationConfigurationReader( new FileReader( conf ) ) );

                    componentsConfiguration.addAllChildren( c.getChild( "components" ) );
                }
            }
        }
    }

    private Configuration getMergedConfiguration()
        throws Exception
    {
        if ( mergedConfiguration == null )
        {
            mergedConfiguration = ConfigurationMerger.merge( getConfiguration(), getDefaultConfiguration() );

            // A little tweak for the lifecycle handlers

            Configuration[] lifecycleHandlers = getConfiguration().getChild( "lifecycle-handlers" ).getChildren( "lifecycle-handler" );

            if ( lifecycleHandlers != null )
            {
                DefaultConfiguration defaultLifecycleHandlers =
                    (DefaultConfiguration) mergedConfiguration.getChild( "lifecycle-handler-manager" ).getChild( "lifecycle-handlers" );

                for ( int i = 0; i < lifecycleHandlers.length; i++ )
                {
                    defaultLifecycleHandlers.addChild( lifecycleHandlers[i] );
                }
            }
        }

        return mergedConfiguration;
    }

    private void initializeLoggerManager()
        throws Exception
    {
        LoggerManager loggerManager = LoggerManagerFactory.create( getMergedConfiguration().getChild( LOGGING_TAG ), getClassLoader() );

        enableLogging( loggerManager.getRootLogger() );

        setLoggerManager( loggerManager );
    }

    private void initializeComponentRepository()
        throws Exception
    {
        ComponentRepository componentRepository = ComponentRepositoryFactory.create( getMergedConfiguration(),
                                                                                     getClassLoader() );
        setComponentRepository( componentRepository );
    }

    private void initializeResourceManager()
        throws Exception
    {
        DefaultResourceManager rm =
            ResourceManagerFactory.create( getMergedConfiguration(),
                                           getLoggerManager(),
                                           getClassLoader() );

        // This needs to be completely clarified. If the container becomes the boundary
        // and barrier between all behaviour in plexus then the subsystems like classworlds
        // can't undermine the barrier. This behaviour is also dependent on composite
        // and primitive components a la SOFA.

        setResourceManager( rm );

        setClassLoader( rm.getPlexusClassLoader() );

        Thread.currentThread().setContextClassLoader( getClassLoader() );
    }

    /**
     * Initialize system properties.
     *
     * If the application needs to setup any system properties than they will
     * be initialized here.
     *
     * @throws Exception
     */
    private void initializeSystemProperties()
        throws Exception
    {
        Configuration[] systemProperties =
            getConfiguration().getChild( "system-properties" ).getChildren( "property" );

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

    private void setLoggerManager( LoggerManager loggerManager )
    {
        this.loggerManager = loggerManager;
    }

    private LoggerManager getLoggerManager()
    {
        return loggerManager;
    }

    private Configuration getConfiguration()
    {
        return configuration;
    }

    private void setConfiguration( Configuration configuration )
    {
        this.configuration = configuration;
    }

    private Reader getConfigurationReader()
    {
        return configurationReader;
    }

    private void setResourceManager( DefaultResourceManager resourceManager )
    {
        this.resourceManager = resourceManager;
    }

    private DefaultContext getContext()
    {
        if ( context == null )
        {
            context = new DefaultContext();
        }

        return context;
    }

    private void setComponentRepository( ComponentRepository componentRepository )
    {
        this.componentRepository = componentRepository;
    }

    private Configuration getDefaultConfiguration()
    {
        return defaultConfiguration;
    }

    private void setDefaultConfiguration( Configuration defaultConfiguration )
    {
        this.defaultConfiguration = defaultConfiguration;
    }

    private ClassWorld getClassWorld()
    {
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

        Configuration c = mergedConfiguration.getChild( "component-manager-manager" );

        componentManagerManager = (ComponentManagerManager) builder.build( (Configuration) c, DefaultComponentManagerManager.class );
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

        Configuration c = mergedConfiguration.getChild( "lifecycle-handler-manager" );

        lifecycleHandlerManager = (LifecycleHandlerManager) builder.build( (Configuration) c, DefaultLifecycleHandlerManager.class );

        lifecycleHandlerManager.initialize( loggerManager, context, componentRepository );
    }
}
