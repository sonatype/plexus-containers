package org.codehaus.plexus.component.configurator.converters.lookup;

//import org.codehaus.plexus.component.configurator.converters.basic.BigIntegerConverter;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.BooleanConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ByteConverter;
import org.codehaus.plexus.component.configurator.converters.basic.CharConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DoubleConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FloatConverter;
import org.codehaus.plexus.component.configurator.converters.basic.IntConverter;
import org.codehaus.plexus.component.configurator.converters.basic.LongConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ShortConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringBufferConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DateConverter;
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
        registerConverter( new BooleanConverter() );

        registerConverter( new ByteConverter() );

        registerConverter( new CharConverter() );

        registerConverter( new DoubleConverter() );

        registerConverter( new FloatConverter() );

        registerConverter( new IntConverter() );

        registerConverter( new LongConverter() );

        registerConverter( new ShortConverter() );

        registerConverter( new StringBufferConverter() );

        registerConverter( new StringConverter() );

        registerConverter( new DateConverter() );

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
