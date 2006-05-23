package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentFactoryManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration configuration = context.getContainerConfiguration();

        PlexusConfiguration c = configuration.getChild( "component-factory-manager" );

        setupCoreComponent( "component-factory-manager", configurator, c, context.getContainer() );

        if ( context.getContainer().getComponentFactoryManager() instanceof Contextualizable )
        {
            context.getContainer().getContext().put( PlexusConstants.PLEXUS_KEY, context.getContainer() );

            try
            {
                ( (Contextualizable) context.getContainer().getComponentFactoryManager() ).contextualize( context.getContainer().getContext() );
            }
            catch ( ContextException e )
            {
                throw new ContainerInitializationException( "Error contextualization component factory manager.", e );
            }
        }
    }
}
