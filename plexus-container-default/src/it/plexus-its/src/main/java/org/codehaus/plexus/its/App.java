package org.codehaus.plexus.its;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.Logger;

/** @author Jason van Zyl */
public interface App
{
    PlexusContainer getContainer();

    Logger getLogger();
}
