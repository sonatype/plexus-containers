package org.codehaus.plexus;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
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

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    public DefaultPlexusContainer()
    {
        context = new DefaultContext();

        loggerManager = new ConsoleLoggerManager();

        enableLogging( loggerManager.getRootLogger() );
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

            try
            {
                componentManager = componentManagerManager.createComponentManager( descriptor, this );
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


    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        return lookup( role + id );
    }

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    public ComponentDescriptor getComponentDescriptor( String role )
    {
        ComponentDescriptor result = componentRepository.getComponentDescriptor( role );

        if ( result == null && parentContainer != null )
        {
            result = parentContainer.getComponentDescriptor( role );
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

    // ----------------------------------------------------------------------
    // Component Release
    // ----------------------------------------------------------------------

    public void release( Object component )
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

        ComponentManager componentManager = componentManagerManager.findComponentManagerByComponentInstance( component );

        componentManager.suspend( component );
    }

    public void resume( Object component )
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

        // Should be safe now to lookup any component aside from the cmm and lhm

        discoverComponents();

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

            List componentDescriptors = componentDiscoverer.findComponents( classRealm.getClassLoader() );

            for ( Iterator j = componentDescriptors.iterator(); j.hasNext(); )
            {
                ComponentDescriptor componentDescriptor = (ComponentDescriptor) j.next();

                componentRepository.addComponentDescriptor( componentDescriptor );
            }
        }
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
            if ( classLoader != null && classRealm != null )
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
                                             "most likely corrupt." );
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

        enableLogging( loggerManager.getRootLogger() );
    }

    private void initializeCoreComponents()
        throws Exception
    {
        // Component repository

        PlexusXStream builder = new PlexusXStream();

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
        return loggerManager.getRootLogger();
    }
}
