package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.DefaultComponentManagerManager;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xstream.ObjectBuilder;
import org.codehaus.plexus.lifecycle.DefaultLifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.LoggerManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @todo remove explicit avalon dependencies from here.
 * @todo remove lifecycle handling from the repository.
 * @todo remove instance manager handling from the repository.
 * @todo remove plexus container reference.
 */
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

    /**
     * Object to lock when creating a new component manager during
     * component lookup. Separate from enclosing class as we have no control
     * on what locks calling code places.
     */
    private Object lookupLock = new Object();

    private LifecycleHandlerManager lifecycleHandlerManager = null;

    private ComponentManagerManager componentManagerManager = null;

    /** Constructor. */
    public DefaultComponentRepository()
    {
        componentDescriptors = new HashMap();

        componentManagers = Collections.synchronizedMap( new HashMap() );

        compManagersByCompClass = Collections.synchronizedMap( new HashMap() );
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public PlexusContainer getPlexusContainer()
    {
        return plexusContainer;
    }

    public void setPlexusContainer( PlexusContainer plexusContainer )
    {
        this.plexusContainer = plexusContainer;
    }

    public ClassLoader getClassLoader()
    {
        return getPlexusContainer().getClassLoader();
    }

    public void setComponentLogManager( LoggerManager manager )
    {
        loggerManager = manager;
    }

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

    Map getComponentDescriptors()
    {
        return componentDescriptors;
    }

    Map getComponentManagers()
    {
        return componentManagers;
    }

    ComponentManager getComponentManager( String componentKey )
    {
        return (ComponentManager) getComponentManagers().get( componentKey );
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
     * @see ComponentRepository#contextualize(org.apache.avalon.framework.context.Context)
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
        initializeLifecycleHandlerManager();

        initializeComponentManagerManager();

        initializeComponentDescriptors();
    }

    /**
     * Adds all the lifecycle handlers and initializes them. Sets up the default lifecycle handler
     */
    private void initializeLifecycleHandlerManager()
        throws Exception
    {
        ObjectBuilder builder = new ObjectBuilder();

        builder.alias( "lifecycle-handler-manager", DefaultLifecycleHandlerManager.class );

        Configuration c = getConfiguration().getChild( "lifecycle-handler-manager" );

        lifecycleHandlerManager = (LifecycleHandlerManager) builder.build( (PlexusConfiguration) c, DefaultLifecycleHandlerManager.class );

        lifecycleHandlerManager.initialize( loggerManager, context, this );
    }

    /**
     * Grab all the InstanceManager configurations and make them available
     * during lookup
     *
     * @throws Exception
     */
    private void initializeComponentManagerManager()
        throws Exception
    {
        ObjectBuilder builder = new ObjectBuilder();

        builder.alias( "component-manager-manager", DefaultComponentManagerManager.class );

        Configuration c = getConfiguration().getChild( "component-manager-manager" );

        componentManagerManager = (ComponentManagerManager) builder.build( (PlexusConfiguration) c, DefaultComponentManagerManager.class );
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
            addComponentDescriptor( buildComponentDescriptor( componentConfigurations[i] ) );
        }
    }

    protected ComponentDescriptor buildComponentDescriptor( Configuration configuration )
        throws Exception
    {
        ObjectBuilder objectBuilder = new ObjectBuilder();

        objectBuilder.alias( "component", ComponentDescriptor.class );

        objectBuilder.alias( "requirement", String.class );

        ComponentDescriptor cd =
            (ComponentDescriptor) objectBuilder.build(  (PlexusConfiguration) configuration, ComponentDescriptor.class );

        return cd;
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
                        throw new ServiceException( key, "Error retrieving component from ComponentManager" );
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

                    throw new ServiceException( key, "Could not create component for key " + key + "!", e );
                }
                try
                {
                    component = componentManager.getComponent();
                }
                catch ( Exception e )
                {
                    getLogger().error( "Could not create component: " + key, e );

                    throw new ServiceException( key, "Could not create component for key " + key + "!", e );
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

    public synchronized void suspend( Object component )
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = findComponentManager( component );

        componentManager.suspend( component );
    }

    public synchronized void resume( Object component )
    {
        if ( component == null )
        {
            return;
        }

        ComponentManager componentManager = findComponentManager( component );

        componentManager.resume( component );
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

        ComponentManager componentManager = findComponentManager( component );

        componentManager.release( component );
    }

    protected ComponentManager findComponentManager( Object component )
    {
        return (ComponentManager) compManagersByCompClass.get( component.getClass().getName() );
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
                getLogger().error( "Error while disposing component manager. Continuing with the rest", e );
            }
        }

        componentManagers.clear();

        getLogger().info( "ComponentRepository disposed." );
    }
}
