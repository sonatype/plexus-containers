package org.codehaus.plexus;

import static org.codehaus.plexus.component.CastUtils.isAssignableFrom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

public class DefaultComponentRegistry implements ComponentRegistry
{
    private static final String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    private final MutablePlexusContainer container;
    private final ComponentRepository repository;
    private final LifecycleHandlerManager lifecycleHandlerManager;
    private final Logger logger;

    private boolean disposingComponents; 

    private final Map<String, ComponentManagerFactory> componentManagerFactories =
        Collections.synchronizedMap( new TreeMap<String, ComponentManagerFactory>() );

    private final Map<Key, ComponentManager<?>> componentManagers = new TreeMap<Key, ComponentManager<?>>();
    private final Map<Object, ComponentManager<?>> componentManagersByComponent = new IdentityHashMap<Object, ComponentManager<?>>();

    private final Map<Key, Object> unmanagedComponents = new TreeMap<Key, Object>();

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
        List<ComponentManager<?>> managers;
        synchronized ( this )
        {
            managers = new ArrayList<ComponentManager<?>>( componentManagers.values() );
            componentManagers.clear();
            componentManagersByComponent.clear();
            unmanagedComponents.clear();

            disposingComponents = true;
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
        try
        {
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
        finally 
        {
            synchronized ( this )
            {
                disposingComponents = false;
            }
        }
    }

    public void registerComponentManagerFactory( ComponentManagerFactory componentManagerFactory )
    {
        componentManagerFactories.put( componentManagerFactory.getId(), componentManagerFactory );
    }

    public void addComponentDescriptor( ComponentDescriptor<?> componentDescriptor ) 
        throws CycleDetectedInComponentGraphException
    {
        repository.addComponentDescriptor( componentDescriptor );
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> void addComponent( T component, String role, String roleHint )
    {
        ComponentDescriptor descriptor = new ComponentDescriptor(component.getClass(), null );
        descriptor.setRole( role );
        descriptor.setRoleHint( roleHint );

        Key key = new Key( descriptor.getRealm(), role, roleHint );

        unmanagedComponents.put( key, component );
    }

    public <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String role, String roleHint )
    {
        return repository.getComponentDescriptor( type, role, roleHint );
    }

    @Deprecated
    public ComponentDescriptor<?> getComponentDescriptor( String role, String roleHint, ClassRealm realm )
    {
        return repository.getComponentDescriptor( role, roleHint, realm );
    }

    public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( Class<T> type, String role )
    {
        return repository.getComponentDescriptorMap( type, role );
    }

    public <T> List<ComponentDescriptor<T>> getComponentDescriptorList( Class<T> type, String role )
    {
        return repository.getComponentDescriptorList( type, role );
    }

    public <T> T lookup( Class<T> type, String role, String roleHint ) throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }
        if ( roleHint == null )
        {
            roleHint = "";
        }

