package org.codehaus.plexus.component.configurator.converters.lookup;

//import org.codehaus.plexus.component.configurator.converters.basic.BigIntegerConverter;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.BooleanConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ByteConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.CharConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DateConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DoubleConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FloatConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.IntConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.LongConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ShortConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringBufferConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.CollectionConverter;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.composite.PlexusConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.PropertiesConverter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultConverterLookup implements ConverterLookup
{
    private List converters = new LinkedList();

    private Map converterMap = new HashMap();

    public DefaultConverterLookup()
    {
        registerDefaultBasicConverters();

        registerDefaultCompositeConverters();
    }

    public void registerConverter( ConfigurationConverter converter )
    {
       converters.add( converter );
    }

    public ConfigurationConverter lookupConverterForType( Class type ) throws ComponentConfigurationException
    {
        ConfigurationConverter retValue = null;

        if ( converterMap.containsKey( type ) )
        {
            retValue = ( ConfigurationConverter ) converterMap.get( type );
        }
        else
        {
            for ( Iterator iterator = converters.iterator(); iterator.hasNext(); )
            {
                ConfigurationConverter converter = ( ConfigurationConverter ) iterator.next();

                if ( converter.canConvert( type ) )
                {
                    retValue = converter;

                    break;
                }
            }
            converterMap.put( type, retValue );
        }
        if ( retValue == null )
        {
            // this is highly irregular
            String msg = "Configuration converter lookup failed";

            throw new ComponentConfigurationException( msg );
        }
        return retValue;
    }


    private void registerDefaultBasicConverters()
    {
        registerConverter( new BooleanConfigurationConverter() );

        registerConverter( new ByteConfigurationConverter() );

        registerConverter( new CharConfigurationConverter() );

        registerConverter( new DoubleConfigurationConverter() );

        registerConverter( new FloatConfigurationConverter() );

        registerConverter( new IntConfigurationConverter() );

        registerConverter( new LongConfigurationConverter() );

        registerConverter( new ShortConfigurationConverter() );

        registerConverter( new StringBufferConfigurationConverter() );

        registerConverter( new StringConfigurationConverter() );

        registerConverter( new DateConfigurationConverter() );

        //registerConverter( new BigIntegerConverter() );
    }

    private void registerDefaultCompositeConverters()
    {
        registerConverter( new CollectionConverter() );

        registerConverter( new PropertiesConverter() );

        registerConverter( new PlexusConfigurationConverter() );

        // this converter should be always registred as the last one
        registerConverter( new ObjectWithFieldsConverter() );
    }

}
