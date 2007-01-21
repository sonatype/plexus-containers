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
import org.codehaus.plexus.context.ContextException;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * <pre>
 *  //TODO move lookup code to a separate component
 *  //TODO  register live components so they can be wired
 *  //keep track of the interfaces for components
 *  //todo allow setting of a live configuraton so applications that embed plexus can use
 *  whatever configuration mechanism they like. They just have to adapt it into something plexus can understand.
 *  //todo
 *  make a typesafe configuration model for the container
 *  //todo pico like registration
 *  //todo need loggers per execution in the maven embedder
 *  //todo a simple front-end to make containers of different flavours, a flavour encapsulating
 *  // a set of behaviours
 *  //todo the core components should probably have a small lifecycle to pass through
 * </pre>
 *
 * @author Jason van Zyl
 * @author Kenney Westerhof
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

    // todo: don't use a reader
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

    /** Map&lt;String, PlexusContainer> where the key is the container name. */
    protected Map childContainers = new WeakHashMap();

    protected Date creationDate = new Date();

    protected boolean reloadingEnabled;

    private static ThreadLocal lookupRealm = new ThreadLocal();

    public static ClassRealm setLookupRealm( ClassRealm realm )
    {
        if ( realm == null )
        {
            return null;
        }
        
        ClassRealm oldRealm = (ClassRealm) lookupRealm.get();
        lookupRealm.set( realm );
        return oldRealm;
    }

    public static ClassRealm getLookupRealm()
    {
        return (ClassRealm) lookupRealm.get();
    }

    // ----------------------------------------------------------------------
    // Constructors
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
        construct( DEFAULT_CONTAINER_NAME, null, null, null, null );
    }

    public DefaultPlexusContainer( String name, Map context )
        throws PlexusContainerException
    {
        construct( name, context, null, null, null );
    }

    public DefaultPlexusContainer( String name, Map context, ClassWorld classWorld )
        throws PlexusContainerException
    {
        this( name, context, classWorld, null );
    }

    public DefaultPlexusContainer( String name, Map context, ClassWorld classWorld, InputStream config )
        throws PlexusContainerException
    {
        construct( name, context, config, classWorld, null );
    }

    // ///// Utility constructors for various configuration sources

    public DefaultPlexusContainer( String name, Map context, File file )
        throws PlexusContainerException
    {
        this( name, context, null, toStream( file ) );
    }

    public DefaultPlexusContainer( String name, Map context, File file, ClassWorld classWorld )
        throws PlexusContainerException
    {
        this( name, context, classWorld, toStream( file ) );
    }

    public DefaultPlexusContainer( String name, Map context, String configurationResource )
        throws PlexusContainerException
    {
        this( name, context, null, toStream( configurationResource ) );
    }

    public DefaultPlexusContainer( String name, Map context, String configurationResource, ClassWorld classWorld )
        throws PlexusContainerException
    {
        this( name, context, classWorld, toStream( configurationResource ) );
    }

    public DefaultPlexusContainer( String name, Map context, URL url )
        throws PlexusContainerException
    {
        this( name, context, null, toStream( url ) );
    }

    public DefaultPlexusContainer( String name, Map context, URL url, ClassWorld classWorld )
        throws PlexusContainerException
    {
        this( name, context, classWorld, toStream( url ) );
    }

    // ----------------------------------------------------------------------------
    // Inheritance of Containers
    // ----------------------------------------------------------------------------

    protected DefaultPlexusContainer( String name, Map context, PlexusContainer parent, List discoveryListeners )
        throws PlexusContainerException
    {
        this.parentContainer = parent;

        this.loggerManager = parentContainer.getLoggerManager();

        this.containerRealm = getChildRealm( getName(), name, parent.getContainerRealm() );

        for ( Iterator it = discoveryListeners.iterator(); it.hasNext(); )
        {
            registerComponentDiscoveryListener( (ComponentDiscoveryListener) it.next() );
        }

        construct( name, context, null, containerRealm.getWorld(), containerRealm );
    }

    // ----------------------------------------------------------------------------
    // Lookup
    // ----------------------------------------------------------------------------

    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        return lookup( componentKey, DefaultPlexusContainer.getLookupRealm() );
        // componentLookupManager.lookup( componentKey, (ClassRealm) null );
    }

    /**
     * @deprecated
     */
    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        return lookupMap( role, DefaultPlexusContainer.getLookupRealm() );
    }

    public Map lookupMap( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, realm );
    }

    public Object lookup( String componentKey, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( componentKey, realm );
    }

    public List lookupList( String role )
        throws ComponentLookupException
    {
        return lookupList( role, DefaultPlexusContainer.getLookupRealm() );
    }

    public List lookupList( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, realm );
    }

    /**
     * @deprecated
     */
    public Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, DefaultPlexusContainer.getLookupRealm() );
    }

    public Object lookup( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, realm );
    }

    /**
     * @deprecated
     */
    public Object lookup( Class componentClass )
        throws ComponentLookupException
    {
        return lookup( componentClass, DefaultPlexusContainer.getLookupRealm() );
    }

    public Object lookup( Class componentClass, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( componentClass, realm );
    }

    /**
     * @deprecated
     */
    public Map lookupMap( Class role )
        throws ComponentLookupException
    {
        return lookupMap( role, DefaultPlexusContainer.getLookupRealm() );

    }

    public Map lookupMap( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, realm );
    }

    /**
     * @deprecated
     */
    public List lookupList( Class role )
        throws ComponentLookupException
    {
        return lookupList( role, DefaultPlexusContainer.getLookupRealm() );
    }

    public List lookupList( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, realm );
    }

    /**
     * @deprecated
     */
    public Object lookup( Class role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role, roleHint, DefaultPlexusContainer.getLookupRealm() );
    }

    public Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, realm );
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
    // only works in Maven where artifacts are downloaded
    // ----------------------------------------------------------------------------
    public ClassRealm createComponentRealm( String id, List jars )
        throws PlexusContainerException
    {
        ClassRealm componentRealm;

        try
        {
            // XXX this could NOT be a child realm for this container!

            ClassRealm realm = classWorld.getRealm( id );
            getLogger()
                .warn( "Reusing existing component realm: " + id + " - no components detected!", new Throwable() );
            return realm;
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
                componentRealm.addURL( ( (File) it.next() ).toURI().toURL() );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new PlexusContainerException( "Error adding JARs to realm.", e );
        }

        getLogger().debug( "Created component realm: " + id );

        // ----------------------------------------------------------------------------
        // Discover the components that are present in the new componentRealm.
        // ----------------------------------------------------------------------------

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

    /** @deprecated */
    public PlexusContainer createChildContainer( String name, List classpathJars, Map context )
        throws PlexusContainerException
    {
        return createChildContainer( name, classpathJars, context, Collections.EMPTY_LIST );
    }

    public PlexusContainer createChildContainer( String name, List classpathJars, Map context, List discoveryListeners )
        throws PlexusContainerException
    {
        if ( hasChildContainer( name ) )
        {
            throw new DuplicateChildContainerException( getName(), name );
        }

        DefaultPlexusContainer child = new DefaultPlexusContainer( name, context, this, discoveryListeners );

        childContainers.put( name, child );

        return child;
    }

    private static ClassRealm getChildRealm( String parentName, String name, ClassRealm containerRealm )
        throws PlexusContainerException
    {
        String childRealmId = parentName + ".child-container[" + name + "]";

        try
        {
            // FIXME: an existing realm probably is in use and already
            // has a parent realm; is it safe to change that?
            ClassRealm childRealm = containerRealm.getWorld().getRealm( childRealmId );
            childRealm.setParentRealm( containerRealm );

            return childRealm;
        }
        catch ( NoSuchRealmException e )
        {
            try
            {
                return containerRealm.createChildRealm( childRealmId );
            }
            catch ( DuplicateRealmException impossibleError )
            {
                throw new PlexusContainerException( "Error creating new realm: After getRealm() failed, newRealm() "
                    + "produced duplication error on same id!", impossibleError );
            }
        }
    }

    // XXX remove
    public void setName( String name )
    {
        this.name = name;
    }

    // XXX remove!
    public void setParentPlexusContainer( PlexusContainer container )
    {
        this.parentContainer = container;
    }

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    /**
     * @deprecated use {@link DefaultPlexusContainer#getComponentDescriptor(String, ClassRealm)}
     */
    public ComponentDescriptor getComponentDescriptor( String componentKey )
    {
        return getComponentDescriptor( componentKey, DefaultPlexusContainer.getLookupRealm() );
    }

    public ComponentDescriptor getComponentDescriptor( String componentKey, ClassRealm classRealm )
    {
        ComponentDescriptor result = componentRepository.getComponentDescriptor( componentKey, classRealm );

        ClassRealm tmpRealm = classRealm.getParentRealm();

        while ( result == null && tmpRealm != null )
        {
            result = componentRepository.getComponentDescriptor( componentKey, classRealm );
            tmpRealm = tmpRealm.getParentRealm();
        }

        if ( result == null && parentContainer != null )
        {
            result = parentContainer.getComponentDescriptor( componentKey, classRealm );
        }

        return result;
    }

    /**
     * @deprecated
     */
    public Map getComponentDescriptorMap( String role )
    {
        return getComponentDescriptorMap( role, DefaultPlexusContainer.getLookupRealm() );
    }

    public Map getComponentDescriptorMap( String role, ClassRealm realm )
    {
        Map result = new WeakHashMap();

        if ( parentContainer != null )
        {
            Map m = parentContainer.getComponentDescriptorMap( role, realm );
            if ( m != null )
            {
                result.putAll( m );
            }
        }

        Map componentDescriptors = componentRepository.getComponentDescriptorMap( role, realm );

        if ( componentDescriptors != null )
        {
            result.putAll( componentDescriptors );
        }

        return result;
    }

    /**
     * @deprecated
     */
    public List getComponentDescriptorList( String role )
    {
        return getComponentDescriptorList( role, DefaultPlexusContainer.getLookupRealm() );
    }

    public List getComponentDescriptorList( String role, ClassRealm realm )
    {
        List result;

        Map componentDescriptorsByHint = getComponentDescriptorMap( role, realm );

        if ( componentDescriptorsByHint != null )
        {
            result = new ArrayList( componentDescriptorsByHint.values() );
        }
        else
        {
            // XXX why is it unhinted? the getComponentDescriptor's param is named 'componentKey'
            ComponentDescriptor unhintedDescriptor = getComponentDescriptor( role, realm );

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

        ComponentManager componentManager = componentManagerManager.findComponentManagerByComponentInstance( component );

        if ( componentManager == null )
        {
            if ( parentContainer != null )
            {
                parentContainer.release( component );
            }
            else
            {
                getLogger()
                    .warn( "Component manager not found for returned component. Ignored. component=" + component );
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

    /**
     * @deprecated
     */
    public boolean hasComponent( String componentKey )
    {
        return hasComponent( componentKey, DefaultPlexusContainer.getLookupRealm() );
    }

    public boolean hasComponent( String componentKey, ClassRealm realm )
    {
        return componentRepository.hasComponent( componentKey, realm );
    }

    /**
     * @deprecated
     */
    public boolean hasComponent( String role, String roleHint )
    {
        return hasComponent( role, roleHint, DefaultPlexusContainer.getLookupRealm() );
    }

    public boolean hasComponent( String role, String roleHint, ClassRealm realm )
    {
        return componentRepository.hasComponent( role, roleHint, realm );
    }

    public void suspend( Object component )
        throws ComponentLifecycleException
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = componentManagerManager.findComponentManagerByComponentInstance( component );

        componentManager.suspend( component );
    }

    public void resume( Object component )
        throws ComponentLifecycleException
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

    boolean initialized;

    private void construct( String name, Map context, InputStream in, ClassWorld classWorld, ClassRealm realm )
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

        containerRealm = realm;

        if ( containerRealm == null )
        {
            try
            {
                containerRealm = this.classWorld.getRealm( DEFAULT_REALM_NAME );
            }
            catch ( NoSuchRealmException e )
            {
                List realms = new LinkedList( this.classWorld.getRealms() );
                containerRealm = (ClassRealm) realms.get( 0 );
                if ( containerRealm == null )
                {
                    System.err.println( "No container realm! Expect errors." );
                    new Throwable().printStackTrace();
                }
            }
        }

        setLookupRealm( containerRealm );

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

        // TODO: just store reference to the configuration in a String and use that in the configuration initialization
        this.configurationReader = in == null ? null : new InputStreamReader( in );

        try
        {
            // XXX this will set up the configuration and process it
            initialize();
            // XXX this will wipe out the configuration field - is this needed? If so,
            // why? can we remove the need to have a configuration field then?
            start();
        }
        finally
        {
            IOUtil.close( this.configurationReader );
        }
    }

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
        catch ( ContextException e )
        {
            throw new PlexusContainerException( "Error processing configuration", e );
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

    protected void initializePhases()
        throws PlexusContainerException
    {
        PlexusConfiguration initializationConfiguration = configuration.getChild( "container-initialization" );

        ContainerInitializationContext initializationContext = new ContainerInitializationContext(
            this,
            classWorld,
            containerRealm,
            configuration );

        // PLXAPI: I think we might only ever need one of these so maybe we can create it with a constructor
        // and store it.
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
        return discoverComponents( classRealm, false );
    }

    /**
     * @see org.codehaus.plexus.MutablePlexusContainer#discoverComponents(org.codehaus.plexus.classworlds.realm.ClassRealm,
     *      boolean)
     */
    public List discoverComponents( ClassRealm classRealm, boolean override )
        throws PlexusConfigurationException, ComponentRepositoryException
    {
        return ComponentDiscoveryPhase.discoverComponents( this, classRealm, override );
    }

    protected void start()
        throws PlexusContainerException
    {
        // XXX this is called after initializeConfiguration - is this correct?
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

    public void addContextValue( Object key, Object value )
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

    public void setContainerRealm( ClassRealm containerRealm )
    {
        this.containerRealm = containerRealm;
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

    // TODO: put this in a separate helper class and turn into a component if possible, too big.

    protected void initializeConfiguration()
        throws ConfigurationProcessingException, ConfigurationResourceNotFoundException, PlexusConfigurationException,
        ContextException
    {
        // System userConfiguration

        InputStream is = containerRealm.getResourceAsStream( PlexusConstants.BOOTSTRAP_CONFIGURATION );

        if ( is == null )
        {
            ClassRealm cr = containerRealm;
            String realmStack = "";
            while ( cr != null )
            {
                realmStack += "\n  " + cr.getId() + " parent=" + cr.getParent() + " ("
                    + cr.getResource( PlexusConstants.BOOTSTRAP_CONFIGURATION ) + ")";
                cr = cr.getParentRealm();
            }

            throw new IllegalStateException( "The internal default plexus-bootstrap.xml is missing. "
                + "This is highly irregular, your plexus JAR is most likely corrupt. Realms:" + realmStack );
        }

        PlexusConfiguration bootstrapConfiguration = PlexusTools
            .buildConfiguration( PlexusConstants.BOOTSTRAP_CONFIGURATION, new InputStreamReader( is ) );

        // Some of this could probably be collapsed as having a plexus.xml in your
        // META-INF/plexus directory is probably a better solution then specifying
        // a configuration with an URL but I'm leaving the configuration by URL
        // as folks might be using it ... I made this change to accomodate Maven
        // but I think it's better to discover a configuration in a standard
        // place.

        configuration = bootstrapConfiguration;

        if ( !containerContext.contains( PlexusConstants.IGNORE_CONTAINER_CONFIGURATION )
            || containerContext.get( PlexusConstants.IGNORE_CONTAINER_CONFIGURATION ) != Boolean.TRUE )
        {
            PlexusXmlComponentDiscoverer discoverer = new PlexusXmlComponentDiscoverer();

            PlexusConfiguration plexusConfiguration = discoverer.discoverConfiguration( getContext(), containerRealm );

            if ( plexusConfiguration != null )
            {
                configuration = PlexusConfigurationMerger.merge( plexusConfiguration, configuration );

                processConfigurationsDirectory();
            }
        }

        if ( configurationReader != null )
        {
            // User userConfiguration

            PlexusConfiguration userConfiguration = PlexusTools
                .buildConfiguration( "<User Specified Configuration Reader>",
                                     getInterpolationConfigurationReader( configurationReader ) );

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
     * Process any additional component configuration files that have been specified. The specified directory is scanned
     * recursively so configurations can be within nested directories to help with component organization.
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
                    componentConfigurationFiles = FileUtils.getFiles( configurationsDirectory, "**/*.conf", "**/*.xml" );
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
                        PlexusConfiguration componentConfiguration = PlexusTools
                            .buildConfiguration( componentConfigurationFile.getAbsolutePath(),
                                                 getInterpolationConfigurationReader( reader ) );

                        componentsConfiguration.addChild( componentConfiguration.getChild( "components" ) );
                    }
                    catch ( FileNotFoundException e )
                    {
                        throw new PlexusConfigurationException( "File " + componentConfigurationFile
                            + " disappeared before processing", e );
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

            if ( this.initialized )
            {
                discoverComponents( containerRealm );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new PlexusContainerException( "Cannot add jar resource: " + jar + " (bad URL)", e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException( "Cannot add jar resource: " + jar
                + " (error discovering new components)", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new PlexusContainerException( "Cannot add jar resource: " + jar
                + " (error discovering new components)", e );
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
            String message = "The specified JAR repository doesn't exist or is not a directory: '"
                + repository.getAbsolutePath() + "'.";

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

        if ( realm == null )
        {
            // The core components need the container realm.
            realm = containerRealm;
        }

        return realm;
    }

    private static InputStream toStream( File file )
        throws PlexusContainerException
    {
        try
        {
            return file == null ? null : new FileInputStream( file );
        }
        catch ( FileNotFoundException e )
        {
            throw new PlexusContainerException( "Could not read file: " + file.getAbsolutePath() );
        }
    }

    private static InputStream toStream( URL url )
        throws PlexusContainerException
    {
        try
        {
            return url == null ? null : url.openStream();
        }
        catch ( IOException e )
        {
            throw new PlexusContainerException( "Could not read url: " + url );
        }
    }

    private static InputStream toStream( String resource )
        throws PlexusContainerException
    {
        if ( resource == null )
        {
            return null;
        }
        InputStream in = DefaultPlexusContainer.class.getResourceAsStream( resource );
        if ( in == null )
        {
            throw new PlexusContainerException( "Could not load resource '" + resource + "'." );
        }
        return in;
    }

    /**
     * Utility method to get a default lookup realm for a component.
     */
    public static ClassRealm getLookupRealm( Object component )
    {
        if ( component.getClass().getClassLoader() instanceof ClassRealm )
        {
            return ( (ClassRealm) component.getClass().getClassLoader() );
        }
        else
        {
            return DefaultPlexusContainer.getLookupRealm();
        }

    }
}
