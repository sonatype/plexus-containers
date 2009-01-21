package org.codehaus.plexus;

import static com.google.common.base.ReferenceType.WEAK;
import static com.google.common.collect.Maps.newConcurrentHashMap;
import com.google.common.collect.ReferenceMap;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.ComponentIndex;
import static org.codehaus.plexus.component.CastUtils.cast;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.manager.StaticComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.NullLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

public class DefaultComponentRegistry implements ComponentRegistry
{
    private static final String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    private final MutablePlexusContainer container;
    private final LifecycleHandlerManager lifecycleHandlerManager;
    private final Logger logger;

    private final ConcurrentMap<String, ComponentManagerFactory> componentManagerFactories = newConcurrentHashMap();

    private final ComponentIndex<ComponentManager<?>> index = new ComponentIndex<ComponentManager<?>>();
    private final Map<Object, ComponentManager<?>> componentManagersByComponent =
        new ReferenceMap<Object, ComponentManager<?>>( WEAK, WEAK);

    public DefaultComponentRegistry( MutablePlexusContainer container, LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.container = container;
        this.lifecycleHandlerManager = lifecycleHandlerManager;

        Logger containerLogger = container.getLogger();
        if ( containerLogger != null )
        {
            logger = containerLogger;
        }
        else
        {
            logger = new NullLogger();
        }
    }

    public void dispose()
    {
        Collection<ComponentManager<?>> managers = index.clear();
        componentManagersByComponent.clear();

        // Call dispose callback outside of synchronized lock to avoid deadlocks
        for ( ComponentManager<?> componentManager : managers )
        {
            try
            {
                componentManager.dispose();
            }
            catch ( Exception e )
            {
                // todo dain use a monitor instead of a logger
                logger.error( "Error while disposing component manager. Continuing with the rest", e );
            }
        }
    }

    //
    // Component Manager Factories
    //

    public void registerComponentManagerFactory( ComponentManagerFactory componentManagerFactory )
    {
        componentManagerFactories.put( componentManagerFactory.getId(), componentManagerFactory );
    }

    //
    // Component Descriptors
    //

    public void addComponentDescriptor( ComponentDescriptor<?> componentDescriptor ) throws ComponentRepositoryException
    {
        // verify the descriptor matches the role hint and type
        verifyComponentDescriptor( componentDescriptor );

        // Get the ComponentManagerFactory
        String instantiationStrategy = componentDescriptor.getInstantiationStrategy();
        if ( instantiationStrategy == null )
        {
            instantiationStrategy = DEFAULT_INSTANTIATION_STRATEGY;
        }
        ComponentManagerFactory componentManagerFactory = componentManagerFactories.get( instantiationStrategy );
        if ( componentManagerFactory == null )
        {
            throw new ComponentRepositoryException( "Unsupported instantiation strategy: " + instantiationStrategy,
                componentDescriptor );
        }

        // Get the LifecycleHandler
        LifecycleHandler lifecycleHandler;
        try
        {
            lifecycleHandler = lifecycleHandlerManager.getLifecycleHandler( componentDescriptor.getLifecycleHandler() );
        }
        catch ( UndefinedLifecycleHandlerException e )
        {
            throw new ComponentRepositoryException( "Undefined lifecycle handler: " + componentDescriptor.getLifecycleHandler(),
                componentDescriptor );
        }

        // Create the ComponentManager
        ComponentManager<?> componentManager = componentManagerFactory.createComponentManager( container,
            lifecycleHandler,
            componentDescriptor );

        // Add componentManager to indexe
        index.add( componentDescriptor.getRealm(),
            componentDescriptor.getImplementationClass(),
            componentDescriptor.getRoleHint(),
            componentManager);

    }

