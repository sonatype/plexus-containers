package org.codehaus.plexus;

import static com.google.common.base.ReferenceType.WEAK;
import static com.google.common.collect.Maps.newConcurrentHashMap;
import com.google.common.collect.ReferenceMap;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.ComponentIndex;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.manager.StaticComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

public class DefaultComponentRegistry implements ComponentRegistry
{
    private static final String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    private final MutablePlexusContainer container;
    private final ComponentRepository repository;
    private final LifecycleHandlerManager lifecycleHandlerManager;
    private final Logger logger;

    private final ConcurrentMap<String, ComponentManagerFactory> componentManagerFactories = newConcurrentHashMap();

    private final ComponentIndex<ComponentManager<?>> index = new ComponentIndex<ComponentManager<?>>();
    private final Map<Object, ComponentManager<?>> componentManagersByComponent =
        new ReferenceMap<Object, ComponentManager<?>>( WEAK, WEAK);

    public DefaultComponentRegistry( MutablePlexusContainer container,
                                     ComponentRepository repository,
                                     LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.container = container;
        this.repository = repository;
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
        repository.addComponentDescriptor( componentDescriptor );
    }

    public <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String roleHint )
    {
        return repository.getComponentDescriptor( type, roleHint );
    }

    public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( Class<T> type )
    {
        return repository.getComponentDescriptorMap( type );
    }

    public <T> List<ComponentDescriptor<T>> getComponentDescriptorList( Class<T> type )
    {
        return repository.getComponentDescriptorList( type );
    }

    //
    // Component Instances
    //

    public <T> void addComponent( T instance, String role, String roleHint ) throws ComponentRepositoryException
    {
        StaticComponentManager<T> componentManager = new StaticComponentManager<T>( container, instance, role, roleHint );

        ComponentDescriptor<T> descriptor = componentManager.getComponentDescriptor();
        addComponentDescriptor( descriptor );

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

        return getComponent( type, roleHint, null );
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
            Map<String, ComponentDescriptor<T>> componentDescriptors = getComponentDescriptorMap( type );
            for ( Entry<String, ComponentDescriptor<T>> entry : componentDescriptors.entrySet() )
            {
                String roleHint = entry.getKey();
                ComponentDescriptor<T> componentDescriptor = entry.getValue();
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, roleHint, componentDescriptor );
                components.put( roleHint, component );
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, roleHint, null );
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
            List<ComponentDescriptor<T>> componentDescriptors = getComponentDescriptorList( type );
            for ( ComponentDescriptor<T> componentDescriptor : componentDescriptors )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, componentDescriptor.getRoleHint(), componentDescriptor );
                components.add( component );
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, roleHint, null );
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
        repository.removeComponentRealm( classRealm );

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

    private <T> T getComponent( Class<T> type, String roleHint, ComponentDescriptor<T> descriptor )
        throws ComponentLookupException
    {
        ComponentManager<T> componentManager = getComponentManager( type, roleHint, descriptor );

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

    private synchronized <T> ComponentManager<T> getComponentManager( Class<T> type, String roleHint, ComponentDescriptor<T> descriptor ) throws ComponentLookupException
    {
        ComponentManager<T> componentManager = (ComponentManager<T>) index.get( type, roleHint );

        // if component manager is not found, create one
        if ( componentManager == null )
        {
            // we need to create a component manager, but first we must have a descriptor
            if ( descriptor == null )
            {
                descriptor = getComponentDescriptor( type, roleHint );
                if ( descriptor == null )
                {
                    throw new ComponentLookupException(
                        "Component descriptor cannot be found in the component repository",
                        type,
                        roleHint );
                }
            }

            // verify the found descriptor matches the role hint and type
            verifyComponentDescriptor( type, roleHint, descriptor );

            componentManager = createComponentManager( descriptor );
        }
        return componentManager;
    }

    private <T> ComponentManager<T> createComponentManager( ComponentDescriptor<T> descriptor ) throws ComponentLookupException
    {
        // Get the ComponentManagerFactory
        String instantiationStrategy = descriptor.getInstantiationStrategy();
        if ( instantiationStrategy == null )
        {
            instantiationStrategy = DEFAULT_INSTANTIATION_STRATEGY;
        }
        ComponentManagerFactory componentManagerFactory = componentManagerFactories.get( instantiationStrategy );
        if ( componentManagerFactory == null )
        {
            throw new ComponentLookupException( "Unsupported instantiation strategy: " + instantiationStrategy, descriptor );
        }

        // Get the LifecycleHandler
        LifecycleHandler lifecycleHandler;
        try
        {
            lifecycleHandler = lifecycleHandlerManager.getLifecycleHandler( descriptor.getLifecycleHandler() );
        }
        catch ( UndefinedLifecycleHandlerException e )
        {
            throw new ComponentLookupException( "Undefined lifecycle handler: " + descriptor.getLifecycleHandler(), descriptor );
        }

        // Create the ComponentManager
        ComponentManager<T> componentManager = componentManagerFactory.createComponentManager( container,
            lifecycleHandler,
            descriptor );

        // Add componentManager to indexe
        index.add(descriptor.getRealm(), descriptor.getImplementationClass(), descriptor.getRoleHint(), componentManager);

        return componentManager;
    }

    private <T> void verifyComponentDescriptor( Class<T> lookupClass, String roleHint, ComponentDescriptor<T> descriptor )
        throws ComponentLookupException
    {
        String role = descriptor.getRole();
        ClassLoader classLoader = descriptor.getRealm();

        if ( !roleHint.equals( descriptor.getRoleHint() ) )
        {
            throw new ComponentLookupException( "Expected component descriptor to have roleHint " + roleHint + ", but it was " + descriptor.getRoleHint(), descriptor );
        }

        if ( classLoader == null)
        {
            throw new ComponentLookupException( "ComponentDescriptor realm is null", descriptor);
        }

        Class<?> implementationClass = descriptor.getImplementationClass();
        if (implementationClass.equals( Object.class ))
        {
            throw new ComponentLookupException( "ComponentDescriptor implementation class could not be loaded", descriptor);
        }

        if (role == null)
        {
            throw new ComponentLookupException( "ComponentDescriptor role is null", descriptor);
        }

        Class<?> roleClass;
        try
        {
            roleClass = classLoader.loadClass( role );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ComponentLookupException( "ComponentDescriptor role is not a class", descriptor);
        }

        if (!roleClass.isAssignableFrom( implementationClass ))
        {
            throw new ComponentLookupException( "ComponentDescriptor implementation class does not implement the role class:" +
                " implementationClass=" + implementationClass.getName() + " roleClass=" + roleClass.getName(),
                descriptor);
        }

        if (!lookupClass.isAssignableFrom( implementationClass ))
        {
            throw new ComponentLookupException( "ComponentDescriptor implementation class does not implement the lookup class:" +
                " implementationClass=" + implementationClass.getName() + " lookupClass=" + lookupClass.getName(),
                descriptor);
        }
    }
}
