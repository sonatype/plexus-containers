package org.codehaus.plexus.component.configurator.converters.composite;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;


public class PlexusConfigurationConverter extends AbstractCompositeConverter
{
    public boolean canConvert( Class type )
    {
        return PlexusConfiguration.class.isAssignableFrom( type );
    }

    public Object fromConfiguration( ConverterLookup converterLookup,
                                     PlexusConfiguration configuration,
                                     Class type,
                                     ClassLoader classLoader,
                                     ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        return configuration;
    }
}
