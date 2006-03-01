package org.codehaus.plexus.component.reloading;

import org.codehaus.plexus.PlexusContainer;

/**
 * @author Jason van Zyl
 * @version $Revision$
 */
public interface ComponentReloadingStrategy
{
    boolean shouldReload( String role, PlexusContainer container )
        throws ComponentReloadingException;

    boolean shouldReload( String role, String roleHint, PlexusContainer container )
        throws ComponentReloadingException;

}
