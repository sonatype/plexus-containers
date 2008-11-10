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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
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
import org.codehaus.plexus.configuration.source.ConfigurationSource;
import org.codehaus.plexus.container.initialization.ComponentDiscoveryPhase;
import org.codehaus.plexus.container.initialization.ContainerInitializationContext;
import org.codehaus.plexus.container.initialization.ContainerInitializationPhase;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.ReaderFactory;

/**
 * Default implementation of PlexusContainer and MutablePlexusContainer.
 * @author Jason van Zyl
 * @author Kenney Westerhof
 */
public class DefaultPlexusContainer
    extends AbstractLogEnabled
    implements MutablePlexusContainer
{
    protected static final String DEFAULT_CONTAINER_NAME = "default";

    protected static final String DEFAULT_REALM_NAME = "plexus.core";

    /**
     * Container's name
     */
    protected String name;

    /**
     * Arbitrary data associated with the container.  Data in the container has highest precedence when configuring
     * a component to create.
     */
    protected Context containerContext;

    protected PlexusConfiguration configuration;

    // todo: don't use a reader
    protected Reader configurationReader;

    protected ClassWorld classWorld;

    protected ClassRealm containerRealm;

    // ----------------------------------------------------------------------------
    // Core components
    // ----------------------------------------------------------------------------

    /**
     * A repository of component descriptions which are used to create new components and for tooling.
     */
    protected ComponentRepository componentRepository;

    /**
     * The main component registry.  Components are wrapped in a ComponentManager and the ComponentManagerManager
     * is the index of all existing ComponentManagers in this container.
     */
    protected ComponentManagerManager componentManagerManager;

    /**
     * Simple index (registry) of LifecycleHandler instances.
     */
    protected LifecycleHandlerManager lifecycleHandlerManager;

    /**
     * Simple index (registry) of ComponentDiscovers and ComponentDiscoveryListener.
     */
    protected ComponentDiscovererManager componentDiscovererManager;

    /**
     * Trivial class to look-up ComponentFactory instances in this container.
     */
    protected ComponentFactoryManager componentFactoryManager;

    /**
     * Encapsulates the algorithm for finding components by role, roleHint, and classRealm.
     */
    protected ComponentLookupManager componentLookupManager;

    /**
     * Generic logger interface.
     */
    protected LoggerManager loggerManager;

    /**
     * Converts a ComponentDescriptor into PlexusConfiguration.   
     */
    protected ConfigurationSource configurationSource;

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    protected final Date creationDate = new Date();

    // TODO: Is there a more threadpool-friendly way to do this?
    private ThreadLocal<ClassRealm> lookupRealm = new ThreadLocal<ClassRealm>();
    
    private boolean devMode;

    public void addComponent( Object component, String role )
        throws ComponentRepositoryException
    {
        ComponentDescriptor cd = new ComponentDescriptor();

        cd.setRole( role );

        cd.setRoleHint( PlexusConstants.PLEXUS_DEFAULT_HINT );

        cd.setImplementation( role );

        addComponentDescriptor( cd );
    }

    /**
     * Used for getLookupRealm for threads when the threadlocal doesn't contain a value.
     */
    private ClassRealm staticLookupRealm;

    public ClassRealm setLookupRealm( ClassRealm realm )
    {
        // todo [dain] This code is non-symetrical, undocumented behavior and will cause memory leaks in thread pools
        // since it is not possible to clean the thread local
        if ( realm == null )
        {
            return null;
        }

        ClassRealm oldRealm = lookupRealm.get();

        // todo [dain] Again non-symentrical, undocumented and could cause memory leaks
        if ( oldRealm == null )
        {
            oldRealm = staticLookupRealm;
        }

        staticLookupRealm = realm;

        lookupRealm.set( realm );

        return oldRealm;
    }

    public ClassRealm getLookupRealm()
    {
        ClassRealm cr = lookupRealm.get();

        return cr == null ? staticLookupRealm : cr;
    }

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public DefaultPlexusContainer()
        throws PlexusContainerException
    {
        construct( new DefaultContainerConfiguration() );
    }

    public DefaultPlexusContainer( ContainerConfiguration c )
        throws PlexusContainerException
    {
        construct( c );
    }

    // ----------------------------------------------------------------------------
    // Requirements
    // ----------------------------------------------------------------------------
    // o discover all components that are present in the artifact
    // o create a separate realm for the components
    // o retrieve all the dependencies for the artifact: this last part unfortunately
    // only works in Maven where artifacts are downloaded
    // ----------------------------------------------------------------------------

    // TODO This needs to be connected to the parent realm most likely

    public ClassRealm createChildRealm( String id )
    {
        try
        {
            return containerRealm.createChildRealm( id );
        }
        catch ( DuplicateRealmException e )
        {
            try
            {
                return classWorld.getRealm( id );
            }
            catch ( NoSuchRealmException e1 )
            {
                return null;
            }
        }
    }    

    boolean initialized;

    private void construct( ContainerConfiguration c )
        throws PlexusContainerException
    {        
        devMode = c.isDevMode();

        name = c.getName();

        configurationSource = c.getConfigurationSource();

        // ----------------------------------------------------------------------------
        // ClassWorld
        // ----------------------------------------------------------------------------

        classWorld = c.getClassWorld();

        // Make sure we have a valid ClassWorld
        if ( classWorld == null )
        {
            classWorld = new ClassWorld( DEFAULT_REALM_NAME, Thread.currentThread().getContextClassLoader() );
        }

        containerRealm = c.getRealm();

        if ( containerRealm == null )
        {
            try
            {
                containerRealm = classWorld.getRealm( DEFAULT_REALM_NAME );
            }
            catch ( NoSuchRealmException e )
            {
                List realms = new LinkedList( classWorld.getRealms() );

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

        containerContext = new DefaultContext();

        if ( c.getContext() != null )
        {
            for ( Iterator it = c.getContext().entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                addContextValue( entry.getKey(), entry.getValue() );
            }
        }

        // ----------------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------------

        // TODO: just store reference to the configuration in a String and use that in the configuration initialization

        InputStream in = null;

        if ( c.getContainerConfiguration() != null )
        {
            in = toStream( c.getContainerConfiguration() );
        }

        try
        {
            if ( c.getContainerConfigurationURL() != null )
            {
                in = c.getContainerConfigurationURL().openStream();
            }
        }
        catch ( IOException e )
        {
            throw new PlexusContainerException( "Error reading configuration URL", e );
        }

        try
        {
            configurationReader = in == null ? null : ReaderFactory.newXmlReader( in );
        }
        catch ( IOException e )
        {
            throw new PlexusContainerException( "Error reading configuration file", e );
        }

        try
        {
            // XXX this will set up the configuration and process it
            initialize( c );

            // XXX this will wipe out the configuration field - is this needed? If so,
            // why? can we remove the need to have a configuration field then?
            start();
        }
        finally
        {
            IOUtil.close( configurationReader );
        }
    }

    // ----------------------------------------------------------------------------
    // Lookup
    // ----------------------------------------------------------------------------

    public Object lookup( String role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role );
    }

    public Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint );
    }

    public Object lookup( Class role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role );
    }

    public Object lookup( Class role, String roleHint )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint );
    }

    public Object lookup( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, realm );
    }

    public Object lookup( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, realm );
    }

    public Object lookup( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, realm );
    }

    public Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, realm );
    }

    public Map<String, Object> lookupMap( String role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role );
    }

    public Map<String, Object> lookupMap( String role, List<String> hints )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, hints );
    }

    public Map<String, Object> lookupMap( Class role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role );
    }

    public Map<String, Object> lookupMap( Class role, List<String> hints )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, hints );
    }

    public List<Object> lookupList( String role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role );
    }

    public List<Object> lookupList( String role, List<String> hints )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, hints );
    }

    public List<Object> lookupList( Class role )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role );
    }

    public List<Object> lookupList( Class role, List<String> hints )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, hints );
    }

    // ----------------------------------------------------------------------
    // Timestamping Methods
    // ----------------------------------------------------------------------

    public Date getCreationDate()
    {
        return creationDate;
    }

    // XXX remove
    public void setName( String name )
    {
        this.name = name;
    }

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    public ComponentDescriptor getComponentDescriptor( String role )
    {
        return getComponentDescriptor( role, getLookupRealm() );
    }

    public ComponentDescriptor getComponentDescriptor( String role, ClassRealm realm )
    {
        return getComponentDescriptor( role, PlexusConstants.PLEXUS_DEFAULT_HINT, realm );
    }

    public ComponentDescriptor getComponentDescriptor( String role, String hint )
    {
        return getComponentDescriptor( role, hint, getLookupRealm() );
    }

    public ComponentDescriptor getComponentDescriptor( String role, String hint, ClassRealm classRealm )
    {
        ComponentDescriptor result = componentRepository.getComponentDescriptor( role, hint, classRealm );

        ClassRealm tmpRealm = classRealm.getParentRealm();

        while ( ( result == null ) && ( tmpRealm != null ) )
        {
            result = componentRepository.getComponentDescriptor( role, hint, classRealm );
            tmpRealm = tmpRealm.getParentRealm();
        }

        return result;
    }

    public Map<String, ComponentDescriptor> getComponentDescriptorMap( String role )
    {
        return getComponentDescriptorMap( role, null );
    }

    public Map<String, ComponentDescriptor> getComponentDescriptorMap( String role, ClassRealm realm )
    {
        Map<String, ComponentDescriptor> componentDescriptors = componentRepository.getComponentDescriptorMap( role, realm );
        return componentDescriptors;
    }

    public List<ComponentDescriptor> getComponentDescriptorList( String role )
    {
        return getComponentDescriptorList( role, null );
    }

    public List<ComponentDescriptor> getComponentDescriptorList( String role, ClassRealm realm )
    {
        Map<String, ComponentDescriptor> componentDescriptors = getComponentDescriptorMap( role, realm );

        return new ArrayList<ComponentDescriptor>( componentDescriptors.values() );
    }

    public List<ComponentDescriptor> getComponentDescriptorList( String role, List<String> roleHints, ClassRealm realm )
    {
        if ( roleHints != null )
        {
            ArrayList<ComponentDescriptor> descriptors = new ArrayList<ComponentDescriptor>( roleHints.size() );

            for ( String roleHint : roleHints )
            {
                ComponentDescriptor componentDescriptor = getComponentDescriptor( role, roleHint, realm );

                if ( componentDescriptor != null )
                {
                    descriptors.add( componentDescriptor );
                }
            }
            return descriptors;
        }
        else
        {
            return getComponentDescriptorList( role, realm );
        }

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
            // This needs to be tracked down but the user doesn't need to see this
            getLogger().debug( "Component manager not found for returned component. Ignored. component=" + component );
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

    public void releaseAll( Map<String, Object> components )
        throws ComponentLifecycleException
    {
        for ( Object component : components.values() )
        {
            release( component );
        }
    }

    public void releaseAll( List<Object> components )
        throws ComponentLifecycleException
    {
        for ( Object component : components )
        {
            release( component );
        }
    }

    public boolean hasComponent( String componentKey )
    {
        return hasComponent( componentKey, getLookupRealm() );
    }

    public boolean hasComponent( String componentKey, ClassRealm realm )
    {
        return componentRepository.hasComponent( componentKey, realm );
    }

    public boolean hasComponent( String role, String roleHint )
    {
        return hasComponent( role, roleHint, getLookupRealm() );
    }

    public boolean hasComponent( String role, String roleHint, ClassRealm realm )
    {
        return componentRepository.hasComponent( role, roleHint, realm );
    }

    // ----------------------------------------------------------------------
    // Lifecycle Management
    // ----------------------------------------------------------------------

    protected void initialize( ContainerConfiguration containerConfiguration )
        throws PlexusContainerException
    {
        if ( initialized )
        {
            throw new PlexusContainerException( "The container has already been initialized!" );
        }

        try
        {
            initializeConfiguration( containerConfiguration );

            initializePhases( containerConfiguration );
        }
        catch ( ContextException e )
        {
            throw new PlexusContainerException( "Error processing configuration", e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException( "Error configuring components", e );
        }
        catch ( IOException e )
        {
            throw new PlexusContainerException( "Error reading configuration file", e );
        }

        initialized = true;
    }

    protected void initializePhases( ContainerConfiguration containerConfiguration )
        throws PlexusContainerException
    {
        ContainerInitializationPhase[] initPhases = containerConfiguration.getInitializationPhases();

        ContainerInitializationContext initializationContext = new ContainerInitializationContext(
            this,
            classWorld,
            containerRealm,
            configuration,
            containerConfiguration );

        for ( ContainerInitializationPhase phase : initPhases )
        {
            try
            {
                phase.execute( initializationContext );
            }
            catch ( Exception e )
            {
                throw new PlexusContainerException( "Error initializaing container in " + phase.getClass().getName()
                    + ".", e );
            }
        }
    }

    // We need to be aware of dependencies between discovered components when the listed component
    // as the discovery listener itself depends on components that need to be discovered.
    public List discoverComponents( ClassRealm classRealm )
        throws PlexusConfigurationException,
            ComponentRepositoryException
    {
        return discoverComponents( classRealm, false );
    }

    /**
     * @see org.codehaus.plexus.MutablePlexusContainer#discoverComponents(org.codehaus.plexus.classworlds.realm.ClassRealm,boolean)
     */
    public List<ComponentDescriptor> discoverComponents( ClassRealm classRealm, boolean override )
        throws PlexusConfigurationException,
            ComponentRepositoryException
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
        try
        {
            disposeAllComponents();

            boolean needToDisposeRealm = false;
            
            // In dev mode i don't want to dispose of the realm in the world
            if ( !isDevMode() )
            {
                needToDisposeRealm = true;
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
        finally
        {
            staticLookupRealm = null;
            lookupRealm.set( null );
        }
    }

    protected void disposeAllComponents()
    {
        componentManagerManager.disposeAllComponents( getLogger() );
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

    protected void initializeConfiguration( ContainerConfiguration c )
        throws PlexusConfigurationException,
            ContextException,
            IOException
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

            containerRealm.display();

            throw new IllegalStateException( "The internal default plexus-bootstrap.xml is missing. "
                + "This is highly irregular, your plexus JAR is most likely corrupt. Realms:" + realmStack );
        }

        PlexusConfiguration bootstrapConfiguration = PlexusTools.buildConfiguration(
            PlexusConstants.BOOTSTRAP_CONFIGURATION,
            ReaderFactory.newXmlReader( is ) );

        // Some of this could probably be collapsed as having a plexus.xml in your
        // META-INF/plexus directory is probably a better solution then specifying
        // a configuration with an URL but I'm leaving the configuration by URL
        // as folks might be using it ... I made this change to accomodate Maven
        // but I think it's better to discover a configuration in a standard
        // place.

        configuration = bootstrapConfiguration;

        if ( !containerContext.contains( PlexusConstants.IGNORE_CONTAINER_CONFIGURATION )
            || ( containerContext.get( PlexusConstants.IGNORE_CONTAINER_CONFIGURATION ) != Boolean.TRUE ) )
        {
            PlexusXmlComponentDiscoverer discoverer = new PlexusXmlComponentDiscoverer();

            PlexusConfiguration plexusConfiguration = discoverer.discoverConfiguration( getContext(), containerRealm );

            if ( plexusConfiguration != null )
            {
                configuration = PlexusConfigurationMerger.merge( plexusConfiguration, configuration );
            }
        }

        if ( configurationReader != null )
        {
            // User userConfiguration

            PlexusConfiguration userConfiguration = PlexusTools.buildConfiguration(
                "<User Specified Configuration Reader>",
                getInterpolationConfigurationReader( configurationReader ) );

            // Merger of bootstrapConfiguration and user userConfiguration

            configuration = PlexusConfigurationMerger.merge( userConfiguration, configuration );
        }
    }

    protected Reader getInterpolationConfigurationReader( Reader reader )
    {
        return new InterpolationFilterReader( reader, new ContextMapAdapter( containerContext ) );
    }

    public void addJarResource( File jar )
        throws PlexusContainerException
    {
        try
        {
            containerRealm.addURL( jar.toURI().toURL() );

            if ( initialized )
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

            for ( File jar : jars )
            {
                if ( jar.getAbsolutePath().endsWith( ".jar" ) )
                {
                    try
                    {
                        addJarResource( jar );
                    }
                    catch ( PlexusContainerException e )
                    {
                        getLogger().warn( "Unable to add JAR: " + jar, e );
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

    public void removeComponentRealm( ClassRealm realm )
        throws PlexusContainerException
    {
        if ( getContainerRealm().getId().equals( realm.getId() ) )
        {
            throw new IllegalArgumentException( "Cannot remove container realm: " + realm.getId()
                + "\n(trying to remove container realm as if it were a component realm)." );
        }

        componentRepository.removeComponentRealm( realm );
        try
        {
            componentManagerManager.dissociateComponentRealm( realm );
        }
        catch ( ComponentLifecycleException e )
        {
            throw new PlexusContainerException( "Failed to dissociate component realm: " + realm.getId(), e );
        }

        ClassRealm lookupRealm = getLookupRealm();
        if ( ( lookupRealm != null ) && lookupRealm.getId().equals( realm.getId() ) )
        {
            setLookupRealm( getContainerRealm() );
        }
    }

    private InputStream toStream( String resource )
        throws PlexusContainerException
    {
        if ( resource == null )
        {
            return null;
        }

        String relativeResource = resource;
        if ( resource.startsWith( "/" ) )
        {
            relativeResource = resource.substring( 1 );
        }

        InputStream is = getClass().getClassLoader().getResourceAsStream( relativeResource );

        if ( is == null )
        {
            try
            {
                return new FileInputStream( resource );
            }
            catch ( FileNotFoundException e )
            {
                return null;
            }
        }

        return is;
    }

    /**
     * Utility method to get a default lookup realm for a component.
     */
    public ClassRealm getLookupRealm( Object component )
    {
        if ( component.getClass().getClassLoader() instanceof ClassRealm )
        {
            return ( (ClassRealm) component.getClass().getClassLoader() );
        }
        else
        {
            return getLookupRealm();
        }

    }

    public void setConfigurationSource( ConfigurationSource configurationSource )
    {
        this.configurationSource = configurationSource;
    }

    public ConfigurationSource getConfigurationSource()
    {
        return configurationSource;
    }
    
    public boolean isDevMode()
    {
        return this.devMode;
    }
}
