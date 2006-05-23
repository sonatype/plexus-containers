package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentRepositoryPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration configuration = context.getContainerConfiguration();

        PlexusConfiguration c = configuration.getChild( "component-repository" );

        setupCoreComponent( "component-repository", configurator, c, context.getContainer() );

        context.getContainer().getComponentRepository().configure( configuration );

        context.getContainer().getComponentRepository().setClassRealm( context.getContainer().getContainerRealm() );

        try
        {
            context.getContainer().getComponentRepository().initialize();
        }
        catch ( ComponentRepositoryException e )
        {
            throw new ContainerInitializationException( "Error initializing component repository.", e );
        }

    }

}
