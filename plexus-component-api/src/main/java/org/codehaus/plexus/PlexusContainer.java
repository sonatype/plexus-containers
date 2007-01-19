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

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * The core component of Plexus. This is the entry-point for loading and accessing other components, as well as an
 * element in a hierarchy of containers. A Plexus Container can also itself be a component, however, the hierarchy must
 * be bootstrapped by a PlexusContainer implementation.
 */
public interface PlexusContainer
{
    String ROLE = PlexusContainer.class.getName();

    /**
     * Returns the unique name of this container in the container hierarchy.
     * @return the unique name of this container in the container hierarchy
     */
    String getName();

    /**
     * Returns the date this container was created.
     * @return the date this container was created
     */
    public Date getCreationDate();

    /**
     * Returns true if this container has a child with the given name.
     * @param name a key unique amongst this container's children
     * @return true if this container has a child with the given name
     */
    boolean hasChildContainer( String name );

    /**
     * Removes the keyed child from this container. Does not necessarily dispose the children.
     * @param name a key unique amongst this container's children
     */
    void removeChildContainer( String name );

    /**
     * Returns a child container with the unique name.
     * @param name a key unique amongst this container's children
     * @return the keyed child container
     */
    PlexusContainer getChildContainer( String name );

    // ------------------------------------------------------------------------
    // Lookup
    // ------------------------------------------------------------------------

    /**
     * @deprecated
     */
    Object lookup( String componentKey )
        throws ComponentLookupException;

