package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

/**
 * @todo (michal) should this phase be called only for components which
 *  does not implement Configurable interface?
 */
public class AutoConfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        try
        {
            ComponentConfigurator componentConfigurator =
                (ComponentConfigurator) manager.getContainer().lookup( ComponentConfigurator.ROLE );

            if ( manager.getComponentDescriptor().hasConfiguration() )
            {
                componentConfigurator.configureComponent( object, manager.getComponentDescriptor().getConfiguration() );
            }
        }
        catch ( ComponentLookupException e )
        {
            throw new PhaseExecutionException( "Unable to auto-configure component as its configurator could not be found", e );
        }
        catch ( ComponentConfigurationException e )
        {
            throw new PhaseExecutionException( "Unable to auto-configure component", e );
        }
    }
}
