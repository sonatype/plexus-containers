package org.codehaus.plexus;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.discovery.DiscoveryListenerDescriptor;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.configuration.processor.ConfigurationProcessor;
import org.codehaus.plexus.configuration.processor.DirectoryConfigurationResourceHandler;
import org.codehaus.plexus.configuration.processor.FileConfigurationResourceHandler;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
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

/**
 * @todo clarify configuration handling vis-a-vis user vs default values
 * @todo use classworlds whole hog, plexus' concern is applications.
 * @todo allow setting of a live configuraton so applications that embed plexus
 * can use whatever configuration mechanism they like. They just have to
 * adapt it into something plexus can understand.
 */
public class DefaultPlexusContainer
    extends AbstractLogEnabled
    implements PlexusContainer
{
    private PlexusContainer parentContainer;

    private LoggerManager loggerManager;

    private DefaultContext context;

    protected PlexusConfiguration configuration;

    private Reader configurationReader;

    private ClassWorld classWorld;

    private ClassRealm coreRealm;

    private ClassRealm plexusRealm;

    private String name;

    private ComponentRepository componentRepository;

    private ComponentManagerManager componentManagerManager;

    private LifecycleHandlerManager lifecycleHandlerManager;

    private ComponentDiscovererManager componentDiscovererManager;

    private ComponentFactoryManager componentFactoryManager;

    private ComponentComposerManager componentComposerManager;

    private Map realmAliases;

    public static final String BOOTSTRAP_CONFIGURATION = "org/codehaus/plexus/plexus-bootstrap.xml";

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

                getLogger().error( "Nonexistent component: " + componentKey );

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

    protected void addRealmAlias( String alias, String realmId )
    {
        realmAliases.put( alias, realmId );
    }

    public ClassRealm getComponentRealm( String id )
    {
        ClassRealm classRealm;

        try
        {
            // If there is an alias then use the alias
            // We still need to account for the case where a component
            // is a dependency of more than one component which is in
            // an entirely different realm.

            if ( realmAliases.get( id ) != null )
            {
                id = (String) realmAliases.get( id );
            }

            classRealm = classWorld.getRealm( id );
        }
        catch ( NoSuchRealmException e )
        {
            classRealm = plexusRealm;
        }

        return classRealm;
    }

    protected Map getRealmAliases()
    {
        return realmAliases;
    }

    public void initialize()
        throws Exception
    {
        realmAliases = new HashMap();

        initializeClassWorlds();

        initializeConfiguration();

        initializeResources();

        initializeCoreComponents();

        initializeLoggerManager();

        initializeContext();

        initializeSystemProperties();
    }

    public void registerComponentDiscoverytListeners()
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
    }

    // We are assuming that any component which is designated as a component discovery
    // listener is listed in the plexus.xml file that will be discovered and processed
    // before the components.xml are discovered in JARs and processed.

    /**
     * TODO: Enhance the ComponentRepository so that it can take entire
     * ComponentSetDescriptors instead of just ComponentDescriptors.
     *
     * @throws Exception
     */
    public List discoverComponents( ClassRealm classRealm )
        throws Exception
    {
        List discoveredComponentDescriptors = new ArrayList();

        for ( Iterator i = componentDiscovererManager.getComponentDiscoverers().iterator(); i.hasNext(); )
        {
            ComponentDiscoverer componentDiscoverer = (ComponentDiscoverer) i.next();

            List componentSetDescriptors = componentDiscoverer.findComponents( getContext(), classRealm );

            for ( Iterator j = componentSetDescriptors.iterator(); j.hasNext(); )
            {
                ComponentSetDescriptor componentSet = (ComponentSetDescriptor) j.next();

                List componentDescriptors = componentSet.getComponents();

                for ( Iterator k = componentDescriptors.iterator(); k.hasNext(); )
                {
                    ComponentDescriptor componentDescriptor = (ComponentDescriptor) k.next();

                    componentDescriptor.setComponentSetDescriptor( componentSet );

                    // If the user has already defined a component descriptor for this particular
                    // component then do not let the discovered component descriptor override
                    // the user defined one.
                    if ( getComponentDescriptor( componentDescriptor.getComponentKey() ) == null )
                    {
                        addComponentDescriptor( componentDescriptor );

                        // We only want to add components that have not yet been
                        // discovered in a parent realm. We don't quite have fine
                        // grained control over this right now but this is for
                        // dynamic additions which are only happening from maven
                        // at the moment. And plugins have a parent realm and
                        // a grand parent realm so if the component has been
                        // discovered it's most likely in those realms.

                        // I actually need to keep track of what realm a component
                        // was discovered in so that i can accurately search the
                        // parents.

                        discoveredComponentDescriptors.add( componentDescriptor );
                    }
                }

                //discoveredComponentDescriptors.addAll( componentDescriptors );
            }
        }

        return discoveredComponentDescriptors;
    }

    // We need to be aware of dependencies between discovered components when the listed component
    // as the discovery listener itself depends on components that need to be discovered.

    public void start()
        throws Exception
    {
        registerComponentDiscoverytListeners();

        discoverComponents( plexusRealm );

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

    /**
     * @see PlexusContainer#setConfigurationResource(Reader)
     */
    public void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException
    {
        this.configurationReader = configuration;
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    protected void loadComponentsOnStart()
        throws PlexusConfigurationException, ComponentLookupException
    {
        PlexusConfiguration[] loadOnStartComponents = configuration.getChild( "load-on-start" ).getChildren( "component" );

        getLogger().debug( "Found " + loadOnStartComponents.length + " components to load on start" );

        for ( int i = 0; i < loadOnStartComponents.length; i++ )
        {
            String role = loadOnStartComponents[i].getChild( "role" ).getValue( null );

            String roleHint = loadOnStartComponents[i].getChild( "role-hint" ).getValue();

            if ( role == null )
            {
                throw new PlexusConfigurationException( "Missing 'role' element from load-on-start." );
            }

            if ( roleHint == null )
            {
                getLogger().info( "Loading on start [role]: " + "[" + role + "]" );

                lookup( role );
            }
            else if ( roleHint.equals("*") )
            {
                getLogger().info( "Loading on start all components with [role]: " + "[" + role + "]" );

                lookupList( role );
            }
            else
            {
                getLogger().info( "Loading on start [role,roleHint]: " + "[" + role + "," + roleHint + "]" );

                lookup( role, roleHint );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Misc Configuration
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public void setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;
    }

    public ClassRealm getCoreRealm()
    {
        return coreRealm;
    }

    public void setCoreRealm( ClassRealm coreRealm )
    {
        this.coreRealm = coreRealm;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void initializeClassWorlds()
        throws Exception
    {
        if ( classWorld == null )
        {
            classWorld = new ClassWorld();
        }

        // Create a name for our application if one doesn't exist.
        initializeName();

        if ( coreRealm == null )
        {
            try
            {
                coreRealm = classWorld.getRealm( "plexus.core" );
            }
            catch ( NoSuchRealmException e )
            {
                /* We are being loaded with someone who hasn't
                 * given us any classworlds realms.  In this case,
                 * we want to use the classes already in the
                 * ClassLoader for our realm.
                 */
                coreRealm = classWorld.newRealm( "plexus.core", Thread.currentThread().getContextClassLoader() );
            }
        }

        // We are in a non-embedded situation
        if ( plexusRealm == null )
        {
            try
            {
                plexusRealm = coreRealm.getWorld().getRealm( "plexus.core.maven" );
            }
            catch ( NoSuchRealmException e )
            {
                //plexusRealm = coreRealm.getWorld().newRealm( "plexus.core.maven" );

                // If no app realm can be found then we will make the plexusRealm
                // the same as the app realm.

                plexusRealm = coreRealm;
            }

            //plexusRealm.importFrom( coreRealm.getId(), "" );

            addContextValue( "common.classloader", plexusRealm.getClassLoader() );

            Thread.currentThread().setContextClassLoader( plexusRealm.getClassLoader() );
        }
    }

    protected ClassRealm getContainerRealm() {
        return plexusRealm;
    }


    private void initializeClassWorlds2()
        throws Exception
    {
        if ( classWorld == null )
        {
            classWorld = new ClassWorld();
        }

        // Create a name for our application if one doesn't exist.
        initializeName();

        if ( coreRealm == null )
        {
            try
            {
                coreRealm = classWorld.getRealm( "plexus.core" );
            }
            catch ( NoSuchRealmException e )
            {
                /* We are being loaded with someone who hasn't
                 * given us any classworlds realms.  In this case,
                 * we want to use the classes already in the
                 * ClassLoader for our realm.
                 */
                plexusRealm = classWorld.newRealm( "plexus.core", Thread.currentThread().getContextClassLoader() );
            }
        }

        // We are in a non-embedded situation
        if ( plexusRealm == null )
        {
            try
            {
                plexusRealm = coreRealm.getWorld().getRealm( "plexus." + getName() );
            }
            catch ( NoSuchRealmException e )
            {
                plexusRealm = coreRealm.getWorld().newRealm( "plexus." + getName() );
            }

            plexusRealm.importFrom( coreRealm.getId(), "" );

            addContextValue( "common.classloader", plexusRealm.getClassLoader() );

            Thread.currentThread().setContextClassLoader( plexusRealm.getClassLoader() );
        }
    }

    /**
     * Create a name for our application if one doesn't exist.
     */
    protected void initializeName()
    {
        if ( name == null )
        {
            int i = 0;

            while ( true )
            {
                try
                {
                    classWorld.getRealm( "plexus.app" + i );
                    i++;
                }
                catch ( NoSuchRealmException e )
                {
                    setName( "app" + i );
                    return;
                }
            }
        }
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

        addContextValue( PlexusConstants.PLEXUS_CORE_REALM, plexusRealm );
    }

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    protected void initializeConfiguration()
        throws Exception
    {
        // System userConfiguration

        InputStream is = coreRealm.getResourceAsStream( BOOTSTRAP_CONFIGURATION );

        if ( is == null )
        {
            throw new IllegalStateException( "The internal default plexus.conf is missing. " +
                                             "This is highly irregular, your plexus JAR is " +
                                             "most likely corrupt." );
        }

        PlexusConfiguration systemConfiguration = PlexusTools.buildConfiguration( new InputStreamReader( is ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String PLEXUS_XML = "META-INF/plexus/plexus.xml";

        InputStream plexusXml = plexusRealm.getResourceAsStream( PLEXUS_XML );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        // Some of this could probably be collapsed as having a plexus.xml in your
        // META-INF/plexus directory is probably a better solution then specifying
        // a configuration with an URL but I'm leaving the configuration by URL
        // as folks might be using it ... I made this change to accomodate Maven
        // but I think it's better to discover a configuration in a standard
        // place.

        configuration = systemConfiguration;

        if ( plexusXml != null )
        {
            // User userConfiguration

            PlexusConfiguration plexusConfiguration =
                PlexusTools.buildConfiguration( getInterpolationConfigurationReader( new InputStreamReader( plexusXml ) ) );

            configuration = PlexusConfigurationMerger.merge( plexusConfiguration, configuration );

            processConfigurationsDirectory();
        }

        if ( configurationReader != null )
        {
            // User userConfiguration

            PlexusConfiguration userConfiguration =
                PlexusTools.buildConfiguration( getInterpolationConfigurationReader( configurationReader ) );

            // Merger of systemConfiguration and user userConfiguration

            configuration = PlexusConfigurationMerger.merge( userConfiguration, configuration );

            processConfigurationsDirectory();
        }

        // ---------------------------------------------------------------------------
        // Now that we have the configuration we will use the ConfigurationProcessor
        // to inline any external configuration instructions.
        //
        // At his point the variables in the configuration have already been
        // interpolated so we can send in an empty Map because the context
        // values are already there.
        // ---------------------------------------------------------------------------

        ConfigurationProcessor p = new ConfigurationProcessor();

        p.addConfigurationResourceHandler( new FileConfigurationResourceHandler() );

        p.addConfigurationResourceHandler( new DirectoryConfigurationResourceHandler() );

        configuration = p.process( configuration, new HashMap() );
    }

    protected Reader getInterpolationConfigurationReader( Reader reader )
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
            PlexusConfiguration componentsConfiguration = configuration.getChild( "components" );

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

                    componentsConfiguration.addChild( componentConfiguration.getChild( "components" ) );
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
        BasicComponentConfigurator configurator = new BasicComponentConfigurator();

        PlexusConfiguration c = configuration.getChild( "component-repository" );

        processCoreComponentConfiguration( "component-repository", configurator, c );

        componentRepository.configure( configuration );

        componentRepository.setClassRealm( plexusRealm );

        componentRepository.initialize();

        // Lifecycle handler manager

        c = configuration.getChild( "lifecycle-handler-manager" );

        processCoreComponentConfiguration( "lifecycle-handler-manager",configurator, c );

        lifecycleHandlerManager.initialize();

        // Component manager manager

        c = configuration.getChild( "component-manager-manager" );

        processCoreComponentConfiguration( "component-manager-manager",configurator,  c );

        componentManagerManager.setLifecycleHandlerManager( lifecycleHandlerManager );

        // Component discoverer manager

        c = configuration.getChild( "component-discoverer-manager" );

        processCoreComponentConfiguration( "component-discoverer-manager", configurator, c );

        componentDiscovererManager.initialize();

        // Component factory manager

        c = configuration.getChild( "component-factory-manager" );

        processCoreComponentConfiguration(  "component-factory-manager", configurator, c );

        // Component factory manager

        c = configuration.getChild( "component-composer-manager" );

        processCoreComponentConfiguration( "component-composer-manager", configurator, c );
    }

    private void processCoreComponentConfiguration( String role, BasicComponentConfigurator configurator, PlexusConfiguration c )
            throws ComponentConfigurationException
    {
        String implementation = c.getAttribute( "implementation", null);

        if ( implementation == null )
        {

            String msg = "Core component: '"+
                    role +
                    "' + which is needed by plexus to function properly cannot " +
                    "be instantiated. Implementation attribute was not specified in plexus.conf." +
                    "This is highly irregular, your plexus JAR is most likely corrupt.";

            throw new ComponentConfigurationException( msg );
        }

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( role );

        componentDescriptor.setImplementation( implementation );

        PlexusConfiguration configuration = new XmlPlexusConfiguration( "configuration" );

        configuration.addChild( c );

        configurator.configureComponent( this, componentDescriptor, configuration );
    }


    private void initializeSystemProperties()
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] systemProperties = configuration.getChild( "system-properties" ).getChildren( "property" );

        for ( int i = 0; i < systemProperties.length; ++i )
        {
            String name = systemProperties[i].getAttribute( "name" );

            String value = systemProperties[i].getAttribute( "value" );

            if ( name == null )
            {
                throw new PlexusConfigurationException( "Missing 'name' attribute in 'property' tag. " );
            }

            if ( value == null )
            {
                throw new PlexusConfigurationException( "Missing 'value' attribute in 'property' tag. " );
            }

            System.getProperties().setProperty( name, value );

            getLogger().info( "Setting system property: [ " + name + ", " + value + " ]" );
        }
    }

    // ----------------------------------------------------------------------
    // Resource Management
    // ----------------------------------------------------------------------

    // TODO: Do not swallow exception
    public void initializeResources()
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] resourceConfigs = configuration.getChild( "resources" ).getChildren();

        for ( int i = 0; i < resourceConfigs.length; ++i )
        {
            try
            {
                String name = resourceConfigs[i].getName();

                if ( name.equals( "jar-repository" ) )
                {
                    addJarRepository( new File( resourceConfigs[i].getValue() ) );
                }
                else if ( name.equals( "directory" ) )
                {
                    File directory = new File( resourceConfigs[i].getValue() );

                    if ( directory.exists() && directory.isDirectory() )
                    {
                        plexusRealm.addConstituent( directory.toURL() );
                    }
                }
                else
                {
                    getLogger().warn( "Unknown resource type: " + name );
                }
            }
            catch ( Exception e )
            {
                getLogger().error( "Error configuring resource: " + resourceConfigs[i].getName() + "=" + resourceConfigs[i].getValue(), e );
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void addJarResource( File jar )
        throws Exception
    {
        plexusRealm.addConstituent( jar.toURL() );
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
            getLogger().warn( "The specified JAR repository doesn't exist or is not a directory." );
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

        return componentFactory.newInstance( componentDescriptor, plexusRealm, this );
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
