package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.PlexusContainer;

import java.util.List;
import java.util.Collections;

/**
 * No Op component composer. It's meant to be used with component
 * personalities which support constructor dependecy injection
 *
 * @author <a href="michal@codehaus.pl">Michal Maczka</a>
 * @version $Id$
 */
public class NoOpComponentComposer extends AbstractComponentComposer
{
    public String getId()
    {
        return null;
    }

    public List assembleComponent( Object component, ComponentDescriptor componentDescriptor, PlexusContainer container )
    {
        return Collections.EMPTY_LIST;
    }
}
