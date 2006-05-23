package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentLookupManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration configuration = context.getContainerConfiguration();

        PlexusConfiguration c = configuration.getChild( "component-lookup-manager" );

        setupCoreComponent( "component-lookup-manager", configurator, c, context.getContainer() );

        context.getContainer().getComponentLookupManager().setContainer( context.getContainer() );
    }
}
