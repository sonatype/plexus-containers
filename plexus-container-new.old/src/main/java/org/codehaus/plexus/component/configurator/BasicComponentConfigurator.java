package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;


/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class BasicComponentConfigurator
        implements ComponentConfigurator
{


    public void configureComponent( Object component,
                                    ComponentDescriptor componentDescriptor,
                                    PlexusConfiguration configuration )
            throws ComponentConfigurationException
    {
        // ----------------------------------------------------------------------
        // We should probably take into consideration the realm that the component
        // came from in order to load the correct classes.
        // ----------------------------------------------------------------------

        // michal: My solution was to to use the same classloader which defined component class
        ClassLoader classLoader = component.getClass().getClassLoader();

        DefaultConverterLookup converterLookup = new DefaultConverterLookup();

        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();

        converter.processConfiguration( converterLookup, component, classLoader, configuration, componentDescriptor );        
    }
}
