package org.codehaus.plexus.component.configurator.converters.basic;

import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

public abstract class AbstractBasicConverter extends AbstractConfigurationConverter
{
    abstract public Object fromString( String str );


    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type, ClassLoader classLoader, ComponentDescriptor componentDescriptor )
            throws ComponentConfigurationException
    {
        if ( configuration.getChildCount() > 0 )
        {
            //@todo what we should do here?
        }

        String configValue = null;

        try
        {
            configValue = configuration.getValue();
        }
        catch ( PlexusConfigurationException e )
        {
            String msg = "Error occured while reading config element '"
                    + configuration.getName()
                    + "' of component "
                    + componentDescriptor.getHumanReadableKey();

            throw new ComponentConfigurationException( msg, e );
        }

        Object retValue = fromString( configValue );

        return retValue;
    }
}