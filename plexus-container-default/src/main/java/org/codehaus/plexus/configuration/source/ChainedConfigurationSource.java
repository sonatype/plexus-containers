package org.codehaus.plexus.configuration.source;

import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * A configuration source that delegates to any number of underlying configuration sources. If you are an application
 * author and want to create a custom source of configuration for the components in your application then you would most
 * likely want to create a chained configuration source where you can decide the order of processing, but still have the
 * container perform its default behavior.
 *
 * @author Jason van Zyl
 */
public class ChainedConfigurationSource
    implements ConfigurationSource
{
    private List<ConfigurationSource> configurationSources;

    public ChainedConfigurationSource( List<ConfigurationSource> configurationSources )
    {
        this.configurationSources = configurationSources;
    }

    public PlexusConfiguration getConfiguration( ComponentDescriptor componentDescriptor )
    {
        for ( ConfigurationSource configurationSource : configurationSources )
        {
            PlexusConfiguration configuration = configurationSource.getConfiguration( componentDescriptor );

            if ( configuration != null )
            {
                return configuration;
            }
        }

        return null;
    }

    public List<ConfigurationSource> getConfigurationSources()
    {
        return configurationSources;
    }
}
