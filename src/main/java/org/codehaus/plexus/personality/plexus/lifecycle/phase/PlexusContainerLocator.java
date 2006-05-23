package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Map;

/**
 * A ServiceLocator for PlexusContainer.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusContainerLocator
    implements ServiceLocator
{
    private PlexusContainer container;

    public PlexusContainerLocator( PlexusContainer container )
    {
        this.container = container;
    }

    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        return container.lookup( componentKey );
    }

    public Object lookup( String role,
                          String roleHint )
        throws ComponentLookupException
    {
        return container.lookup( role, roleHint );
    }

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        return container.lookupMap( role );
    }

    public List lookupList( String role )
        throws ComponentLookupException
    {
        return container.lookupList( role );
    }

    public void release( Object component )
        throws ComponentLifecycleException
    {
        container.release( component );
    }

    public void releaseAll( Map components )
        throws ComponentLifecycleException
    {
        container.releaseAll( components );
    }

    public void releaseAll( List components )
        throws ComponentLifecycleException
    {
        container.releaseAll( components );
    }

    public boolean hasComponent( String componentKey )
    {
        return container.hasComponent( componentKey );
    }

    public boolean hasComponent( String role,
                                 String roleHint )
    {
        return container.hasComponent( role, roleHint );
    }
}
