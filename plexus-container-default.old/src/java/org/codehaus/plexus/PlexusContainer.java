package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;

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

    Object lookup( String role, String id )
        throws ComponentLookupException;

    Map lookupMap( String role )
        throws ComponentLookupException;

    List lookupList( String role )
        throws ComponentLookupException;

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

    boolean hasComponent( String role, String id );

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
    // Container setup
    // ----------------------------------------------------------------------

    void addContextValue( Object key, Object value );

    void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException;
}
