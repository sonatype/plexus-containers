package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerFactory;
import org.codehaus.plexus.lifecycle.LifecycleHandlerHousing;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.lifecycle.avalon.AvalonServiceManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.util.ThreadSafeMap;
import org.codehaus.plexus.util.Tracer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultComponentRepository
    extends AbstractLogEnabled
    implements ComponentRepository
{
    // ----------------------------------------------------------------------
    //  Constants
    // ----------------------------------------------------------------------

    /** Components tag. */
    private static String COMPONENTS = "components";

    /** Component tag. */
    private static String COMPONENT = "component";

    /** Id tag. */
    private static String ID = "id";

    /** Role tag. */
    private static String ROLE = "role";

    /** Role tag. */
    private static String ROLE_HINT = "role-hint";

    /** Implementation tag. */
    private static String IMPLEMENTATION = "implementation";

    /** Configuration tag. */
    private static String INSTANTIATION_STRATEGY = "instantiation-strategy";

    /** Configuration tag. */
    private static String CONFIGURATION = "configuration";

    /** Default instantiation strategy tag. */
    protected static String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    /** Instance manager tag. */
    private static String INSTANCE_MANAGER = "instance-manager";

    /** Instance managers tag. */
    private static String INSTANCE_MANAGERS = "instance-managers";

    /** Lifecycle handler tag. */
    private static String LIFECYCLE_HANDLER = "lifecycle-handler";

    /** Lifecycle handlers tag. */
    private static String LIFECYCLE_HANDLERS = "lifecycle-handlers";


    // ----------------------------------------------------------------------
    //  Instance Members
    // ----------------------------------------------------------------------

    /** Configuration */
    private Configuration configuration;

    /** Map of component descriptors keyed by role. */
    private Map componentDescriptors;

    /** Map of component managers by component key. Needs to be
     * threadSafe with lots of reads, small number of writes.*/
    private Map componentManagers;

    /** Map of ComponentManagers keyed by component class. Use a Map
     * which can handle concurrent reads and writes. Will be about
     * the same number of reads as writes
     */
    private Map compManagersByCompClass;

    /** Map of component housings keyed by the component object. */
    //private Map componentHousings;

    private PlexusContainer plexusContainer;

    /** Parent containers context */
    private Context context;

    /** Logger manager. */
    private LoggerManager loggerManager;

    /** The manager manager descriptors. Seperate from the other
     * components as they shouldn't have access to them. Keyed
     * by instantiation strategy*/
    private Map componentManagerDescriptors;

    /** Lifecycle handlers. */
    private Map lifecycleHandlers;

    /** Default instantiation strategy. */
    private String defaultInstantiationStrategy;

    /** Default lifecycle handler. */
    private LifecycleHandler defaultLifecycleHandler;

    /**
     * Object to lock when creating a new component manager during
     * component lookup. Separate from enclosing class as we have no control
     * on what locks calling code places.
     */
    private Object lookupLock = new Object();

    /**
     * ServiceManager given to InstanceManagers if they need access to
     * other components. Care must be taken no circular dependencies
     * are created.
     */
    private ServiceManager service;

    /** Constructor. */
    public DefaultComponentRepository()
    {
        componentDescriptors = new HashMap();

        componentManagerDescriptors = new HashMap();

        componentManagers = new ThreadSafeMap();

        compManagersByCompClass = new ThreadSafeMap();

        lifecycleHandlers = new HashMap();

        service = new AvalonServiceManager( this );
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    /**
     *
     * @return
     */
    public PlexusContainer getPlexusContainer()
    {
        return plexusContainer;
    }

    /**
     *
     * @param plexusContainer
     */
    public void setPlexusContainer( PlexusContainer plexusContainer )
    {
        this.plexusContainer = plexusContainer;
    }

    /**
     *
     * @return
     */
    public ClassLoader getClassLoader()
    {
        return getPlexusContainer().getClassLoader();
    }

    /**
     * @see org.codehaus.plexus.component.repository.ComponentRepository#getDefaultLifecycleHandler()
     */
    public LifecycleHandler getDefaultLifecycleHandler()
    {
        return defaultLifecycleHandler;
    }

    /**
     * @return
     */
    Context getContext()
    {
        return context;
    }

    /**
     * @return
     */
    public LoggerManager getComponentLogManager()
    {
        return loggerManager;
    }

    /**
     * @param manager
     */
    public void setComponentLogManager( LoggerManager manager )
    {
        loggerManager = manager;
    }

    public LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException
    {
        LifecycleHandlerHousing h = null;

        if ( id != null )
        {
            h = (LifecycleHandlerHousing) lifecycleHandlers.get( id );
        }

        if ( h == null )
        {
            throw new UndefinedLifecycleHandlerException( "No LifecycleHandler defined for id: " + id );
        }
        return h.getHandler();
    }

    /**
     * @return
     */
    protected Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public synchronized boolean hasService( String role )
    {
        return getComponentDescriptors().containsKey( role );
    }

    public synchronized boolean hasService( String role, String id )
    {
        return getComponentDescriptors().containsKey( role + id );
    }

    /**
     *
     * @return
     */
    Map getComponentDescriptors()
    {
        return componentDescriptors;
    }

    /**
     *
     * @return
     */
    Map getComponentManagers()
    {
        return componentManagers;
    }

    ComponentManager getComponentManager( String componentKey )
    {
        return (ComponentManager) getComponentManagers().get( componentKey );
    }

    Map getComponentManagerDescriptors()
    {
        return componentManagerDescriptors;
    }

    /**
     * @todo correct this
     * @see org.codehaus.plexus.component.repository.ComponentRepository#getComponentCount()
     */
    public int getComponentCount()
    {
        //this is no longer correct. Each manager could
        //be managing multiple instances. Should sum
        //the number of component managers active
        //connections
        return getComponentManagers().size();
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /**
     * @see org.codehaus.plexus.component.repository.ComponentRepository#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize( Context context )
    {
        this.context = context;
    }

    /** Configure the component repository.
     *
     * @param configuration
     */
    public void configure( Configuration configuration )
    {
        this.configuration = configuration;
    }

    /** Initialize the component repository.
     *
     * @throws Exception
     */
    public void initialize()
        throws Exception
    {
        initializeLifecycleHandlers();
        initializeComponentManagers();
        initializeComponentDescriptors();
    }

    /**
     * Adds all the lifecycle handlers and initializes them. Sets up the default lifecycle handler
     */
    private void initializeLifecycleHandlers()
        throws Exception
    {
        String defaultHandlerId = getConfiguration().getChild( LIFECYCLE_HANDLERS )
            .getAttribute( "default", getConfiguration().getChild( LIFECYCLE_HANDLERS ).getAttribute( "default", null ) );

        if ( defaultHandlerId == null )
        {
            throw new ConfigurationException( "No default lifecycle handler defined" );
        }

        Configuration[] configs = getConfiguration().getChild( LIFECYCLE_HANDLERS ).getChildren( LIFECYCLE_HANDLER );

        for ( int i = 0; i < configs.length; i++ )
        {
            addLifecycleHandlerHousing( configs[i], false );
        }

        //grab the default LifecycleHandler. This is the one used when components don't specify
        //one
        LifecycleHandlerHousing housing = (LifecycleHandlerHousing) lifecycleHandlers.get( defaultHandlerId );

        if ( housing == null )
        {
            throw new ConfigurationException(
                "The default LifecycleHandler is specified as: "
                + defaultHandlerId
                + " but no LifecycleHandler"
                + " of this id is defined" );

        }

        defaultLifecycleHandler = housing.getHandler();

        getLogger().info( "Default LifecycleHandler id is set to: '" + defaultHandlerId + "'" );
    }

    /**
     * Add a LifecycleHandler to this container.
     *
     * @param config
     * @param ignoreDuplicates if duplicate handlers should be quitely ignored
     * @throws Exception
     */
    void addLifecycleHandlerHousing( Configuration config, boolean ignoreDuplicates )
        throws Exception
    {
        LifecycleHandlerHousing housing =
            LifecycleHandlerFactory.createLifecycleHandlerHousing( config,
                                                                   getComponentLogManager(),
                                                                   getClassLoader(),
                                                                   getContext(),
                                                                   this );

        if ( !lifecycleHandlers.containsKey( housing.getId() ) )
        {
            getLogger().info(
                "Adding Lifecyclehandler. id="
                + housing.getId()
                + ", impl="
                + housing.getImplementation() );

            lifecycleHandlers.put( housing.getId(), housing );
        }
        else
        {
            if ( ignoreDuplicates == false )
            {
                throw new ConfigurationException(
                    "Duplicate Lifecycle handler. Duplicate id: " + housing.getId() );
            }
        }
    }

    /**
     * Grab all the InstanceManager configurations and make them available
     * during lookup
     *
     * @throws Exception
     */
    private void initializeComponentManagers()
        throws Exception
    {
        Configuration[] componentConfigurations =
            configuration.getChild( INSTANCE_MANAGERS ).getChildren( INSTANCE_MANAGER );

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            addComponentManagerDescriptor( createComponentDescriptor( componentConfigurations[i] ) );
        }

        defaultInstantiationStrategy = getConfiguration().getChild( INSTANCE_MANAGERS ).getAttribute(
            "default", getConfiguration().getChild( INSTANCE_MANAGERS ).getAttribute( "default", null ) );

        if ( defaultInstantiationStrategy == null || defaultInstantiationStrategy.length() == 0 )
        {
            throw new ConfigurationException( "No default instantiation strategy defined" );
        }

        if ( !getComponentManagerDescriptors().containsKey( defaultInstantiationStrategy ) )
        {
            throw new ConfigurationException(
                "The default instantiation strategy is specified as: '"
                + defaultInstantiationStrategy
                + "' but no InstanceManager"
                + " with this id is defined" );
        }

        getLogger().info( "Default instantiation strategy set to: '" + defaultInstantiationStrategy + "'" );
    }

    /**
     * Adds a InstanceManager to this repository.
     *
     * @param descriptor
     */
    protected void addComponentManagerDescriptor( ComponentDescriptor descriptor )
    {
        getLogger().info(
            "Adding manager manager descriptor. strategy="
            + descriptor.getInstantiationStrategy()
            + ", impl="
            + descriptor.getImplementation() );

        getComponentManagerDescriptors().put( descriptor.getInstantiationStrategy(), descriptor );
    }

    /**
     * Create a new ComponentManager with the correct InstanceManager for the
     * component specified by the given descriptor. The ComponentManager
     * will select the correct LifecycleHandler based on the descriptor
     *
     * @return The new component manager.
     *
     * @throws Exception If an error occurs while attempting to locate
     *         the class or instantiate the component object.
     */
    ComponentManager instantiateComponentManager( ComponentDescriptor descriptor )
        throws Exception
    {
        ComponentDescriptor componentManagerDescriptor;

        String strategy = descriptor.getInstantiationStrategy();

        //donˈt want a 'ROLE#null' lookup
        if ( strategy == null )
        {
            strategy = defaultInstantiationStrategy;
        }

        componentManagerDescriptor = (ComponentDescriptor) getComponentManagerDescriptors().get( strategy );

        if ( componentManagerDescriptor == null )
        {
            throw new ConfigurationException(
                "No instance manager configured with strategy: "
                + strategy
                + " for component with role: "
                + descriptor.getRole() );
        }

        LifecycleHandlerHousing lhh;
        LifecycleHandler h;

        String id = descriptor.getLifecycleHandler();

        if ( id != null )
        {
            lhh = (LifecycleHandlerHousing) lifecycleHandlers.get( id );
            h = lhh.getHandler();
        }
        else
        {
            h = defaultLifecycleHandler;
        }

        ComponentManager componentManager = ComponentManagerFactory.create( componentManagerDescriptor,
                                                                            getComponentLogManager(),
                                                                            getClassLoader(),
                                                                            h,
                                                                            descriptor,
                                                                            service );

        //componentManager.initialize();//performed in ComponentFactory

        //make the ComponentManager available for future requests
        getComponentManagers().put( descriptor.getComponentKey(), componentManager );

        return componentManager;
    }

    /**
     * Grab all the component descriptors from the configuration and
     * make them available during lookup
     *
     * @throws Exception
     */
    public void initializeComponentDescriptors()
        throws Exception
    {
        Configuration[] componentConfigurations =
            configuration.getChild( COMPONENTS ).getChildren( COMPONENT );

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            addComponentDescriptor( createComponentDescriptor( componentConfigurations[i] ) );
        }
    }

    /**
     * Create a component descriptor.
     *
     * @param configuration
     * @return
     * @throws Exception
     */
    ComponentDescriptor createComponentDescriptor( Configuration configuration )
        throws Exception
    {
        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( configuration.getChild( ROLE ).getValue() );

        componentDescriptor.setRoleHint( configuration.getChild( ROLE_HINT ).getValue( null ) );

        componentDescriptor.setImplementation( configuration.getChild( IMPLEMENTATION ).getValue() );

        componentDescriptor.setId( configuration.getChild( ID ).getValue( null ) );

        componentDescriptor.setInstantiationStrategy( configuration.getChild( INSTANTIATION_STRATEGY ).getValue( null ) );

        componentDescriptor.setLifecycleHandler( configuration.getChild( LIFECYCLE_HANDLER ).getValue( null ) );

        componentDescriptor.setConfiguration( configuration.getChild( CONFIGURATION ) );

        return componentDescriptor;
    }

    // ----------------------------------------------------------------------
    //  Component Descriptor processing and Holder creation.
    // ----------------------------------------------------------------------



    /**
     * Adds a component to the ServiceBroker.  If the component has a
     * ServiceSelector, the appropriate action is taken.
     *
     * @param descriptor
     */
    protected void addComponentDescriptor( ComponentDescriptor descriptor )
    {
        if ( getLogger().isDebugEnabled() )
        {
            StringBuffer buff = new StringBuffer();
            buff.append( "Adding ComponentDescriptor. role=" );
            buff.append( descriptor.getRole() );
            buff.append( ", id=" );
            buff.append( descriptor.getId() );
            buff.append( ",role-hint=" );
            buff.append( descriptor.getRoleHint() );
            buff.append( ",strategy=" );
            buff.append( descriptor.getInstantiationStrategy() );
            buff.append( ", impl=" );
            buff.append( descriptor.getImplementation() );

            getLogger().debug( buff.toString() );
        }
        getComponentDescriptors().put( descriptor.getComponentKey(), descriptor );
    }


    // ----------------------------------------------------------------------
    // Service lookup methods
    // ----------------------------------------------------------------------

    public synchronized Object lookup( String key )
        throws ServiceException
    {
        // Attempt to lookup the componentManager by key.
        ComponentManager componentManager = getComponentManager( key );

        Object component = null;

        //have todo some synchronization stuff here as two different threads may
        //try to create seperate instances of the same component managers. Need
        //to block one until the other has created it.Seeing this happens once
        //per component it shouldn't be a drag on performance
        if ( componentManager == null )
        {
            //lock, and check for component manager again within
            //synch block, as another thread may have just created one
            synchronized ( lookupLock )
            {
                componentManager = getComponentManager( key );
                if ( componentManager != null )
                {
                    try
                    {
                        return componentManager.getComponent();
                    }
                    catch ( Exception e )
                    {
                        throw new ServiceException(
                            key,
                            "Error retrieving component from ComponentManager" );
                    }
                }
                // We need to create an manager of this componentManager.
                getLogger().debug( "Creating new ComponentDescriptor for role: " + key );

                ComponentDescriptor descriptor = (ComponentDescriptor) getComponentDescriptors().get( key );

                if ( descriptor == null )
                {
                    getLogger().error( "Non existant component: " + key );
                    throw new ServiceException( key, "Non existant component for key " + key + "." );
                }

                try
                {
                    componentManager = instantiateComponentManager( descriptor );
                }
                catch ( Exception e )
                {
                    getLogger().error( "Could not create component: " + key, e );
                    throw new ServiceException(
                        key,
                        "Could not create component for key " + key + "!",
                        e );
                }
                try
                {
                    component = componentManager.getComponent();
                }
                catch ( Exception e )
                {
                    throw new ServiceException(
                        key,
                        "Error retrieving component from ComponentManager. cause="
                        + Tracer.traceToString( e ) );
                }
                if ( getLogger().isDebugEnabled() )
                {
                    StringBuffer buff = new StringBuffer();
                    buff.append( "Obtained new component :role=" ).append( descriptor.getRole() );
                    buff.append( ",impl=" ).append( descriptor.getImplementation() );
                    buff.append( ",lifecycle-id=" ).append( descriptor.getLifecycleHandler() );
                    buff.append( ",strategy=" ).append( descriptor.getInstantiationStrategy() );
                    getLogger().debug( buff.toString() );
                }
                // We do this so we know what to do when releasing. Only have to do it once
                //per component class
                compManagersByCompClass.put( component.getClass().getName(), componentManager );

                lookupLock.notifyAll();
            }

        }
        else
        {
            try
            {
                component = componentManager.getComponent();
            }
            catch ( Exception e )
            {
                throw new ServiceException( key, "Error retrieving component from ComponentManager" );
            }
        }
        return component;
    }

    public synchronized Object lookup( String role, String id )
        throws ServiceException
    {
        return lookup( role + id );
    }


    /**
     * Release the specified component.
     *
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public synchronized void release( Object component )
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager cm = (ComponentManager) compManagersByCompClass.get( component.getClass().getName() );

        //this repository does not deal with this component
        if ( cm == null )
        {
            return;
        }

        cm.release( component );
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public synchronized void dispose()
    {
        getLogger().info( "Disposing ComponentRepository..." );
        disposeAllComponents();
    }

    /**
     * Method disposeAllComponents.
     */
    protected void disposeAllComponents()
    {
        // Use an array to get the list of componentManagers else we'll
        // end up with a ConcurrentModificationException if we use an
        // Iterator to cycle through the set because release() makes
        // changes to the set as well.
        //<== now not important as each component manager does this.

        for ( Iterator iter = getComponentManagers().values().iterator(); iter.hasNext(); )
        {
            try
            {
                ( (ComponentManager) iter.next() ).dispose();
            }
            catch ( Exception e )
            {
                getLogger().error(
                    "Error while disposing component manager. Continuing with the rest",
                    e );
            }
        }

        componentManagers.clear();

        getLogger().info( "ComponentRepository disposed." );
    }
}
