package org.codehaus.plexus;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.setter.SetterComponentComposer;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.discovery.PlexusXmlComponentDiscoverer;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.configuration.processor.ConfigurationProcessingException;
import org.codehaus.plexus.configuration.processor.ConfigurationProcessor;
import org.codehaus.plexus.configuration.processor.ConfigurationResourceNotFoundException;
import org.codehaus.plexus.configuration.processor.DirectoryConfigurationResourceHandler;
import org.codehaus.plexus.configuration.processor.FileConfigurationResourceHandler;
import org.codehaus.plexus.container.initialization.ComponentDiscoveryPhase;
import org.codehaus.plexus.container.initialization.ContainerInitializationContext;
import org.codehaus.plexus.container.initialization.ContainerInitializationException;
import org.codehaus.plexus.container.initialization.ContainerInitializationPhase;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * //todo move lookup code to a separate component
 * //todo register live components so they can be wired
 * //keep track of the interfaces for components
 * //todo allow setting of a live configuraton so applications that embed plexus
 * can use whatever configuration mechanism they like. They just have to
 * adapt it into something plexus can understand.
 * //todo make a typesafe configuration model for the container
 * //todo pico like registration
 * //todo need loggers per execution like in the maven embedder
 * //todo a simple front-end to make containers of different flavours, a flavour encapsulating
 * //     a set of behaviours
 * //todo the core components should probably have a small lifecycle to pass through
 *
 * @author Jason van Zyl
 */
