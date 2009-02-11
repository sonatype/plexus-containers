package org.codehaus.plexus;

import static com.google.common.base.ReferenceType.WEAK;
import static com.google.common.collect.Iterables.concat;
import com.google.common.collect.ListMultimap;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newConcurrentHashMap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ReferenceMap;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import static org.codehaus.plexus.component.CastUtils.cast;
import org.codehaus.plexus.component.ComponentIndex;
import static org.codehaus.plexus.component.ComponentStack.pushComponentStack;
import static org.codehaus.plexus.component.ComponentStack.popComponentStack;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.manager.StaticComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentDescriptorListener;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.NullLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultComponentRegistry implements ComponentRegistry
{
    private static final String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    private final MutablePlexusContainer container;
    private final LifecycleHandlerManager lifecycleHandlerManager;
    private final Logger logger;

    private final AtomicBoolean disposed = new AtomicBoolean( false );

    private final ConcurrentMap<String, ComponentManagerFactory> componentManagerFactories = newConcurrentHashMap();

    private final ComponentIndex<ComponentManager<?>> index = new ComponentIndex<ComponentManager<?>>();
    private final Map<ComponentDescriptor<?>, ComponentManager<?>> componentManagersByComponentDescriptor =
        new ReferenceMap<ComponentDescriptor<?>, ComponentManager<?>>( WEAK, WEAK);

    private final Map<Object, ComponentManager<?>> componentManagersByComponent =
        new ReferenceMap<Object, ComponentManager<?>>( WEAK, WEAK);

    private final ListMultimap<Pair<Class<?>, String>, ComponentDescriptorListener<?>> listeners = Multimaps.newArrayListMultimap();

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
        if (disposed.getAndSet( true )) {
            // already disposed
            return;
        }

        List<ComponentManager<?>> managers;
        synchronized ( index )
        {
            managers = new ArrayList<ComponentManager<?>>( index.clear() );
            componentManagersByComponentDescriptor.clear();
            componentManagersByComponent.clear();

        }

        // reverse sort the managers by startId
        Collections.sort( managers, new Comparator<ComponentManager<?>>() {
            public int compare( ComponentManager<?> left, ComponentManager<?> right )
            {
                if (left.getStartId() < right.getStartId() )
                {
                    return 1;
                }
                else if (left.getStartId() == right.getStartId() )
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
        });

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
            fireComponentDescriptorRemoved( componentManager.getComponentDescriptor() );
        }
    }

    //
    // Component Manager Factories
    //

    public void registerComponentManagerFactory( ComponentManagerFactory componentManagerFactory )
    {
        if ( disposed.get() )
        {
            throw new IllegalStateException("ComponentRegistry has been disposed");
        }

        componentManagerFactories.put( componentManagerFactory.getId(), componentManagerFactory );
    }

    //
    // Component Descriptors
    //

    public <T> void addComponentDescriptor( ComponentDescriptor<T> componentDescriptor ) throws ComponentRepositoryException
    {
        if ( disposed.get() )
        {
            throw new ComponentRepositoryException("ComponentRegistry has been disposed", componentDescriptor);
        }

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
        ComponentManager<T> componentManager = componentManagerFactory.createComponentManager( container,
            lifecycleHandler,
            componentDescriptor );

        // Add componentManager to indexe
        synchronized ( index )
        {
            index.add( componentDescriptor.getRealm(),
                componentDescriptor.getRoleClass(),
                componentDescriptor.getRoleHint(),
                componentManager);
            componentManagersByComponentDescriptor.put(componentDescriptor, componentManager);
        }

        fireComponentDescriptorAdded( componentDescriptor );
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
        if ( disposed.get() )
        {
            throw new ComponentRepositoryException("ComponentRegistry has been disposed", type, roleHint, realm);
        }

        StaticComponentManager<T> componentManager = new StaticComponentManager<T>( container, instance, type, roleHint, realm );

        // verify descriptor is consistent
        ComponentDescriptor<T> descriptor = componentManager.getComponentDescriptor();
        verifyComponentDescriptor( descriptor );

        synchronized ( index )
        {
            index.add( descriptor.getRealm(), descriptor.getRoleClass(), descriptor.getRoleHint(), componentManager);
            componentManagersByComponentDescriptor.put( descriptor, componentManager);
        }

        fireComponentDescriptorAdded( descriptor );
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

    public <T> T lookup( ComponentDescriptor<T> componentDescriptor ) throws ComponentLookupException
    {
        ComponentManager<T> componentManager = (ComponentManager<T>) componentManagersByComponentDescriptor.get( componentDescriptor );
        if ( componentManager == null )
        {
            throw new ComponentLookupException( "Component descriptor is not registered with PlexusContainer", componentDescriptor );
        }
        return getComponent( componentManager );
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

    public <T> void addComponentDescriptorListener( ComponentDescriptorListener<T> listener )
    {
        Class<T> type = listener.getType();
        List<String> roleHints = listener.getRoleHints();
        synchronized ( this )
        {
            if (roleHints == null )
            {
                listeners.put(new Pair<Class<?>, String>(type, null), listener);
            }
            else
            {
                for ( String roleHint : roleHints )
                {
                    listeners.put(new Pair<Class<?>, String>(type, roleHint), listener);
                }
            }
        }

        // if no hints provided, get all valid hints for this role
        if ( roleHints == null )
        {
            List<ComponentManager<T>> componentManagers = cast( index.getAll( type ) );
            for ( ComponentManager<T> componentManager : componentManagers )
            {
                listener.componentDescriptorAdded( componentManager.getComponentDescriptor() );
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                ComponentManager<T> componentManager = (ComponentManager<T>) index.get( type, roleHint );
                if (componentManager != null) {
                    listener.componentDescriptorAdded( componentManager.getComponentDescriptor() );
                }
            }
        }
    }

    public synchronized <T> void removeComponentDescriptorListener( ComponentDescriptorListener<T> listener )
    {
        Class<?> type = listener.getType();
        List<String> roleHints = listener.getRoleHints();
        if (roleHints == null || roleHints.isEmpty())
        {
            listeners.remove(new Pair<Class<?>, String>(type, null), listener);
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                listeners.remove(new Pair<Class<?>, String>(type, roleHint), listener);
            }
        }
    }

    private synchronized <T> List<ComponentDescriptorListener<T>> getListeners(ComponentDescriptor<T> descriptor)
    {
        List<ComponentDescriptorListener<T>> allHintListeners =
            cast( listeners.get( new Pair<Class<?>, String>( descriptor.getRoleClass(), null ) ) );
        List<ComponentDescriptorListener<T>> specificHintListeners =
            cast( listeners.get( new Pair<Class<?>, String>( descriptor.getRoleClass(), descriptor.getRoleHint() ) ) );

        return newArrayList( concat(allHintListeners, specificHintListeners) );
    }

    private <T> void fireComponentDescriptorAdded( ComponentDescriptor<T> componentDescriptor )
    {
        for ( ComponentDescriptorListener<T> listener : getListeners( componentDescriptor ))
        {
            try
            {
                listener.componentDescriptorAdded( componentDescriptor );
            }
            catch ( Throwable e )
            {
                logger.debug( "ComponentDescriptorListener threw exception while processing " + componentDescriptor, e );
            }
        }
    }

    private <T> void fireComponentDescriptorRemoved( ComponentDescriptor<T> componentDescriptor )
    {
        for ( ComponentDescriptorListener<T> listener : getListeners( componentDescriptor ))
        {
            try
            {
                listener.componentDescriptorRemoved( componentDescriptor );
            }
            catch ( Throwable e )
            {
                logger.error( "ComponentDescriptorListener threw exception while processing " + componentDescriptor, e );
            }
        }
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
        try
        {
            // remove all component managers associated with the realm
            LinkedHashSet<ComponentManager<?>> dispose;
            synchronized ( index )
            {
                dispose = new LinkedHashSet<ComponentManager<?>>(index.removeAll( classRealm ));
                for ( ComponentManager<?> componentManager : dispose )
                {
                    ComponentDescriptor<?> descriptor = componentManager.getComponentDescriptor();
                    componentManagersByComponentDescriptor.remove( descriptor );
                    fireComponentDescriptorRemoved( descriptor );
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
        ComponentDescriptor<T> descriptor = componentManager.getComponentDescriptor();

        // Get instance from manager... may result in creation
        pushComponentStack( descriptor );
        try
        {
            T component = componentManager.getComponent();

            componentManagersByComponent.put( component, componentManager );

            return component;
        }
        catch ( Exception e )
        {
            // get real cause
            Throwable cause = e.getCause();
            if ( cause == null )
            {
                cause = e;
            }

            // do not rewrap ComponentLookupException
            if (cause instanceof ComponentLookupException )
            {
                throw (ComponentLookupException) cause;
            }

            throw new ComponentLookupException( e.getMessage(), descriptor, cause );
        }
        finally
        {
            popComponentStack();
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

    public static class Pair<L,R> {
        private final L left;
        private final R right;

        public Pair( L left, R right )
        {
            this.left = left;
            this.right = right;
        }

        public L getLeft()
        {
            return left;
        }

        public R getRight()
        {
            return right;
        }

        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( !( o instanceof Pair ) )
            {
                return false;
            }

            Pair<?,?> pair = (Pair<?,?>) o;

            return
                ( left == null ? pair.left == null : left.equals( pair.left ) ) && 
                ( right == null ? pair.right == null : right.equals( pair.right ) );

        }

        public int hashCode()
        {
            int result;
            result = ( left != null ? left.hashCode() : 0 );
            result = 31 * result + ( right != null ? right.hashCode() : 0 );
            return result;
        }

        public String toString()
        {
            StringBuilder buf = new StringBuilder( );
            buf.append("[").append(left).append(", ").append(right).append("]");
            return buf.toString();
        }
    }
}
