package org.codehaus.plexus.component.repository;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTools;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.manager.DefaultComponentManagerManager;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xstream.XStreamTool;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.lifecycle.DefaultLifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.LoggerManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
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

    private Configuration configuration;

    private Map componentDescriptorsKeyedByRole;

    private Map componentManagers;

    private Map componentManagersByComponentClass;

    private PlexusContainer plexusContainer;

    private Context context;

    private LoggerManager loggerManager;

    private LifecycleHandlerManager lifecycleHandlerManager = null;

    private ComponentManagerManager componentManagerManager = null;

    private Map componentsByRole;

    public DefaultComponentRepository()
    {
        componentDescriptorsKeyedByRole = new HashMap();

        componentManagers = new HashMap();

        componentManagersByComponentClass = new HashMap();

        componentsByRole = new HashMap();
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

    public synchronized boolean hasService( String role )
    {
        return getComponentDescriptorsKeyedByRole().containsKey( role );
    }

    public synchronized boolean hasService( String role, String id )
    {
        return getComponentDescriptorsKeyedByRole().containsKey( role + id );
    }

    Map getComponentDescriptorsKeyedByRole()
    {
        return componentDescriptorsKeyedByRole;
    }

    Map getComponentManagers()
    {
        return componentManagers;
    }

    ComponentManager getComponentManager( String componentKey )
    {
        return (ComponentManager) getComponentManagers().get( componentKey );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
    {
        this.context = context;
    }

    public void configure( Configuration configuration )
    {
        this.configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        initializeLifecycleHandlerManager();

        initializeComponentManagerManager();

        initializeComponentDescriptors();
    }

    private void initializeLifecycleHandlerManager()
        throws Exception
    {
        XStreamTool builder = new XStreamTool();

        builder.alias( "lifecycle-handler-manager", DefaultLifecycleHandlerManager.class );

        Configuration c = getConfiguration().getChild( "lifecycle-handler-manager" );

        lifecycleHandlerManager = (LifecycleHandlerManager) builder.build( (PlexusConfiguration) c, DefaultLifecycleHandlerManager.class );

        lifecycleHandlerManager.initialize( loggerManager, context, this );
    }

    private void initializeComponentManagerManager()
        throws Exception
    {
        XStreamTool builder = new XStreamTool();

        builder.alias( "component-manager-manager", DefaultComponentManagerManager.class );

        Configuration c = getConfiguration().getChild( "component-manager-manager" );

        componentManagerManager = (ComponentManagerManager) builder.build( (PlexusConfiguration) c, DefaultComponentManagerManager.class );
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
            addComponentDescriptor( componentConfigurations[i] );
        }
    }

    // ----------------------------------------------------------------------
    //  Component Descriptor processing.
    // ----------------------------------------------------------------------

    public void addComponentDescriptor( Configuration configuration )
        throws ComponentRepositoryException
    {
        ComponentDescriptor componentDescriptor = null;
        try
        {
            componentDescriptor = PlexusTools.buildComponentDescriptor( configuration );
        }
        catch ( Exception e )
        {
            throw new ComponentRepositoryException( "Cannot unmarshall component descriptor:", e );
        }

        addComponentDescriptor( componentDescriptor );
    }

    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException
    {
        try
        {
            validateComponentDescriptor( componentDescriptor );
        }
        catch ( ComponentImplementationNotFoundException e )
        {
            throw new ComponentRepositoryException( "Component descriptor validation failed: ", e );
        }

        String roleHint = componentDescriptor.getRoleHint();

        if ( roleHint != null )
        {
            String role = componentDescriptor.getRole();

            Map map = (Map) componentsByRole.get( role );

            if ( map == null )
            {
                map = new HashMap();

                componentsByRole.put( role, map );
            }

            map.put( roleHint, componentDescriptor );
        }

        getComponentDescriptorsKeyedByRole().put( componentDescriptor.getComponentKey(), componentDescriptor );
    }

    protected void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentImplementationNotFoundException
    {
        // Make sure the component implementation classes can be found.
        // Make sure ComponentManager implementation can be found.
        // Validate lifecycle.
        // Validate the component configuration.
        // Validate the component profile if one is used.
    }

    // ----------------------------------------------------------------------
    // Service lookup methods
    // ----------------------------------------------------------------------

    public Object lookup( String key )
        throws ComponentLookupException
    {
        // Attempt to lookup the componentManager by key.
        ComponentManager componentManager = getComponentManager( key );

        Object component = null;

        if ( componentManager == null )
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
                    throw new ComponentLookupException( "Error retrieving component from ComponentManager: " + key );
                }
            }

            // We need to create an manager of this componentManager.
            getLogger().debug( "Creating new ComponentDescriptor for role: " + key );

            ComponentDescriptor descriptor = (ComponentDescriptor) getComponentDescriptorsKeyedByRole().get( key );

            if ( descriptor == null )
            {
                getLogger().error( "Non existant component: " + key );

                throw new ComponentLookupException( "Non existant component: " + key );
            }

            try
            {
                componentManager = instantiateComponentManager( descriptor );
            }
            catch ( Exception e )
            {
                getLogger().error( "Could not create component: " + key, e );

                throw new ComponentLookupException( "Could not create component for key " + key + "!", e );
            }
            try
            {
                component = componentManager.getComponent();
            }
            catch ( Exception e )
            {
                getLogger().error( "Could not create component: " + key, e );

                throw new ComponentLookupException( "Could not create component for key " + key + "!", e );
            }

            // We do this so we know what to do when releasing. Only have to do it once
            //per component class
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
                throw new ComponentLookupException( "Error retrieving component from ComponentManager" );
            }
        }

        return component;
    }

    public synchronized Map lookupAll( String role )
        throws ComponentLookupException
    {
        Map components = new HashMap();

        Map componentDescriptors = (Map) componentsByRole.get( role );

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

    public synchronized void releaseAll( Map components )
    {
        for ( Iterator i = components.values().iterator(); i.hasNext(); )
        {
            Object component = i.next();

            release( component );
        }
    }

    public synchronized Object lookup( String role, String id )
        throws ComponentLookupException
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
        return (ComponentManager) componentManagersByComponentClass.get( component.getClass().getName() );
    }

    public synchronized void dispose()
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
}