        return getComponent( type, role, roleHint, null );
    }

    public <T> T lookup( ComponentDescriptor<T> componentDescriptor )
        throws ComponentLookupException
    {
        return getComponent( componentDescriptor.getRoleClass(), componentDescriptor.getRole(),
                             componentDescriptor.getRoleHint(), componentDescriptor );
    }

    public <T> Map<String, T> lookupMap( Class<T> type, String role, List<String> roleHints )
        throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // if no hints provided, get all valid hints for this role
        Map<String, T> components = new LinkedHashMap<String, T>();
        if ( roleHints == null )
        {
            Map<String, ComponentDescriptor<T>> componentDescriptors = getComponentDescriptorMap( type, role );
            for ( Entry<String, ComponentDescriptor<T>> entry : componentDescriptors.entrySet() )
            {
                String roleHint = entry.getKey();
                ComponentDescriptor<T> componentDescriptor = entry.getValue();
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, roleHint, componentDescriptor );
                components.put( roleHint, component );
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, roleHint, null );
                components.put( roleHint, component );
            }
        }

        return components;
    }

    public <T> List<T> lookupList( Class<T> type, String role, List<String> roleHints ) throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // if no hints provided, get all valid hints for this role
        List<T> components = new ArrayList<T>();
        if ( roleHints == null )
        {
            List<ComponentDescriptor<T>> componentDescriptors = getComponentDescriptorList( type, role );
            for ( ComponentDescriptor<T> componentDescriptor : componentDescriptors )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, componentDescriptor.getRoleHint(), componentDescriptor );
                components.add( component );
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, roleHint, null );
                components.add( component );
            }
        }

        return components;
    }

    public void release( Object component )
        throws ComponentLifecycleException
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
        
        List<ComponentManager<?>> dispose = new ArrayList<ComponentManager<?>>();
        try
        {
            synchronized ( this )
            {
                for ( Iterator<Entry<Key, ComponentManager<?>>> it = componentManagers.entrySet().iterator(); it.hasNext(); )
                {
                    Entry<Key, ComponentManager<?>> entry = it.next();
                    Key key = entry.getKey();

                    ComponentManager<?> componentManager = entry.getValue();

                    if ( key.realm.equals( classRealm ) )
                    {
                        dispose.add( componentManager );
                        it.remove();
                    }
                    else
                    {
                        componentManager.dissociateComponentRealm( classRealm );
                    }
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

    private <T> T getComponent( Class<T> type, String role, String roleHint, ComponentDescriptor<T> descriptor )
        throws ComponentLookupException
    {
        // lookup for unmanaged components first

        T component = this.<T>getUnmanagedComponent( role, roleHint ); // weird syntax due to http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954

        if ( component != null )
        {
            return component;
        }

        ComponentManager<T> componentManager = getComponentManager( type, role, roleHint, descriptor );

        // Get instance from manager... may result in creation
        try
        {
            component = componentManager.getComponent();
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

    @SuppressWarnings("unchecked")
    private synchronized <T> T getUnmanagedComponent( String role, String roleHint )
    {
        Set<ClassRealm> realms = getSearchRealms( true );

        if ( realms != null )
        {
            // ignore unmanaged components, they are not associated with realms 
            // but lookup realm is provided via thread context classloader 
            return null;
        }
        else
        {
            if ( StringUtils.isEmpty( roleHint ) )
            {
                roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
            }

            return (T) unmanagedComponents.get( new Key( null, role, roleHint ) );
        }
    }

    private synchronized <T> ComponentManager<T> getComponentManager( Class<T> type, String role, String roleHint, ComponentDescriptor<T> descriptor )
        throws ComponentLookupException
    {
        if ( disposingComponents )
        {
            throw new ComponentLookupException("ComponentRegistry is not active",
                role,
                roleHint );
        }

        if ( descriptor == null )
        {
            descriptor = getComponentDescriptor( type, role, roleHint );
        }

        ComponentManager<T> componentManager = null;

        if ( descriptor != null )
        {
            componentManager = getComponentManager( type, role, descriptor.getRoleHint(), descriptor.getRealm() );
        }
        else
        {
            componentManager = getComponentManager( type, role, roleHint );
        }

        if ( componentManager == null )
        {
            // we need to create a component manager, but first we must have a descriptor
            if ( descriptor == null )
            {
                descriptor = getComponentDescriptor( type, role, roleHint );
                if ( descriptor == null )
                {
                    throw new ComponentLookupException(
                        "Component descriptor cannot be found in the component repository",
                        role,
                        roleHint );
                }
                // search also into descriptor realm as the key of a created component is per descriptor realm
                componentManager = getComponentManager( type, role, descriptor.getRoleHint(), descriptor.getRealm() );
            }

            if ( componentManager == null )
            {
                componentManager = createComponentManager( descriptor, role, descriptor.getRoleHint() );
            }
        }

        return componentManager;
    }

    @SuppressWarnings( "unchecked" )
    private <T> ComponentManager<T> getComponentManager( Class<T> type, String role, String roleHint )
    {
        Set<ClassRealm> realms = getSearchRealms( false );

        // return the component in the first realm
        for ( ClassRealm realm : realms )
        {
            ComponentManager<?> manager = componentManagers.get( new Key( realm, role, roleHint ) );
            if ( manager != null && isAssignableFrom( type, manager.getType() ) )
            {
                return (ComponentManager<T>) manager;
            }
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    private <T> ComponentManager<T> getComponentManager( Class<T> type, String role, String roleHint, ClassRealm realm )
    {
        ComponentManager<?> manager = componentManagers.get( new Key( realm, role, roleHint ) );
        if ( manager != null && isAssignableFrom( type, manager.getType() ) )
        {
            return (ComponentManager<T>) manager;
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    private Set<ClassRealm> getSearchRealms( boolean specifiedOnly )
    {
        // determine realms to search
        Set<ClassRealm> realms = ClassRealmUtil.getContextRealms( container.getClassWorld() );

        if ( realms.isEmpty() )
        {
            if ( specifiedOnly )
            {
                return null;
            }

            realms.addAll( container.getClassWorld().getRealms() );
        }

        return realms;
    }

    private <T> ComponentManager<T> createComponentManager( ComponentDescriptor<T> descriptor,
                                                            String role,
                                                            String roleHint )
        throws ComponentLookupException
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
            throw new ComponentLookupException( "Unsupported instantiation strategy: " + instantiationStrategy,
                role,
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
                role,
                roleHint,
                descriptor.getRealm() );
        }

        // Create the ComponentManager
        ComponentManager<T> componentManager = componentManagerFactory.createComponentManager( container,
            lifecycleHandler,
            descriptor,
            role,
            roleHint );

        // Add componentManager to indexes
        Key key = new Key( descriptor.getRealm(), role, roleHint );
        componentManagers.put( key, componentManager );

        return componentManager;
    }

    private static class Key implements Comparable<Key>
    {
        private final ClassRealm realm;
        private final String role;
        private final String roleHint;
        private final int hashCode;

        private Key( ClassRealm realm, String role, String roleHint )
        {
            this.realm = realm;

            if ( role == null )
            {
                role = "null";
            }
            this.role = role;

            if ( roleHint == null )
            {
                roleHint = "null";
            }
            this.roleHint = roleHint;

            int hashCode;
            hashCode = ( realm != null ? realm.hashCode() : 0 );
            hashCode = 31 * hashCode + role.hashCode();
            hashCode = 31 * hashCode + roleHint.hashCode();
            this.hashCode = hashCode;
        }

        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            Key key = (Key) o;

            return !( realm != null ? !realm.equals( key.realm ) : key.realm != null ) &&
                role.equals( key.role ) &&
                roleHint.equals( key.roleHint );

        }

        public int hashCode()
        {
            return hashCode;
        }

        public String toString()
        {
            return realm + "/" + role + "/" + roleHint;
        }

        public int compareTo( Key o )
        {
            int value;
            if ( realm != null )
            {
                value = o.realm == null? -1 : realm.getId().compareTo( o.realm.getId() );
            }
            else
            {
                value = o.realm == null ? 0 : 1;
            }

            if ( value == 0 )
            {
                value = role.compareTo( o.role );
                if ( value == 0 )
                {
                    value = roleHint.compareTo( o.roleHint );
                }
            }
            return value;
        }
    }
}
