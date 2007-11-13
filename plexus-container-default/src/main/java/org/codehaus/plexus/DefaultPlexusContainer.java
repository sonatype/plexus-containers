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
import org.codehaus.plexus.util.ReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Map&lt;String, PlexusContainer> where the key is the container name.
     */
    protected Map childContainers = new WeakHashMap();

    protected Date creationDate = new Date();

    protected boolean reloadingEnabled;

    // TODO: Is there a more threadpool-friendly way to do this?
    private ThreadLocal lookupRealm = new ThreadLocal();

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
     * Used for getLookupRealm for threads when the threadlocal
     * doesn't contain a value.
     */
    private ClassRealm staticLookupRealm;

    public ClassRealm setLookupRealm( ClassRealm realm )
    {
        if ( realm == null )
        {
            return null;
        }

        ClassRealm oldRealm = (ClassRealm) lookupRealm.get();

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
        ClassRealm cr = (ClassRealm) lookupRealm.get();

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

    //TODO This needs to be connected to the parent realm most likely

    public ClassRealm createComponentRealm( String id, List jars )
        throws PlexusContainerException
    {
        ClassRealm componentRealm;

        try
        {
            ClassRealm realm = classWorld.getRealm( id );

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

    //
    // Make sure the lookups and classloaders are connected
    //
    public PlexusContainer createChildContainer( String name, Set urls )
    //    throws PlexusContainerException
    {
    //    return createChildContainer( name, new ClassRealm( name, urls ) );
        return null;
    }

    public PlexusContainer createChildContainer( String name, ClassRealm realm )
        throws PlexusContainerException
    {
        if ( hasChildContainer( name ) )
        {
            throw new DuplicateChildContainerException( getName(), name );
        }

        ContainerConfiguration c = new DefaultContainerConfiguration()
            .setName( name )
            .setParentContainer( this )
            .setClassWorld( new ClassWorld( name, realm ) );

        PlexusContainer childContainer=new DefaultPlexusContainer( c );
        childContainers.put( name, childContainer );

        return childContainer;
    }

    boolean initialized;

    private void construct( ContainerConfiguration c )
        throws PlexusContainerException
    {
        if ( c.getParentContainer() != null )
        {
            parentContainer = c.getParentContainer();

            loggerManager = parentContainer.getLoggerManager();

            containerRealm = (ClassRealm) c.getClassWorld().getRealms().iterator().next();
        }

        name = c.getName();

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

    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        return lookup( componentKey, getLookupRealm() );
    }

    public Object lookup( String componentKey, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( componentKey, realm );
    }

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        return lookupMap( role, getLookupRealm() );
    }

    public Map lookupMap( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, realm );
    }

    public List lookupList( String role )
        throws ComponentLookupException
    {
        return lookupList( role, getLookupRealm() );
    }

    public List lookupList( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, realm );
    }

    public Map lookupMap( String role, List hints )
        throws ComponentLookupException
    {
        return lookupMap( role, hints, getLookupRealm() );
    }

    public Map lookupMap( String role, List hints, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, hints, realm );
    }

    public List lookupList( String role, List hints )
        throws ComponentLookupException
    {
        return lookupList( role, hints, getLookupRealm() );
    }

    public List lookupList( String role, List hints, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, hints, realm );
    }

    public Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, getLookupRealm() );
    }

    public Object lookup( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, realm );
    }

    public Object lookup( Class componentClass )
        throws ComponentLookupException
    {
        return lookup( componentClass, getLookupRealm() );
    }

    public Object lookup( Class componentClass, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( componentClass, realm );
    }

    public Map lookupMap( Class role )
        throws ComponentLookupException
    {
        return lookupMap( role, getLookupRealm() );

    }

    public Map lookupMap( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, realm );
    }

    public List lookupList( Class role )
        throws ComponentLookupException
    {
        return lookupList( role, getLookupRealm() );
    }

    public List lookupList( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, realm );
    }

    public Object lookup( Class role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role, roleHint, getLookupRealm() );
    }

    public Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookup( role, roleHint, realm );
    }

    public Map lookupMap( Class role, List hints )
        throws ComponentLookupException
    {
        return lookupMap( role, hints, getLookupRealm() );

    }

    public Map lookupMap( Class role, List hints, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupMap( role, hints, realm );
    }

    public List lookupList( Class role, List hints )
        throws ComponentLookupException
    {
        return lookupList( role, hints, getLookupRealm() );
    }

    public List lookupList( Class role, List hints, ClassRealm realm )
        throws ComponentLookupException
    {
        return componentLookupManager.lookupList( role, hints, realm );
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


    // XXX remove
    public void setName( String name )
    {
        this.name = name;
    }

    // XXX remove!
    public void setParentPlexusContainer( PlexusContainer container )
    {
        parentContainer = container;
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

        if ( ( result == null ) && ( parentContainer != null ) )
        {
            result = parentContainer.getComponentDescriptor( role, hint, classRealm );
        }

        return result;
    }

    public Map getComponentDescriptorMap( String role )
    {
        return getComponentDescriptorMap( role, getLookupRealm() );
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

    public List getComponentDescriptorList( String role )
    {
        return getComponentDescriptorList( role, getLookupRealm() );
    }

    public List getComponentDescriptorList( String role, ClassRealm realm )
    {
        Map componentDescriptors = getComponentDescriptorMap( role, realm );

        return new ArrayList( componentDescriptors.values() );
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

        ContainerInitializationContext initializationContext =
            new ContainerInitializationContext( this, classWorld, containerRealm, configuration, containerConfiguration );

        for ( int i = 0; i < initPhases.length; i++ )
        {
            ContainerInitializationPhase phase = initPhases[i];

            try
            {
                phase.execute( initializationContext );
            }
            catch ( Exception e )
            {
                throw new PlexusContainerException( "Error initializaing container in " + phase.getClass().getName() + ".", e );
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
     * @see org.codehaus.plexus.MutablePlexusContainer#discoverComponents(org.codehaus.plexus.classworlds.realm.ClassRealm,boolean)
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
        try
        {
            disposeAllComponents();

            boolean needToDisposeRealm = true;

            if ( ( parentContainer != null ) && containerRealm.getId().equals( parentContainer.getContainerRealm().getId() ) )
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
        finally
        {
            lookupRealm.set( null );
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

    protected void initializeConfiguration( ContainerConfiguration c )
        throws ConfigurationProcessingException, ConfigurationResourceNotFoundException, PlexusConfigurationException,
        ContextException, IOException
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

        PlexusConfiguration bootstrapConfiguration = PlexusTools
            .buildConfiguration( PlexusConstants.BOOTSTRAP_CONFIGURATION, ReaderFactory.newXmlReader( is ) );

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

                    Reader reader = null;
                    try
                    {
                        reader = ReaderFactory.newXmlReader( componentConfigurationFile );
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
                    catch ( IOException e )
                    {
                        throw new PlexusConfigurationException( "IO error while reading " + componentConfigurationFile, e );
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

    public void removeComponentRealm( ClassRealm realm )
        throws PlexusContainerException
    {
        if ( getContainerRealm().getId().equals( realm.getId() ) )
        {
            throw new IllegalArgumentException(
                                                "Cannot remove container realm: "
                                                                + realm.getId()
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
}
