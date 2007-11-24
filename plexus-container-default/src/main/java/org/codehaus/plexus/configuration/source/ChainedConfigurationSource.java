package org.codehaus.plexus.configuration.source;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;
import java.util.Iterator;

/**
 * A configuration source that delegates to any number of underlying configuration sources.
 *
 * @author Jason van Zyl
 */
public class ChainedConfigurationSource
    implements ConfigurationSource
{
    private List configurationSources;

    public PlexusConfiguration getConfiguration( ComponentDescriptor componentDescriptor )
    {
        for ( Iterator i = configurationSources.iterator(); i.hasNext(); )
        {
            ConfigurationSource configurationSource = (ConfigurationSource) i.next();

            PlexusConfiguration configuration = configurationSource.getConfiguration( componentDescriptor );

            if ( configuration != null )
            {
                return configuration;
            }
        }

        return null;
    }
}