public class DefaultPlexusContainer
    extends AbstractLogEnabled
    implements MutablePlexusContainer
{
    protected static final String DEFAULT_CONTAINER_NAME = "default";

    protected static final String DEFAULT_REALM_NAME = "plexus.core";

    protected String name;

    protected PlexusContainer parentContainer;

    protected DefaultContext containerContext;

    protected PlexusConfiguration configuration;

    //todo: don't use a reader
    protected Reader configurationReader;

    protected ClassWorld classWorld;

    protected ClassRealm containerRealm;

    // ----------------------------------------------------------------------------
    // Core components
    // ----------------------------------------------------------------------------

    protected List initializationPhases;

    protected ComponentRepository componentRepository;

    protected ComponentManagerManager componentManagerManager;

    protected LifecycleHandlerManager lifecycleHandlerManager;

    protected ComponentDiscovererManager componentDiscovererManager;

    protected ComponentFactoryManager componentFactoryManager;

    protected ComponentLookupManager componentLookupManager;

    protected ComponentComposerManager componentComposerManager;

    protected LoggerManager loggerManager;

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    protected Map childContainers = new WeakHashMap();

    protected Date creationDate = new Date();

    protected boolean reloadingEnabled;

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    // Requirements
    // - container name
    // - ClassWorld
    // - Context values
    // - User space configuration
    // - ClassWorld

    // Do we need named realms?

    public DefaultPlexusContainer()
        throws PlexusContainerException
    {
        this( DEFAULT_CONTAINER_NAME, null, null, null );
    }

    public DefaultPlexusContainer( String name,
                                   Map context )
        throws PlexusContainerException
    {
        this( name, context, null );
    }

    public DefaultPlexusContainer( String name,
                                   Map context,
                                   String configuration )
        throws PlexusContainerException
    {
        this( name, context, configuration, null );
    }

    public DefaultPlexusContainer( String name,
                                   Map context,
                                   String configuration,
                                   ClassWorld classWorld )
        throws PlexusContainerException
    {
        this.name = name;

        // ----------------------------------------------------------------------------
        // ClassWorld
        // ----------------------------------------------------------------------------

        this.classWorld = classWorld;

        // Make sure we have a valid ClassWorld
        if ( this.classWorld == null )
        {
            this.classWorld = new ClassWorld( DEFAULT_REALM_NAME, Thread.currentThread().getContextClassLoader() );
        }

        try
        {
            containerRealm = this.classWorld.getRealm( DEFAULT_REALM_NAME );
        }
        catch ( NoSuchRealmException e )
        {
            List realms = new LinkedList( this.classWorld.getRealms() );
            containerRealm = (ClassRealm) realms.get( 0 );
        }

        // ----------------------------------------------------------------------------
        // Context
        // ----------------------------------------------------------------------------

        this.containerContext = new DefaultContext();

        if ( context != null )
        {
            for ( Iterator it = context.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                addContextValue( entry.getKey(), entry.getValue() );
            }
        }

        // ----------------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------------

        //TODO: just store reference to the configuration in a String and use that in the configuration
        // initialization
        if ( configuration != null )
        {
            //containerRealm.display();

            InputStream is = containerRealm.getResourceAsStream( configuration );

            if ( is == null )
            {
                throw new PlexusContainerException(
                    "The specified user configuration '" + configuration + "' is null." );
            }

            configurationReader = new InputStreamReader( is );
        }

        initialize();

        start();
    }

    // ----------------------------------------------------------------------------
    // Inheritance of Containers
    // ----------------------------------------------------------------------------

    public DefaultPlexusContainer( String name,
                                   Map context,
                                   String configuration,
                                   Set jars,
                                   PlexusContainer parentContainer )
        throws PlexusContainerException
    {
        this( name, context, configuration );

        this.parentContainer = parentContainer;

        this.loggerManager = parentContainer.getLoggerManager();

        //this.classWorld =  parentContainer.getClassWorld();

        this.containerRealm.setParentRealm( parentContainer.getContainerRealm() );
    }

    // ----------------------------------------------------------------------------
    // Lookup
    // ----------------------------------------------------------------------------

    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( componentKey );
    }

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role );
    }

    public List lookupList( String role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role );
    }

    public Object lookup( String role,
                          String roleHint )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint );
    }

    public Object lookup( Class componentClass )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( componentClass );
    }

    public Map lookupMap( Class role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role );
    }

    public List lookupList( Class role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role );
    }

    public Object lookup( Class role,
                          String roleHint )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint );
    }

    // ----------------------------------------------------------------------
    // Timestamping Methods
    // ----------------------------------------------------------------------

    public Date getCreationDate()
    {
        return creationDate;
    }

    // ----------------------------------------------------------------------
    // Child container access
    // ----------------------------------------------------------------------

    public boolean hasChildContainer( String name )
    {
        return childContainers.get( name ) != null;
    }

    public void removeChildContainer( String name )
    {
        childContainers.remove( name );
    }

    public PlexusContainer getChildContainer( String name )
    {
        return (PlexusContainer) childContainers.get( name );
    }

    // ----------------------------------------------------------------------------
    // Requirements
    // ----------------------------------------------------------------------------
    // o discover all components that are present in the artifact
    // o create a separate realm for the components
    // o retrieve all the dependencies for the artifact: this last part unfortunately
    //   only works in Maven where artifacts are downloaded
    // ----------------------------------------------------------------------------
    public ClassRealm createComponentRealm( String id,
                                            List jars )
        throws PlexusContainerException
    {
        ClassRealm componentRealm;

        try
        {
            return classWorld.getRealm( id );
        }
        catch ( NoSuchRealmException e )
        {
        }

        try
        {
            componentRealm = containerRealm.createChildRealm( id );
        }
        catch ( DuplicateRealmException e )
        {
            throw new PlexusContainerException( "Error creating child realm.", e );
        }

        try
        {
            for ( Iterator it = jars.iterator(); it.hasNext(); )
            {
                Object next = it.next();

                File jar = (File) next;

                componentRealm.addURL( jar.toURI().toURL() );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new PlexusContainerException( "Error adding JARs to realm.", e );
        }

        // ----------------------------------------------------------------------------
        // Discover the components that are present in the new componentRealm.
        // ----------------------------------------------------------------------------

        // are all the discoverers are present: YES
        // are all the listeners present: YES the PluginCollector appears to be there
        // we are not discovering the clean plugin

        try
        {
            discoverComponents( componentRealm );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException( "Error configuring discovered component.", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new PlexusContainerException( "Error resolving discovered component.", e );
        }

        return componentRealm;
    }

    // ----------------------------------------------------------------------------
    // The method from alpha-9 for creating child containers
    // ----------------------------------------------------------------------------

    public PlexusContainer createChildContainer( String name,
                                                 List classpathJars,
                                                 Map context )
        throws PlexusContainerException
    {
        return createChildContainer( name, classpathJars, context, Collections.EMPTY_LIST );
    }

    public PlexusContainer createChildContainer( String name,
                                                 List classpathJars,
                                                 Map context,
                                                 List discoveryListeners )
        throws PlexusContainerException
    {
        if ( hasChildContainer( name ) )
        {
            throw new DuplicateChildContainerException( getName(), name );
        }

        DefaultPlexusContainer child = new DefaultPlexusContainer( name, context, null, null );

        child.classWorld = classWorld;

        ClassRealm childRealm = null;

        String childRealmId = getName() + ".child-container[" + name + "]";

        try
        {
            childRealm = classWorld.getRealm( childRealmId );
        }
        catch ( NoSuchRealmException e )
        {
            try
            {
                childRealm = classWorld.newRealm( childRealmId );
            }
            catch ( DuplicateRealmException impossibleError )
            {
                getLogger().error( "An impossible error has occurred. After getRealm() failed, newRealm() " +
                    "produced duplication error on same id!", impossibleError );
            }
        }

        //childRealm.setParent( plexusRealm );
        childRealm.setParentRealm( containerRealm );

        //child.coreRealm = childRealm;
        child.containerRealm = childRealm;

        //child.plexusRealm = childRealm;

        child.setName( name );

        child.setParentPlexusContainer( this );

        // ----------------------------------------------------------------------
        // Set all the child elements from the parent that were set
        // programmatically.
        // ----------------------------------------------------------------------

        child.setLoggerManager( loggerManager );

        for ( Iterator it = context.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            child.addContextValue( entry.getKey(), entry.getValue() );
        }

        child.initialize();

        for ( Iterator it = classpathJars.iterator(); it.hasNext(); )
        {
            Object next = it.next();

            File jar = (File) next;

            child.addJarResource( jar );
        }

        for ( Iterator it = discoveryListeners.iterator(); it.hasNext(); )
        {
            ComponentDiscoveryListener listener = (ComponentDiscoveryListener) it.next();

            child.registerComponentDiscoveryListener( listener );
        }

        child.start();

        childContainers.put( name, child );

        return child;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setParentPlexusContainer( PlexusContainer container )
    {
        this.parentContainer = container;
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
        List result;

        Map componentDescriptorsByHint = getComponentDescriptorMap( role );

        if ( componentDescriptorsByHint != null )
        {
            result = new ArrayList( componentDescriptorsByHint.values() );
        }
        else
        {
            ComponentDescriptor unhintedDescriptor = getComponentDescriptor( role );

            if ( unhintedDescriptor != null )
            {
                result = Collections.singletonList( unhintedDescriptor );
            }
            else
            {
                result = Collections.EMPTY_LIST;
            }
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
        throws ComponentLifecycleException
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager =
            componentManagerManager.findComponentManagerByComponentInstance( component );

        if ( componentManager == null )
        {
            if ( parentContainer != null )
            {
                parentContainer.release( component );
            }
            else
            {
                getLogger().warn(
                    "Component manager not found for returned component. Ignored. component=" + component );
            }
        }
        else
        {
            componentManager.release( component );

            if ( componentManager.getConnections() <= 0 )
            {
                componentManagerManager.unassociateComponentWithComponentManager( component );
            }
        }
    }

    public void releaseAll( Map components )
        throws ComponentLifecycleException
    {
        for ( Iterator i = components.values().iterator(); i.hasNext(); )
        {
            Object component = i.next();

            release( component );
        }
    }

    public void releaseAll( List components )
        throws ComponentLifecycleException
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

    public boolean hasComponent( String role,
                                 String roleHint )
    {
        return componentRepository.hasComponent( role, roleHint );
    }

    public void suspend( Object component )
        throws ComponentLifecycleException
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager =
            componentManagerManager.findComponentManagerByComponentInstance( component );

        componentManager.suspend( component );
    }

    public void resume( Object component )
        throws ComponentLifecycleException
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager =
            componentManagerManager.findComponentManagerByComponentInstance( component );

        componentManager.resume( component );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    boolean initialized;

    protected void initialize()
        throws PlexusContainerException
    {
        if ( initialized )
        {
            throw new PlexusContainerException( "The container has already been initialized!" );
        }

        try
        {
            initializeConfiguration();

            initializePhases();
        }
        catch ( ConfigurationProcessingException e )
        {
            throw new PlexusContainerException( "Error processing configuration", e );
        }
        catch ( ConfigurationResourceNotFoundException e )
        {
            throw new PlexusContainerException( "Error processing configuration", e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException( "Error configuring components", e );
        }

        initialized = true;
    }

    public void initializePhases()
        throws PlexusContainerException
    {
        PlexusConfiguration initializationConfiguration = configuration.getChild( "container-initialization" );

        ContainerInitializationContext initializationContext =
            new ContainerInitializationContext( this, classWorld, containerRealm, configuration );

        ComponentConfigurator c = new BasicComponentConfigurator();

        try
        {
            c.configureComponent( this, initializationConfiguration, containerRealm );
        }
        catch ( ComponentConfigurationException e )
        {
            throw new PlexusContainerException( "Error setting container initialization initializationPhases.", e );
        }

        for ( Iterator iterator = initializationPhases.iterator(); iterator.hasNext(); )
        {
            ContainerInitializationPhase phase = (ContainerInitializationPhase) iterator.next();

            try
            {
                phase.execute( initializationContext );
            }
            catch ( ContainerInitializationException e )
            {
                throw new PlexusContainerException( "Error initializaing container in " + phase + ".", e );
            }
        }
    }

    // We need to be aware of dependencies between discovered components when the listed component
    // as the discovery listener itself depends on components that need to be discovered.
    public List discoverComponents( ClassRealm classRealm )
        throws PlexusConfigurationException, ComponentRepositoryException
    {
        return ComponentDiscoveryPhase.discoverComponents( this, classRealm );
    }

    protected void start()
        throws PlexusContainerException
    {
        configuration = null;
    }

    public void dispose()
    {
        disposeAllComponents();

        boolean needToDisposeRealm = true;

        if ( parentContainer != null && containerRealm.getId().equals( parentContainer.getContainerRealm().getId() ) )
        {
            needToDisposeRealm = false;
        }

        if ( parentContainer != null )
        {
            parentContainer.removeChildContainer( getName() );

            parentContainer = null;
        }

        try
        {
            containerRealm.setParentRealm( null );

            if ( needToDisposeRealm )
            {
                classWorld.disposeRealm( containerRealm.getId() );
            }
        }
        catch ( NoSuchRealmException e )
        {
            getLogger().debug( "Failed to dispose realm for exiting container: " + getName(), e );
        }
    }

    protected void disposeAllComponents()
    {
        // copy the list so we don't get concurrent modification exceptions during disposal
        Collection collection = new ArrayList( componentManagerManager.getComponentManagers().values() );
        for ( Iterator iter = collection.iterator(); iter.hasNext(); )
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

    public void addContextValue( Object key,
                                 Object value )
    {
        containerContext.put( key, value );
    }

    // ----------------------------------------------------------------------
    // Misc Configuration
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public void setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;
    }

    public ClassRealm getContainerRealm()
    {
        return containerRealm;
    }

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    public Context getContext()
    {
        return containerContext;
    }

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    //TODO: put this in a separate helper class and turn into a component if possible, too big.

    protected void initializeConfiguration()
        throws ConfigurationProcessingException, ConfigurationResourceNotFoundException, PlexusConfigurationException
    {
        // System userConfiguration

        InputStream is = containerRealm.getResourceAsStream( PlexusConstants.BOOTSTRAP_CONFIGURATION );

        if ( is == null )
        {
            throw new IllegalStateException( "The internal default plexus-bootstrap.xml is missing. " +
                "This is highly irregular, your plexus JAR is most likely corrupt." );
        }

        PlexusConfiguration bootstrapConfiguration =
            PlexusTools.buildConfiguration( PlexusConstants.BOOTSTRAP_CONFIGURATION, new InputStreamReader( is ) );

        // Some of this could probably be collapsed as having a plexus.xml in your
        // META-INF/plexus directory is probably a better solution then specifying
        // a configuration with an URL but I'm leaving the configuration by URL
        // as folks might be using it ... I made this change to accomodate Maven
        // but I think it's better to discover a configuration in a standard
        // place.

        configuration = bootstrapConfiguration;

        PlexusXmlComponentDiscoverer discoverer = new PlexusXmlComponentDiscoverer();

        PlexusConfiguration plexusConfiguration = discoverer.discoverConfiguration( getContext(), containerRealm );

        if ( plexusConfiguration != null )
        {
            configuration = PlexusConfigurationMerger.merge( plexusConfiguration, configuration );

            processConfigurationsDirectory();
        }

        if ( configurationReader != null )
        {
            // User userConfiguration

            PlexusConfiguration userConfiguration = PlexusTools.buildConfiguration(
                "<User Specified Configuration Reader>", getInterpolationConfigurationReader( configurationReader ) );

            // Merger of bootstrapConfiguration and user userConfiguration

            configuration = PlexusConfigurationMerger.merge( userConfiguration, configuration );

            processConfigurationsDirectory();
        }

        // ---------------------------------------------------------------------------
        // Now that we have the configuration we will use the ConfigurationProcessor
        // to inline any external configuration instructions.
        //
        // At his point the variables in the configuration have already been
        // interpolated so we can send in an empty Map because the containerContext
        // values are already there.
        // ---------------------------------------------------------------------------

        ConfigurationProcessor p = new ConfigurationProcessor();

        p.addConfigurationResourceHandler( new FileConfigurationResourceHandler() );

        p.addConfigurationResourceHandler( new DirectoryConfigurationResourceHandler() );

        configuration = p.process( configuration, Collections.EMPTY_MAP );
    }

    protected Reader getInterpolationConfigurationReader( Reader reader )
    {
        return new InterpolationFilterReader( reader, new ContextMapAdapter( containerContext ) );
    }

    /**
     * Process any additional component configuration files that have been
     * specified. The specified directory is scanned recursively so configurations
     * can be within nested directories to help with component organization.
     */
    private void processConfigurationsDirectory()
        throws PlexusConfigurationException
    {
        String s = configuration.getChild( "configurations-directory" ).getValue( null );

        if ( s != null )
        {
            PlexusConfiguration componentsConfiguration = configuration.getChild( "components" );

            File configurationsDirectory = new File( s );

            if ( configurationsDirectory.exists() && configurationsDirectory.isDirectory() )
            {
                List componentConfigurationFiles;
                try
                {
                    componentConfigurationFiles =
                        FileUtils.getFiles( configurationsDirectory, "**/*.conf", "**/*.xml" );
                }
                catch ( IOException e )
                {
                    throw new PlexusConfigurationException( "Unable to locate configuration files", e );
                }

                for ( Iterator i = componentConfigurationFiles.iterator(); i.hasNext(); )
                {
                    File componentConfigurationFile = (File) i.next();

                    FileReader reader = null;
                    try
                    {
                        reader = new FileReader( componentConfigurationFile );
                        PlexusConfiguration componentConfiguration = PlexusTools.buildConfiguration(
                            componentConfigurationFile.getAbsolutePath(),
                            getInterpolationConfigurationReader( reader ) );

                        componentsConfiguration.addChild( componentConfiguration.getChild( "components" ) );
                    }
                    catch ( FileNotFoundException e )
                    {
                        throw new PlexusConfigurationException(
                            "File " + componentConfigurationFile + " disappeared before processing", e );
                    }
                    finally
                    {
                        IOUtil.close( reader );
                    }
                }
            }
        }
    }

    public void addJarResource( File jar )
        throws PlexusContainerException
    {
        try
        {
            containerRealm.addURL( jar.toURI().toURL() );

            //TODO: might not necessarily want to discover components here.
            discoverComponents( containerRealm );
        }
        catch ( MalformedURLException e )
        {
            throw new PlexusContainerException( "Cannot add jar resource: " + jar + " (bad URL)", e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException(
                "Cannot add jar resource: " + jar + " (error discovering new components)", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new PlexusContainerException(
                "Cannot add jar resource: " + jar + " (error discovering new components)", e );
        }
    }

    public void addJarRepository( File repository )
    {
        if ( repository.exists() && repository.isDirectory() )
        {
            File[] jars = repository.listFiles();

            for ( int j = 0; j < jars.length; j++ )
            {
                if ( jars[j].getAbsolutePath().endsWith( ".jar" ) )
                {
                    try
                    {
                        addJarResource( jars[j] );
                    }
                    catch ( PlexusContainerException e )
                    {
                        getLogger().warn( "Unable to add JAR: " + jars[j], e );
                    }
                }
            }
        }
        else
        {
            String message = "The specified JAR repository doesn't exist or is not a directory: '" +
                repository.getAbsolutePath() + "'.";

            if ( getLogger() != null )
            {
                getLogger().warn( message );
            }
            else
            {
                System.out.println( message );
            }
        }
    }

    public Logger getLogger()
    {
        return super.getLogger();
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

    // ----------------------------------------------------------------------
    // Autowire Support
    // ----------------------------------------------------------------------

    // note:jvz Currently this only works for setters as I'm experimenting for
    // webwork. I would like the API for autowiring to be simple so we could easily look
    // for constructors with parameters and use that method of composition before attempting
    // the use of setters or private fields.

    public Object autowire( Object component )
        throws CompositionException
    {
        SetterComponentComposer composer = new SetterComponentComposer();

        composer.assembleComponent( component, null, this );

        return component;
    }

    public Object createAndAutowire( String clazz )
        throws CompositionException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Object component = containerRealm.loadClass( clazz ).newInstance();

        SetterComponentComposer composer = new SetterComponentComposer();

        composer.assembleComponent( component, null, this );

        return component;
    }

    // ----------------------------------------------------------------------
    // Reloading
    // ----------------------------------------------------------------------

    public void setReloadingEnabled( boolean reloadingEnabled )
    {
        this.reloadingEnabled = reloadingEnabled;
    }

    public boolean isReloadingEnabled()
    {
        return reloadingEnabled;
    }

    // ----------------------------------------------------------------------------
    // Mutable Container Interface
    // ----------------------------------------------------------------------------

    public ComponentRepository getComponentRepository()
    {
        return componentRepository;
    }

    public void setComponentRepository( ComponentRepository componentRepository )
    {
        this.componentRepository = componentRepository;
    }

    public ComponentManagerManager getComponentManagerManager()
    {
        return componentManagerManager;
    }

    public void setComponentManagerManager( ComponentManagerManager componentManagerManager )
    {
        this.componentManagerManager = componentManagerManager;
    }

    public LifecycleHandlerManager getLifecycleHandlerManager()
    {
        return lifecycleHandlerManager;
    }

    public void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.lifecycleHandlerManager = lifecycleHandlerManager;
    }

    public ComponentDiscovererManager getComponentDiscovererManager()
    {
        return componentDiscovererManager;
    }

    public void setComponentDiscovererManager( ComponentDiscovererManager componentDiscovererManager )
    {
        this.componentDiscovererManager = componentDiscovererManager;
    }

    public ComponentFactoryManager getComponentFactoryManager()
    {
        return componentFactoryManager;
    }

    public void setComponentFactoryManager( ComponentFactoryManager componentFactoryManager )
    {
        this.componentFactoryManager = componentFactoryManager;
    }

    public ComponentLookupManager getComponentLookupManager()
    {
        return componentLookupManager;
    }

    public void setComponentLookupManager( ComponentLookupManager componentLookupManager )
    {
        this.componentLookupManager = componentLookupManager;
    }

    public ComponentComposerManager getComponentComposerManager()
    {
        return componentComposerManager;
    }

    public void setComponentComposerManager( ComponentComposerManager componentComposerManager )
    {
        this.componentComposerManager = componentComposerManager;
    }

    public LoggerManager getLoggerManager()
    {
        return loggerManager;
    }

    public void setLoggerManager( LoggerManager loggerManager )
    {
        this.loggerManager = loggerManager;
    }

    // Configuration

    public PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    // Parent Container

    public PlexusContainer getParentContainer()
    {
        return parentContainer;
    }

    // ----------------------------------------------------------------------------
    // Component Realms
    // ----------------------------------------------------------------------------

    public ClassRealm getComponentRealm( String realmId )
    {
        ClassRealm realm = null;

        try
        {
            realm = classWorld.getRealm( realmId );
        }
        catch ( NoSuchRealmException e )
        {
            // This should never happen: when a component is discovered, it is discovered from a realm and
            // it is at that point the realm id is assigned to the component descriptor.
        }

        return realm;
    }
}
