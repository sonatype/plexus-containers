package org.codehaus.plexus;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

import java.io.File;
import java.io.Reader;
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

    PlexusContainer createChildContainer( String name,
                                          List classpathJars,
                                          Map context )
        throws PlexusContainerException;

    PlexusContainer createChildContainer( String name,
                                          List classpathJars,
                                          Map context,
                                          List discoveryListeners )
        throws PlexusContainerException;

    Object lookup( String componentKey )
        throws ComponentLookupException;

    Object lookup( String role,
                   String roleHint )
        throws ComponentLookupException;

    Map lookupMap( String role )
        throws ComponentLookupException;

    List lookupList( String role )
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

    void initialize()
        throws PlexusContainerException;

    void start()
        throws PlexusContainerException;

    void dispose();

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    Context getContext();

    ClassRealm getContainerRealm();

    // ----------------------------------------------------------------------
    // Container setup
    // ----------------------------------------------------------------------

    //TODO: remove, make the PlexusContainer interface mostly immutable
    void addContextValue( Object key,
                          Object value );

    //TODO: remove, make the PlexusContainer interface mostly immutable
    void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException;

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

    // Required by maven
    LoggerManager getLoggerManager();

    /**
     * @deprecated
     */
    Logger getLogger();
}
