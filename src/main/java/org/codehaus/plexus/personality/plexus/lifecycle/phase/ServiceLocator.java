package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Map;

/**
 * Provides services to components by means of a lookup.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ServiceLocator
{
	//----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
    // Component release
    // ----------------------------------------------------------------------

    void release( Object component )
        throws ComponentLifecycleException;

    void releaseAll( Map components )
        throws ComponentLifecycleException;

    void releaseAll( List components )
        throws ComponentLifecycleException;

    // ----------------------------------------------------------------------
    // Component discovery
    // ----------------------------------------------------------------------

    boolean hasComponent( String componentKey );

    boolean hasComponent( String role, String roleHint );
}