    /**
     * Looks up and returns a component object with the given unique key or role.
     * @param componentKey a unique key for the desired component
     * @return a Plexus component object
     */
    Object lookup( String componentKey, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Object lookup( String role, String roleHint )
        throws ComponentLookupException;

    /**
     * Looks up and returns a component object with the given unique role/role-hint combination.
     * @param role a non-unique key for the desired component
     * @param roleHint a hint for the desired component implementation
     * @return a Plexus component object
     */
    Object lookup( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Object lookup( Class componentClass )
        throws ComponentLookupException;

    /**
     * Looks up and returns a component object with the given unique key or role.
     * @param componentClass a unique key for the desired component
     * @return a Plexus component object
     */
    Object lookup( Class componentClass, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Object lookup( Class role, String roleHint )
        throws ComponentLookupException;

    /**
     * Looks up and returns a component object with the given unique role/role-hint combination.
     * @param role a non-unique class key for the desired component
     * @param roleHint a hint for the desired component implementation
     * @return a Plexus component object
     */
    Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */

    List lookupList( String role )
        throws ComponentLookupException;

    /**
     * Looks up and returns a List of component objects with the given role.
     * @param role a non-unique key for the desired components
     * @return a List of component objects
     */
    List lookupList( String role, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    List lookupList( Class role )
        throws ComponentLookupException;

    /**
     * Looks up and returns a List of component objects with the given role.
     * @param role a non-unique class key for the desired components
     * @return a List of component objects
     */
    List lookupList( Class role, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Map lookupMap( String role )
        throws ComponentLookupException;

    /**
     * Looks up and returns a Map of component objects with the given role, keyed by all available role-hints.
     * @param role a non-unique key for the desired components
     * @return a Map of component objects
     */
    Map lookupMap( String role, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Map lookupMap( Class role )
        throws ComponentLookupException;

    /**
     * Looks up and returns a Map of component objects with the given role, keyed by all available role-hints.
     * @param role a non-unique class key for the desired components
     * @return a Map of component objects
     */
    Map lookupMap( Class role, ClassRealm realm )
        throws ComponentLookupException;

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    /**
     * @deprecated use {@link PlexusContainer#getComponentDescriptor(String, ClassRealm)}
     */
    ComponentDescriptor getComponentDescriptor( String componentKey );

    /**
     * Returns the ComponentDescriptor with the given component key. Searches up the hierarchy until one is found, null
     * if none is found.
     * @param componentKey a unique key for the desired component's descriptor
     * @return the ComponentDescriptor with the given component key
     */
    ComponentDescriptor getComponentDescriptor( String componentKey, ClassRealm componentRealm );

    /**
     * @deprecated use {@link PlexusContainer#getComponentDescriptorMap(String, ClassRealm)}
     */
    Map getComponentDescriptorMap( String role );

    /**
     * Returns a Map of ComponentDescriptors with the given role, keyed by role-hint. Searches up the hierarchy until
     * all are found, an empty Map if none are found.
     * @param role a non-unique key for the desired components
     * @return a Map of component descriptors keyed by role-hint
     */
    Map getComponentDescriptorMap( String role, ClassRealm componentRealm );

    /**
     * @deprecated use {@link PlexusContainer#getComponentDescriptorList(String, ClassRealm)}
     */

    List getComponentDescriptorList( String role );

    /**
     * Returns a List of ComponentDescriptors with the given role. Searches up the hierarchy until all are found, an
     * empty List if none are found.
     * @param role a non-unique key for the desired components
     * @return a List of component descriptors
     */
    List getComponentDescriptorList( String role, ClassRealm componentRealm );

    /**
     * Adds a component descriptor to this container. componentDescriptor should have realmId set.
     * @param componentDescriptor
     * @throws ComponentRepositoryException
     */
    void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException;

    /**
     * Releases the component from the container. This is dependant upon how the implementation manages the component,
     * but usually enacts some standard lifecycle shutdown procedure on the component. In every case, the component is
     * no longer accessible from the container (unless another is created).
     * @param component the plexus component object to release
     * @throws ComponentLifecycleException
     */
    void release( Object component )
        throws ComponentLifecycleException;

    /**
     * Releases all Mapped component values from the container.
     * @see PlexusContainer#release( Object component )
     * @param components Map of plexus component objects to release
     * @throws ComponentLifecycleException
     */
    void releaseAll( Map components )
        throws ComponentLifecycleException;

    /**
     * Releases all Listed components from the container.
     * @see PlexusContainer#release( Object component )
     * @param components List of plexus component objects to release
     * @throws ComponentLifecycleException
     */
    void releaseAll( List components )
        throws ComponentLifecycleException;

    /**
     * Returns true if this container has the keyed component.
     * @param componentKey
     * @return true if this container has the keyed component
     */
    boolean hasComponent( String componentKey );

    /**
     * Returns true if this container has a component with the given role/role-hint.
     * @param role
     * @param roleHint
     * @return true if this container has a component with the given role/role-hint
     * @deprecated
     */
    boolean hasComponent( String role, String roleHint );

    /**
     * Attempts to suspend execution of the component.
     * @param component a plexus component
     * @throws ComponentLifecycleException
     */
    void suspend( Object component )
        throws ComponentLifecycleException;

    /**
     * Attempts to resume execution of the component.
     * @param component a plexus component
     * @throws ComponentLifecycleException
     */
    void resume( Object component )
        throws ComponentLifecycleException;

    /**
     * Disposes of this container, which in turn disposes all of it's components. This container should also remove
     * itself from the container hierarchy.
     */
    void dispose();

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    /**
     * Add a key/value pair to this container's Context.
     * @param key any unique object valid to the Context's implementation
     * @param value any object valid to the Context's implementation
     */
    void addContextValue( Object key, Object value );

    /**
     * Returns this container's context. A Context is a simple data store used to hold values which may alter the
     * execution of the Container.
     * @return this container's context.
     */
    Context getContext();

    /**
     * Returns the Classworld's ClassRealm of this Container, which acts as the default parent for all contained
     * components.
     * @return the ClassRealm of this Container
     */
    ClassRealm getContainerRealm();

    // ----------------------------------------------------------------------
    // Discovery
    // ----------------------------------------------------------------------

    /**
     * Adds the listener to this container. ComponentDiscoveryListeners have the ability to respond to various
     * ComponentDiscoverer events.
     * @param listener A listener which responds to differnet ComponentDiscoveryEvents
     */
    void registerComponentDiscoveryListener( ComponentDiscoveryListener listener );

    /**
     * Removes the listener from this container.
     * @param listener A listener to remove
     */
    void removeComponentDiscoveryListener( ComponentDiscoveryListener listener );

    /**
     * Adds a directory of jar resources.
     * @see PlexusContainer#addJarResource(File)
     * @param repository a directory containing JAR files
     */
    void addJarRepository( File repository );

    /**
     * Adds a jar to this container's ClassRealm - whose components are then discovered (via the various registered
     * ComponentDiscoverer's).
     * @param resource a JAR file
     * @throws PlexusContainerException
     */
    void addJarResource( File resource )
        throws PlexusContainerException;

    // ----------------------------------------------------------------------
    // Autowiring Support
    // ----------------------------------------------------------------------

    /**
     * Assembles a component and returns it. May bypass the normal component assembly hooks (such as creation of a
     * ComponentDescriptor).
     * @param component a valid Plexus component
     * @return a componsed (dependency-injected) component
     */
    Object autowire( Object component )
        throws CompositionException;

    /**
     * Creates a corrosponding component instance found in this container's classrealm, then autowires it.
     * @see PlexusContainer#autowire(Object)
     * @param clazz A class available in this container's ClassRealm
     * @return A newly created and autowired component
     * @throws CompositionException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    Object createAndAutowire( String clazz )
        throws CompositionException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    // ----------------------------------------------------------------------
    // Reloading
    // ----------------------------------------------------------------------

    /**
     * Sets the ability to reload a component's metadata multiple times.
     * @param reloadingEnabled true is a component may be reloaded
     */
    void setReloadingEnabled( boolean reloadingEnabled );

    /**
     * Returns true if a component's metadata may be reloaded, otherwise, it may be loaded only once.
     * @return true if a component's metadata may be reloaded
     */
    boolean isReloadingEnabled();

    // ------------------------------------------------------------------------
    // Required for compatibility
    // ------------------------------------------------------------------------

    void setLoggerManager( LoggerManager loggerManager );

    // Required by maven
    LoggerManager getLoggerManager();

    /**
     * @deprecated
     */
    Logger getLogger();

    // Taken from alpha-9 to keep things working

    void setName( String name );

    /**
     * Sets the parent of this container.
     * @param container the parent of this container, null if none
     */
    void setParentPlexusContainer( PlexusContainer container );

    PlexusContainer createChildContainer( String name, List classpathJars, Map context )
        throws PlexusContainerException;

    public PlexusContainer createChildContainer( String name, List classpathJars, Map context, List discoveryListeners )
        throws PlexusContainerException;

    // ----------------------------------------------------------------------------
    // Component/Plugin ClassRealm creation
    // ----------------------------------------------------------------------------

    /**
     * Creates and returns a new class realm under this container's realm for the given list of jars. If the realm
     * already exists, return the realm with the given ID?
     * @see PlexusContainer#addJarResource(File)
     * @param id unique key for the ClassRealm
     * @param jars list of JARs to place in the realm.
     * @throws PlexusContainerException
     */
    public ClassRealm createComponentRealm( String id, List jars )
        throws PlexusContainerException;

    ClassRealm getComponentRealm( String realmId );
}
