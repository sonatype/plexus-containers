package org.codehaus.plexus.component.configurator.converters.composite;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.util.Properties;


public class PropertiesConverter extends AbstractCompositeConverter
{
    public boolean canConvert( Class type )
    {
        return Properties.class.isAssignableFrom( type );
    }


    public Object fromConfiguration( ConverterLookup converterLookup,
                                     PlexusConfiguration configuration,
                                     Class type,
                                     ClassLoader classLoader,
                                     ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        Properties retValue = new Properties();

        PlexusConfiguration[] children = configuration.getChildren( "property" );

        if ( children != null && children.length > 0 )
        {
            for ( int i = 0; i < children.length; i++ )
            {
                PlexusConfiguration child = children[ i ];

                addEntry( retValue, child );
            }
        }
        return retValue;
    }

    private void addEntry( Properties properties, PlexusConfiguration child ) throws ComponentConfigurationException
    {
        try
        {
            String name =  child.getChild( "name" ).getValue();

            String value =  child.getChild( "value" ).getValue();

            properties.put( name, value );
        }
        catch ( PlexusConfigurationException e )
        {
            String msg = null;

            throw new ComponentConfigurationException( msg );
        }
    }
}
