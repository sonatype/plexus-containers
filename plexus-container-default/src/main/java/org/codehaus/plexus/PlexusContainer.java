package org.codehaus.plexus;

import org.codehaus.classworlds.ClassRealm;
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
import java.util.Set;

public interface PlexusContainer
{
    String ROLE = PlexusContainer.class.getName();

    String getName();

    public Date getCreationDate();

    boolean hasChildContainer( String name );

    void removeChildContainer( String name );

    PlexusContainer getChildContainer( String name );

    PlexusContainer createChildContainer( String name,
                                          Map context,
                                          String configuration,
                                          Set jars )
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

    void dispose();

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    void addContextValue( Object key, Object value );

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
}
