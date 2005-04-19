package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ConfigurablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Configurable )
        {
            try
            {
                ( (Configurable) object ).configure( manager.getComponentDescriptor().getConfiguration() );
            }
            catch ( PlexusConfigurationException e )
            {
                throw new PhaseExecutionException( "Error occurred during phase execution", e );
            }
        }
    }
}
