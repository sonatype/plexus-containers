package org.codehaus.plexus.component.configurator.converters.lookup;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;


public interface ConverterLookup
{
    void registerConverter( ConfigurationConverter converter );

    ConfigurationConverter lookupConverterForType( Class type ) throws ComponentConfigurationException;
}
