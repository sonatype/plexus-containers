package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentManagerManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration configuration = context.getContainerConfiguration();

        PlexusConfiguration c = configuration.getChild( "component-manager-manager" );

        setupCoreComponent( "component-manager-manager", configurator, c, context.getContainer() );

        context.getContainer().getComponentManagerManager().setLifecycleHandlerManager( context.getContainer().getLifecycleHandlerManager() );
    }
}
