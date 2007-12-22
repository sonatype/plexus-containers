package org.codehaus.plexus.configuration.source;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class AnotherDummyConfigurationSource implements ConfigurationSource
{
    
    private ConfigurationSource configurationSource;

    public PlexusConfiguration getConfiguration( ComponentDescriptor componentDescriptor )
    {
        return null;
    }

}
