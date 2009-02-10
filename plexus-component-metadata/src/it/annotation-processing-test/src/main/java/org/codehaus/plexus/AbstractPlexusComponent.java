package org.codehaus.plexus;

import org.codehaus.plexus.component.annotations.Requirement;

public abstract class AbstractPlexusComponent
    implements PlexusComponent
{
    @Requirement
    protected Executor executor;
}
