package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.Logger;

import java.io.Reader;
import java.util.List;
import java.util.Map;

public interface PlexusContainer
{
    String ROLE = PlexusContainer.class.getName();

    // ----------------------------------------------------------------------
    // Component lookup
    // ----------------------------------------------------------------------

    Object lookup( String componentKey )
        throws ComponentLookupException;

    Object lookup( String role, String roleHint )
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

    // ----------------------------------------------------------------------
    // Component release
    // ----------------------------------------------------------------------

    void release( Object component );

    void releaseAll( Map components );

    void releaseAll( List components );

    // ----------------------------------------------------------------------
    // Component discovery
    // ----------------------------------------------------------------------

    boolean hasComponent( String componentKey );

    boolean hasComponent( String role, String roleHint );

    // ----------------------------------------------------------------------
    // Component replacement
    // ----------------------------------------------------------------------

    void suspend( Object component );

    void resume( Object component );

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    void initialize()
        throws Exception;

    void start()
        throws Exception;

    void dispose()
        throws Exception;

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    Context getContext();

    // ----------------------------------------------------------------------
    // Container setup
    // ----------------------------------------------------------------------

    void setParentPlexusContainer( PlexusContainer parentContainer );

    void addContextValue( Object key, Object value );

    void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException;

    Logger getLogger();

    ClassLoader getClassLoader();

    Object createComponentInstance( ComponentDescriptor componentDescriptor )
        throws Exception;

    void composeComponent( Object component, ComponentDescriptor componentDescriptor )
        throws Exception;

    // ----------------------------------------------------------------------
    // Discovery
    // ----------------------------------------------------------------------

    void registerComponentDiscoveryListener( ComponentDiscoveryListener listener );

    void removeComponentDiscoveryListener( ComponentDiscoveryListener listener );
}
