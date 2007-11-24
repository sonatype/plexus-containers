package org.codehaus.plexus.configuration.source;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/** @author Jason van Zyl */
public class ContainerConfigurationSource
    implements ConfigurationSource
{
    public PlexusConfiguration getConfiguration( ComponentDescriptor componentDescriptor )
    {
        if ( componentDescriptor.hasConfiguration() )
        {
            return componentDescriptor.getConfiguration();
        }

        return null;       
    }
}
