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

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PlexusContainer
{
    String ROLE = PlexusContainer.class.getName();

    String getName();

    public Date getCreationDate();

    boolean hasChildContainer( String name );

    void removeChildContainer( String name );

    PlexusContainer getChildContainer( String name );

    // ----------------------------------------------------------------------------
    // Lookup
    // ----------------------------------------------------------------------------

    Object lookup( String componentKey )
        throws ComponentLookupException;

    Object lookup( String role,
                   String roleHint )
        throws ComponentLookupException;

    Map lookupMap( String role )
        throws ComponentLookupException;

    List lookupList( String role )
        throws ComponentLookupException;

    Object lookup( Class componentClass )
        throws ComponentLookupException;

    Object lookup( Class role,
                   String roleHint )
        throws ComponentLookupException;

    Map lookupMap( Class role )
        throws ComponentLookupException;

    List lookupList( Class role )
        throws ComponentLookupException;

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    ComponentDescriptor getComponentDescriptor( String componentKey );

    Map getComponentDescriptorMap( String role );

    List getComponentDescriptorList( String role );

    void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException;

    void release( Object component )
        throws ComponentLifecycleException;

    void releaseAll( Map components )
        throws ComponentLifecycleException;

    void releaseAll( List components )
        throws ComponentLifecycleException;

    boolean hasComponent( String componentKey );

    boolean hasComponent( String role,
                          String roleHint );

    void suspend( Object component )
        throws ComponentLifecycleException;

    void resume( Object component )
        throws ComponentLifecycleException;

    void dispose();

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    void addContextValue( Object key,
                          Object value );

    Context getContext();

    ClassRealm getContainerRealm();

    // ----------------------------------------------------------------------
    // Discovery
    // ----------------------------------------------------------------------

    void registerComponentDiscoveryListener( ComponentDiscoveryListener listener );

    void removeComponentDiscoveryListener( ComponentDiscoveryListener listener );

    void addJarRepository( File repository );

    void addJarResource( File resource )
        throws PlexusContainerException;

    // ----------------------------------------------------------------------
    // Autowiring Support
    // ----------------------------------------------------------------------

    Object autowire( Object component )
        throws CompositionException;

    Object createAndAutowire( String clazz )
        throws CompositionException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    // ----------------------------------------------------------------------
    // Reloading
    // ----------------------------------------------------------------------

    void setReloadingEnabled( boolean reloadingEnabled );

    boolean isReloadingEnabled();

    // ----------------------------------------------------------------------------
    // Required for compatibility
    // ----------------------------------------------------------------------------

    void setLoggerManager( LoggerManager loggerManager );

    // Required by maven
    LoggerManager getLoggerManager();

    /**
     * @deprecated
     */
    Logger getLogger();

    // Taken from alpha-9 to keep things working

    void setName( String name );

    void setParentPlexusContainer( PlexusContainer container );

    PlexusContainer createChildContainer( String name,
                                          List classpathJars,
                                          Map context )
        throws PlexusContainerException;

    public PlexusContainer createChildContainer( String name,
                                                 List classpathJars,
                                                 Map context,
                                                 List discoveryListeners )
        throws PlexusContainerException;

    // ----------------------------------------------------------------------------
    // Component/Plugin ClassRealm creation
    // ----------------------------------------------------------------------------

    public ClassRealm createComponentRealm( String id,
                                            List jars )
        throws PlexusContainerException;
}
