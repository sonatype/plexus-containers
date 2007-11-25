package org.codehaus.plexus.its;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;

/**
 * @plexus.component role-hint="log-enabled"
 */
public class DefaultLogEnabledApp
    extends AbstractLogEnabled
    implements App
{
    /** @plexus.requirement */
    private PlexusContainer container;

    public PlexusContainer getContainer()
    {
        return container;
    }

    public Logger getLogger()
    {
        return getLogger();
    }
}