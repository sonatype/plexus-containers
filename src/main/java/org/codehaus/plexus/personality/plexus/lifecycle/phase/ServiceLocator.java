package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

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
        throws Exception;

    void releaseAll( Map components )
        throws Exception;

    void releaseAll( List components )
        throws Exception;

    // ----------------------------------------------------------------------
    // Component discovery
    // ----------------------------------------------------------------------

    boolean hasComponent( String componentKey );

    boolean hasComponent( String role, String roleHint );
}