    public <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String roleHint )
    {
        ComponentManager<T> componentManager = (ComponentManager<T>) index.get( type, roleHint );
        if ( componentManager == null )
        {
            return null;
        }
        return componentManager.getComponentDescriptor();
    }

    public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( Class<T> type )
    {
        Map<String, ComponentManager<T>> componentManagers = cast( index.getAllAsMap( type ) );

        Map<String, ComponentDescriptor<T>> descriptors = new LinkedHashMap<String, ComponentDescriptor<T>>(componentManagers.size());
        for ( Entry<String, ComponentManager<T>> entry : componentManagers.entrySet() )
        {
            descriptors.put(entry.getKey(), entry.getValue().getComponentDescriptor());
        }

        return Collections.unmodifiableMap( descriptors );
    }

    public <T> List<ComponentDescriptor<T>> getComponentDescriptorList( Class<T> type )
    {
        List<ComponentManager<T>> componentManagers = cast( index.getAll( type ) );

        List<ComponentDescriptor<T>> descriptors = new ArrayList<ComponentDescriptor<T>>(componentManagers.size());
        for ( ComponentManager<T> componentManager : componentManagers )
        {
            descriptors.add( componentManager.getComponentDescriptor() );
        }

        return Collections.unmodifiableList( descriptors );
    }

    //
    // Component Instances
    //

    public <T> void addComponent( T instance, Class<?> type, String roleHint, ClassRealm realm ) throws ComponentRepositoryException
    {
        StaticComponentManager<T> componentManager = new StaticComponentManager<T>( container, instance, type, roleHint, realm );

        // verify descriptor is consistent
        ComponentDescriptor<T> descriptor = componentManager.getComponentDescriptor();
        verifyComponentDescriptor( descriptor );

        index.add(descriptor.getRealm(), descriptor.getImplementationClass(), descriptor.getRoleHint(), componentManager);
    }

    public <T> T lookup( Class<T> type, String roleHint ) throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( roleHint == null )
        {
            roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
        }

        return getComponent( type, roleHint );
    }

    public <T> Map<String, T> lookupMap( Class<T> type, List<String> roleHints )
        throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }

        // if no hints provided, get all valid hints for this role
        Map<String, T> components = new LinkedHashMap<String, T>();
        if ( roleHints == null )
        {
            Map<String, ComponentManager<T>> componentManagers = cast( index.getAllAsMap( type ) );

            for ( Entry<String, ComponentManager<T>> entry : componentManagers.entrySet() )
            {
                String roleHint = entry.getKey();
                ComponentManager<T> componentManager = entry.getValue();

                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( componentManager );
                components.put( roleHint, component);
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, roleHint );
                components.put( roleHint, component );
            }
        }

        return components;
    }

    public <T> List<T> lookupList( Class<T> type, List<String> roleHints ) throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }

        // if no hints provided, get all valid hints for this role
        List<T> components = new ArrayList<T>();
        if ( roleHints == null )
        {
            List<ComponentManager<T>> componentManagers = cast( index.getAll( type ) );
            for ( ComponentManager<T> componentManager : componentManagers )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( componentManager );
                components.add( component);
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, roleHint );
                components.add( component );
            }
        }

        return components;
    }

    public void release( Object component ) throws ComponentLifecycleException
    {
        if ( component == null )
        {
            return;
        }

        // get the component manager
        ComponentManager<?> componentManager = componentManagersByComponent.get( component );

        if ( componentManager == null )
        {
            // This needs to be tracked down but the user doesn't need to see this
            // during the maven bootstrap this logger is null.
            //logger.debug( "Component manager not found for returned component. Ignored. component=" + component );
            return;
        }

        // release the component from the manager
        componentManager.release( component );
    }

    public void removeComponentRealm( ClassRealm classRealm ) throws PlexusContainerException
    {
        LinkedHashSet<ComponentManager<?>> dispose;
        try
        {
            // remove all component managers associated with the realm
            dispose = new LinkedHashSet<ComponentManager<?>>(index.removeAll( classRealm ));

            // Call dispose callback outside of synchronized lock to avoid deadlocks
            for ( ComponentManager<?> componentManager : dispose )
            {
                componentManager.dispose();
            }
        }
        catch ( ComponentLifecycleException e )
        {
            throw new PlexusContainerException( "Failed to dissociate component realm: " + classRealm.getId(), e );
        }
    }

    private <T> T getComponent( Class<T> type, String roleHint ) throws ComponentLookupException
    {
        ComponentManager<T> componentManager = (ComponentManager<T>) index.get( type, roleHint );
        if ( componentManager == null )
        {
            throw new ComponentLookupException( "Component descriptor cannot be found", type, roleHint );
        }
        return getComponent( componentManager );
    }

    private <T> T getComponent( ComponentManager<T> componentManager ) throws ComponentLookupException
    {
        // Get instance from manager... may result in creation
        try
        {
            T component = componentManager.getComponent();

            componentManagersByComponent.put( component, componentManager );

            return component;
        }
        catch ( ComponentInstantiationException e )
        {
            throw new ComponentLookupException( "Component could not be created", componentManager.getComponentDescriptor(), e );
        }
        catch ( ComponentLifecycleException e )
        {
            throw new ComponentLookupException( "Component could not be started", componentManager.getComponentDescriptor(), e );
        }
    }

    private <T> void verifyComponentDescriptor( ComponentDescriptor<T> descriptor ) throws ComponentRepositoryException
    {
        ClassLoader classLoader = descriptor.getRealm();
        if ( classLoader == null)
        {
            throw new ComponentRepositoryException( "ComponentDescriptor realm is null", descriptor);
        }

        Class<?> implementationClass = descriptor.getImplementationClass();
        if (implementationClass.equals( Object.class ))
        {
            throw new ComponentRepositoryException( "ComponentDescriptor implementation class could not be loaded", descriptor);
        }

        String role = descriptor.getRole();
        if (role == null)
        {
            throw new ComponentRepositoryException( "ComponentDescriptor role is null", descriptor);
        }

        Class<?> roleClass;
        try
        {
            roleClass = classLoader.loadClass( role );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ComponentRepositoryException( "ComponentDescriptor role class can not be loaded", descriptor);
        }

        if (!roleClass.isAssignableFrom( implementationClass ))
        {
            throw new ComponentRepositoryException( "ComponentDescriptor implementation class does not implement the role class:" +
                " implementationClass=" + implementationClass.getName() + " roleClass=" + roleClass.getName(),
                descriptor);
        }
    }
}
