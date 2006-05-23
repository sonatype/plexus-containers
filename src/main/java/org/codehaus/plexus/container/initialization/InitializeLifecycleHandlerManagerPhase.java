package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeLifecycleHandlerManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration configuration = context.getContainerConfiguration();

        PlexusConfiguration c = configuration.getChild( "lifecycle-handler-manager" );

        setupCoreComponent( "lifecycle-handler-manager", configurator, c, context.getContainer() );

        context.getContainer().getLifecycleHandlerManager().initialize();
    }

}
