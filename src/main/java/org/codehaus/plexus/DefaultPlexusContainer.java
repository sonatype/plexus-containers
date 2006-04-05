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

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.component.ComponentSelector;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.SetterComponentComposer;
import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.discovery.DiscoveryListenerDescriptor;
import org.codehaus.plexus.component.discovery.PlexusXmlComponentDiscoverer;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.factory.UndefinedComponentFactoryException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.UndefinedComponentManagerException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.configuration.processor.ConfigurationProcessingException;
import org.codehaus.plexus.configuration.processor.ConfigurationProcessor;
import org.codehaus.plexus.configuration.processor.ConfigurationResourceNotFoundException;
import org.codehaus.plexus.configuration.processor.DirectoryConfigurationResourceHandler;
import org.codehaus.plexus.configuration.processor.FileConfigurationResourceHandler;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.session.InvalidSessionException;
import org.codehaus.plexus.session.PlexusContainerSession;
import org.codehaus.plexus.session.SessionException;
import org.codehaus.plexus.session.SessionId;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.StringUtils;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

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
    private static final int SESSION_ID_LEN = 4;
    
    private Random random = new Random();
    
    private PlexusContainer parentContainer;
    
    private Map sessions = new HashMap();

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

    private Map childContainers = new WeakHashMap();

    public static final String BOOTSTRAP_CONFIGURATION = "org/codehaus/plexus/plexus-bootstrap.xml";

    private boolean started = false;

    private boolean initialized = false;

    private final Date creationDate = new Date();

    private boolean reloadingEnabled;

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    public DefaultPlexusContainer()
    {
        context = new DefaultContext();
    }

    // ----------------------------------------------------------------------
    // Container Contract
    // ----------------------------------------------------------------------
    
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

        DefaultPlexusContainer child = new DefaultPlexusContainer();

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

        childRealm.setParent( plexusRealm );

        child.coreRealm = childRealm;

        child.plexusRealm = childRealm;

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
        // component manager manager to create a component manager for us. Also if we are reloading
        // components then we'll also get a new component manager.

        if ( reloadingEnabled || componentManager == null )
        {
            ComponentDescriptor descriptor = componentRepository.getComponentDescriptor( componentKey );

            if ( descriptor == null )
            {
                if ( parentContainer != null )
                {
                    return parentContainer.lookup( componentKey );
                }

                // don't need this AND an exception...we'll put it at the debug output level, rather than error...
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Nonexistent component: " + componentKey );
                }

                String message = "Component descriptor cannot be found in the component repository: " + componentKey + ".";

                throw new ComponentLookupException( message );
            }

            componentManager = createComponentManager( descriptor );
        }

        try
        {
            component = componentManager.getComponent();
        }
        catch ( ComponentInstantiationException e )
        {
            throw new ComponentLookupException( "Unable to lookup component '" + componentKey + "', it could not be created", e );
        }
        catch ( ComponentLifecycleException e )
        {
            throw new ComponentLookupException( "Unable to lookup component '" + componentKey + "', it could not be started", e );
        }

        componentManagerManager.associateComponentWithComponentManager( component, componentManager );

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
        catch ( UndefinedComponentManagerException e )
        {
            String message = "Cannot create component manager for " + descriptor.getComponentKey() + ", so we cannot provide a component instance.";

            throw new ComponentLookupException( message, e );
        }
        catch ( UndefinedLifecycleHandlerException e )
        {
            String message = "Cannot create component manager for " + descriptor.getComponentKey() + ", so we cannot provide a component instance.";

            throw new ComponentLookupException( message, e );
        }

        return componentManager;
    }

    /**
     * @todo Change this to include components looked up from parents as well...
     */
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

    /**
     * @todo Change this to include components looked up from parents as well...
     */
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

    public boolean hasComponent( String role, String roleHint )
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

    /**
     * @deprecated Use getContainerRealm() instead.
     */
    public ClassRealm getComponentRealm( String id )
    {
        return plexusRealm;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public void initialize()
        throws PlexusContainerException
    {
        try
        {
            initializeClassWorlds();

            initializeConfiguration();

            initializeResources();

            initializeCoreComponents();

            initializeLoggerManager();

            initializeContext();

            initializeSystemProperties();

            this.initialized = true;
        }
        catch ( DuplicateRealmException e )
        {
            throw new PlexusContainerException( "Error initializing classworlds", e );
        }
        catch ( ConfigurationProcessingException e )
        {
            throw new PlexusContainerException( "Error processing configuration", e );
        }
        catch ( ConfigurationResourceNotFoundException e )
        {
            throw new PlexusContainerException( "Error processing configuration", e );
        }
        catch ( ComponentConfigurationException e )
        {
            throw new PlexusContainerException( "Error configuring components", e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException( "Error configuring components", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new PlexusContainerException( "Error initializing components", e );
        }
        catch ( ContextException e )
        {
            throw new PlexusContainerException( "Error contextualizing components", e );
        }
    }

    public void registerComponentDiscoveryListeners()
        throws ComponentLookupException
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
     */
    public List discoverComponents( ClassRealm classRealm )
        throws PlexusConfigurationException, ComponentRepositoryException
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

                if ( componentDescriptors != null )
                {
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
        }

        return discoveredComponentDescriptors;
    }

    // We need to be aware of dependencies between discovered components when the listed component
    // as the discovery listener itself depends on components that need to be discovered.

    public boolean isStarted()
    {
        return started;
    }

    public void start()
        throws PlexusContainerException
    {
        try
        {
            registerComponentDiscoveryListeners();

            discoverComponents( plexusRealm );

            loadComponentsOnStart();

            this.started = true;
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException( "Error starting container", e );
        }
        catch ( ComponentLookupException e )
        {
            throw new PlexusContainerException( "Error starting container", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new PlexusContainerException( "Error starting container", e );
        }

        configuration = null;
    }

    public void dispose()
    {
        disposeAllComponents();
        
        if ( parentContainer != null )
        {
            parentContainer.removeChildContainer( getName() );
            parentContainer = null;
        }
        
        try
        {
            plexusRealm.setParent( null );
            classWorld.disposeRealm( plexusRealm.getId() );
        }
        catch ( NoSuchRealmException e )
        {
            getLogger().debug( "Failed to dispose realm for exiting container: " + getName(), e );
        }

        this.started = false;
        this.initialized = true;
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
     * @todo don't hold this reference - the reader will remain open forever
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
            else if ( roleHint.equals( "*" ) )
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
        throws DuplicateRealmException
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

    public ClassRealm getContainerRealm()
    {
        return plexusRealm;
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
        throws ConfigurationProcessingException, ConfigurationResourceNotFoundException, PlexusConfigurationException
    {
        // System userConfiguration

        InputStream is = coreRealm.getResourceAsStream( BOOTSTRAP_CONFIGURATION );

        if ( is == null )
        {
            throw new IllegalStateException( "The internal default plexus-bootstrap.xml is missing. " +
                "This is highly irregular, your plexus JAR is " +
                "most likely corrupt." );
        }

        PlexusConfiguration systemConfiguration = PlexusTools.buildConfiguration( BOOTSTRAP_CONFIGURATION, new InputStreamReader( is ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

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

        PlexusXmlComponentDiscoverer discoverer = new PlexusXmlComponentDiscoverer();
        PlexusConfiguration plexusConfiguration = discoverer.discoverConfiguration( getContext(), plexusRealm );

        if ( plexusConfiguration != null )
        {
            configuration = PlexusConfigurationMerger.merge( plexusConfiguration, configuration );

            processConfigurationsDirectory();
        }

        if ( configurationReader != null )
        {
            // User userConfiguration

            PlexusConfiguration userConfiguration =
                PlexusTools.buildConfiguration( "<User Specified Configuration Reader>", getInterpolationConfigurationReader( configurationReader ) );

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

        configuration = p.process( configuration, Collections.EMPTY_MAP );
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
        throws PlexusConfigurationException
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
                List componentConfigurationFiles = null;
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
                        PlexusConfiguration componentConfiguration =
                            PlexusTools.buildConfiguration( componentConfigurationFile.getAbsolutePath(), getInterpolationConfigurationReader( reader ) );

                        componentsConfiguration.addChild( componentConfiguration.getChild( "components" ) );
                    }
                    catch ( FileNotFoundException e )
                    {
                        throw new PlexusConfigurationException( "File " + componentConfigurationFile + " disappeared before processing", e );
                    }
                    finally
                    {
                        IOUtil.close( reader );
                    }
                }
            }
        }
    }

    private void initializeLoggerManager()
        throws PlexusContainerException
    {
        // ----------------------------------------------------------------------
        // The logger manager may have been set programmatically so we need
        // to check. If it hasn't
        // ----------------------------------------------------------------------

        if ( loggerManager == null )
        {
            try
            {
                loggerManager = (LoggerManager) lookup( LoggerManager.ROLE );
            }
            catch ( ComponentLookupException e )
            {
                throw new PlexusContainerException( "Unable to locate logger manager", e );
            }
        }

        enableLogging( loggerManager.getLoggerForComponent( PlexusContainer.class.getName() ) );
    }

    private void initializeCoreComponents()
        throws ComponentConfigurationException, ComponentRepositoryException, ContextException
    {
        BasicComponentConfigurator configurator = new BasicComponentConfigurator();

        PlexusConfiguration c = configuration.getChild( "component-repository" );

        processCoreComponentConfiguration( "component-repository", configurator, c );

        componentRepository.configure( configuration );

        componentRepository.setClassRealm( plexusRealm );

        componentRepository.initialize();

        // Lifecycle handler manager

        c = configuration.getChild( "lifecycle-handler-manager" );

        processCoreComponentConfiguration( "lifecycle-handler-manager", configurator, c );

        lifecycleHandlerManager.initialize();

        // Component manager manager

        c = configuration.getChild( "component-manager-manager" );

        processCoreComponentConfiguration( "component-manager-manager", configurator, c );

        componentManagerManager.setLifecycleHandlerManager( lifecycleHandlerManager );

        // Component discoverer manager

        c = configuration.getChild( "component-discoverer-manager" );

        processCoreComponentConfiguration( "component-discoverer-manager", configurator, c );

        componentDiscovererManager.initialize();

        // Component factory manager

        c = configuration.getChild( "component-factory-manager" );

        processCoreComponentConfiguration( "component-factory-manager", configurator, c );

        if ( componentFactoryManager instanceof Contextualizable )
        {
            Context context = getContext();

            context.put( PlexusConstants.PLEXUS_KEY, this );

            ( (Contextualizable) componentFactoryManager ).contextualize( getContext() );
        }

        // Component factory manager

        c = configuration.getChild( "component-composer-manager" );

        processCoreComponentConfiguration( "component-composer-manager", configurator, c );
    }

    private void processCoreComponentConfiguration( String role, BasicComponentConfigurator configurator, PlexusConfiguration c )
        throws ComponentConfigurationException
    {
        String implementation = c.getAttribute( "implementation", null );

        if ( implementation == null )
        {

            String msg = "Core component: '" +
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

        try
        {
            configurator.configureComponent( this, configuration, plexusRealm );
        }
        catch ( ComponentConfigurationException e )
        {
            // TODO: don't like rewrapping the same exception, but better than polluting this all through the config code
            String message = "Error configuring component: " + componentDescriptor.getHumanReadableKey();
            throw new ComponentConfigurationException( message, e );
        }
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
            catch ( MalformedURLException e )
            {
                String message = "Error configuring resource: " + resourceConfigs[i].getName() + "=" + resourceConfigs[i].getValue();
                if ( getLogger() != null )
                {
                    getLogger().error( message, e );
                }
                else
                {
                    System.out.println( message );

                    e.printStackTrace();
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void addJarResource( File jar )
        throws PlexusContainerException
    {
        try
        {
            plexusRealm.addConstituent( jar.toURL() );

            if ( isStarted() )
            {
                discoverComponents( plexusRealm );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new PlexusContainerException( "Cannot add jar resource: " + jar + " (bad URL)", e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusContainerException( "Cannot add jar resource: " + jar + " (error discovering new components)", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new PlexusContainerException( "Cannot add jar resource: " + jar + " (error discovering new components)", e );
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
            String message = "The specified JAR repository doesn't exist or is not a directory: '" + repository.getAbsolutePath() + "'.";

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

    public Object createComponentInstance( ComponentDescriptor componentDescriptor )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        String componentFactoryId = componentDescriptor.getComponentFactory();

        ComponentFactory componentFactory = null;
        Object component = null;

        try
        {
            if ( componentFactoryId != null )
            {
                componentFactory = componentFactoryManager.findComponentFactory( componentFactoryId );
            }
            else
            {
                componentFactory = componentFactoryManager.getDefaultComponentFactory();
            }

            component = componentFactory.newInstance( componentDescriptor, plexusRealm, this );
        }
        catch ( UndefinedComponentFactoryException e )
        {
            throw new ComponentInstantiationException( "Unable to create component as factory '" + componentFactoryId + "' could not be found", e );
        }
        finally
        {
            // the java factory is a special case, without a component manager.
            // Don't bother releasing the java factory.
            if ( StringUtils.isNotEmpty( componentFactoryId ) && !"java".equals( componentFactoryId ) )
            {
                release( componentFactory );
            }
        }

        return component;
    }

    public void composeComponent( Object component, ComponentDescriptor componentDescriptor )
        throws CompositionException, UndefinedComponentComposerException
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

    // ----------------------------------------------------------------------
    // Start of new programmatic API to fully control the container
    // ----------------------------------------------------------------------

    public void setLoggerManager( LoggerManager loggerManager )
    {
        this.loggerManager = loggerManager;
    }

    public LoggerManager getLoggerManager()
    {
        return loggerManager;
    }

    public SessionId createSession()
        throws PlexusContainerException
    {
        SessionId sessionId = new SessionId( createSessionIdentifier() );
        
        sessions.put( sessionId, new PlexusContainerSession( sessionId ) );
        
        return sessionId;
    }

    private String createSessionIdentifier()
    {
        StringBuffer buffer = new StringBuffer();
        
        for ( int i = 0; i < SESSION_ID_LEN; i++ )
        {
            buffer.append( random.nextInt( 16 ) );
        }
        
        return buffer.toString();
    }

    public void closeSession( SessionId sessionId )
    {
        PlexusContainerSession session = (PlexusContainerSession) sessions.get( sessionId );
        
        if ( session != null )
        {
            session.close();
            sessions.remove( sessionId );
        }
        
    }
    
    private PlexusContainerSession getSession( SessionId sessionId )
        throws SessionException
    {
        PlexusContainerSession session = (PlexusContainerSession) sessions.get( sessionId );
        
        if ( session == null )
        {
            throw new InvalidSessionException( sessionId );
        }
        
        return session;
    }

    public void touchSession( SessionId sessionId ) throws SessionException
    {
        getSession( sessionId ).touch();
    }

    public void registerSelector( ComponentSelector selector, SessionId sessionId )
        throws PlexusContainerException, SessionException
    {
        getSession( sessionId ).registerSelector( selector );
    }

    public void deregisterSelector( ComponentSelector selector, SessionId sessionId )
        throws PlexusContainerException, SessionException
    {
        getSession( sessionId ).deregisterSelector( selector );
    }
    
    private ComponentSelector getSelector( String role, SessionId sessionId, boolean constructDefault ) 
        throws SessionException, SessionException
    {
        ComponentSelector selector = getSession( sessionId ).getSelector( role );
        
        if ( constructDefault && selector == null )
        {
            selector = new ComponentSelector( role );
        }
        
        return selector;
    }

    public Object lookup( String role, SessionId sessionId )
        throws ComponentLookupException, SessionException
    {
        ComponentSelector selector = getSelector( role, sessionId, true );
        
        ComponentLookupException ex = null;
        Object result = null;
        
        for ( Iterator it = selector.getRoleHints().iterator(); it.hasNext(); )
        {
            String hint = (String) it.next();
            
            try
            {
                result = lookup( role, hint );
                ex = null;
                
                break;
            }
            catch ( ComponentLookupException e )
            {
                if ( ex == null )
                {
                    ex = e;
                }
            }
        }
        
        if ( ex != null )
        {
            throw ex;
        }
        
        return result;
    }

    public Map lookupMap( String role, SessionId sessionId )
        throws ComponentLookupException, SessionException
    {
        ComponentSelector selector = getSelector( role, sessionId, false );
        
        Map result;
        if ( selector != null )
        {
            result = new HashMap();
            
            for ( Iterator it = selector.getRoleHints().iterator(); it.hasNext(); )
            {
                String hint = (String) it.next();
                
                result.put( hint, lookup( role, hint ) );
            }
        }
        else
        {
            result = lookupMap( role );
        }
        
        return result;
    }

    public List lookupList( String role, SessionId sessionId )
        throws ComponentLookupException, SessionException
    {
        ComponentSelector selector = getSelector( role, sessionId, false );
        
        List result;
        if ( selector != null )
        {
            result = new ArrayList();
            
            for ( Iterator it = selector.getRoleHints().iterator(); it.hasNext(); )
            {
                String hint = (String) it.next();
                
                result.add( lookup( role, hint ) );
            }
        }
        else
        {
            result = lookupList( role );
        }
        
        return result;
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
        Object component = plexusRealm.loadClass( clazz ).newInstance();

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
}
