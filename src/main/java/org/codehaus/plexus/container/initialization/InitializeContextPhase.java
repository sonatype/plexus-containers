package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.PlexusConstants;

/**
 * @author Jason van Zyl
 */
public class InitializeContextPhase
    extends AbstractContainerInitializationPhase
{
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        context.getContainer().getContext().put( PlexusConstants.PLEXUS_KEY, context.getContainer() );

        context.getContainer().getContext().put( PlexusConstants.PLEXUS_CORE_REALM, context.getContainer().getContainerRealm() );
    }
}
