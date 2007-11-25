package org.codehaus.plexus.its;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.Logger;

/**
 * @plexus.component role-hint="standard"
 */
public class DefaultApp
    implements App
{
    /** @plexus.requirement */
    private PlexusContainer container;

    /** @plexus.requirement */
    private Logger logger;

    public PlexusContainer getContainer()
    {
        return container;
    }

    public Logger getLogger()
    {
        return logger;
    }
}
