package org.codehaus.plexus.component.configurator.converters.composite;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;


public interface CompositeConverter
{
    boolean canConvert( Class type );


    /**
     * @param converterLookup  Repository of available converters
     * @param configuration
     * @param type                Type of the class which must be returned
     * @param classLoader         ClassLoader which should be used for loading classes
     * @param componentDescriptor Descriptor of the component for which the work is done
     * @return
     * @throws ComponentConfigurationException
     *
     */
    public Object fromConfiguration( ConverterLookup converterLookup,
                                     PlexusConfiguration configuration,
                                     Class type,
                                     ClassLoader classLoader,
                                     ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException;
}
