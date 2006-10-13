package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Map;
import java.util.List;

/**
 * @author Jason van Zyl
 */
public interface ComponentLookupManager
{
    String ROLE = ComponentLookupManager.class.getName();

    Object lookup( String componentKey )
        throws ComponentLookupException;

    Map lookupMap( String role )
        throws ComponentLookupException;

    List lookupList( String role )
        throws ComponentLookupException;

    Object lookup( String role,
                   String roleHint )
        throws ComponentLookupException;

    Object lookup( Class componentClass )
        throws ComponentLookupException;

    Map lookupMap( Class role )
        throws ComponentLookupException;

    List lookupList( Class role )
        throws ComponentLookupException;

    Object lookup( Class role,
                   String roleHint )
        throws ComponentLookupException;

    void setContainer( MutablePlexusContainer container );
}
