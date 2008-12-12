package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.ComponentIndex;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DefaultComponentRegistry implements ComponentRegistry
{
    private static final String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    private final MutablePlexusContainer container;
    private final ComponentRepository repository;
    private final LifecycleHandlerManager lifecycleHandlerManager;
    private final Logger logger;

    private final Map<String, ComponentManagerFactory> componentManagerFactories =
        Collections.synchronizedMap( new TreeMap<String, ComponentManagerFactory>() );

    private final ComponentIndex<ComponentManager<?>> index = new ComponentIndex<ComponentManager<?>>();
    private final Map<Object, ComponentManager<?>> componentManagersByComponent = new IdentityHashMap<Object, ComponentManager<?>>();

    public DefaultComponentRegistry( MutablePlexusContainer container,
                                     ComponentRepository repository,
                                     LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.container = container;
        this.repository = repository;
        this.lifecycleHandlerManager = lifecycleHandlerManager;
        logger = container.getLogger();
    }

    public void dispose()
    {
        Collection<ComponentManager<?>> managers;
        synchronized ( this )
        {
            managers = index.getAll();
            index.clear();
            componentManagersByComponent.clear();
        }

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

    public void registerComponentManagerFactory( ComponentManagerFactory componentManagerFactory )
    {
        componentManagerFactories.put( componentManagerFactory.getId(), componentManagerFactory );
    }

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
        ComponentManager<?> componentManager;
        synchronized ( this )
        {
            componentManager = componentManagersByComponent.get( component );
            if ( componentManager == null )
            {
                // This needs to be tracked down but the user doesn't need to see this
                // during the maven bootstrap this logger is null.
                //logger.debug( "Component manager not found for returned component. Ignored. component=" + component );
                return;
            }
        }

        // release the component from the manager
        componentManager.release( component );

        // only drop the reference to this component if there are no other uses of the component
        // multiple uses of a component is common with singleton beans
        if ( componentManager.getConnections() <= 0 )
        {
            synchronized ( this )
            {
                componentManagersByComponent.remove( component );
            }
        }
    }

    public void removeComponentRealm( ClassRealm classRealm ) throws PlexusContainerException
    {
        repository.removeComponentRealm( classRealm );

        LinkedHashSet<ComponentManager<?>> dispose;
        try
        {
            synchronized ( this )
            {
                // remove all component managers associated with the realm
                dispose = new LinkedHashSet<ComponentManager<?>>(index.removeAll( classRealm ));

                // disassociate realm from all remaining component managers
                for ( ComponentManager<?> componentManager : index.getAll() )
                {
                    componentManager.dissociateComponentRealm( classRealm );
                }
            }

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
            synchronized ( this )
            {
                componentManagersByComponent.put( component, componentManager );
            }
            return component;
        }
        catch ( ComponentInstantiationException e )
        {
            throw new ComponentLookupException(
                "Unable to lookup component '" + componentManager.getRole() + "', it could not be created.",
                componentManager.getRole(), componentManager.getRoleHint(), componentManager.getRealm(), e );
        }
        catch ( ComponentLifecycleException e )
        {
            throw new ComponentLookupException(
                "Unable to lookup component '" + componentManager.getRole() + "', it could not be started.",
                componentManager.getRole(), componentManager.getRoleHint(), componentManager.getRealm(), e );
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

            componentManager = createComponentManager( descriptor, type, roleHint );
        }
        return componentManager;
    }

    private <T> ComponentManager<T> createComponentManager( ComponentDescriptor<T> descriptor, Class<T> type, String roleHint )
        throws ComponentLookupException
    {
        verifyComponentDescriptor( descriptor );

        // Get the ComponentManagerFactory
        String instantiationStrategy = descriptor.getInstantiationStrategy();
        if ( instantiationStrategy == null )
        {
            instantiationStrategy = DEFAULT_INSTANTIATION_STRATEGY;
        }
        ComponentManagerFactory componentManagerFactory = componentManagerFactories.get( instantiationStrategy );
        if ( componentManagerFactory == null )
        {
            throw new ComponentLookupException( "Unsupported instantiation strategy: " + instantiationStrategy,
                type,
                roleHint,
                descriptor.getRealm() );
        }

        // Get the LifecycleHandler
        LifecycleHandler lifecycleHandler;
        try
        {
            lifecycleHandler = lifecycleHandlerManager.getLifecycleHandler( descriptor.getLifecycleHandler() );
        }
        catch ( UndefinedLifecycleHandlerException e )
        {
            throw new ComponentLookupException( "Undefined lifecycle handler: " + descriptor.getLifecycleHandler(),
                type,
                roleHint,
                descriptor.getRealm() );
        }

        // Create the ComponentManager
        ComponentManager<T> componentManager = componentManagerFactory.createComponentManager( container,
            lifecycleHandler,
            descriptor,
            descriptor.getRole(),
            roleHint );

        // Add componentManager to indexe
        index.add(descriptor.getRealm(), type, roleHint, componentManager);

        return componentManager;
    }

    private void verifyComponentDescriptor( ComponentDescriptor<?> componentDescriptor )
        throws ComponentLookupException
    {
        String role = componentDescriptor.getRole();
        String roleHint = componentDescriptor.getRoleHint();
        ClassRealm realm = componentDescriptor.getRealm();

        if (realm == null)
        {
            throw new ComponentLookupException( "ComponentDescriptor realm is null", role, roleHint, realm);
        }

        Class<?> implementationClass = componentDescriptor.getImplementationClass();
        if (implementationClass.equals( Object.class ))
        {
            throw new ComponentLookupException( "ComponentDescriptor implementation class could not be loaded", role, roleHint, realm);
        }

        if (role == null)
        {
            throw new ComponentLookupException( "ComponentDescriptor role is null", role, roleHint, realm);
        }

        Class<?> roleClass;
        try
        {
            roleClass = realm.loadClass( role );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ComponentLookupException( "ComponentDescriptor role is not a class", role, roleHint, realm);
        }

        if (!roleClass.isAssignableFrom( implementationClass )) {
            throw new ComponentLookupException( "ComponentDescriptor implementation class does not implement the role class: implementationClass=" + implementationClass, role, roleHint, realm);
        }
    }
}
