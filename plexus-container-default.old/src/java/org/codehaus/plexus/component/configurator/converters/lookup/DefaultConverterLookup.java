package org.codehaus.plexus.component.configurator.converters.lookup;

//import org.codehaus.plexus.component.configurator.converters.basic.BigIntegerConverter;
import org.codehaus.plexus.component.configurator.converters.basic.BooleanConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ByteConverter;
import org.codehaus.plexus.component.configurator.converters.basic.CharConverter;
import org.codehaus.plexus.component.configurator.converters.basic.Converter;
import org.codehaus.plexus.component.configurator.converters.basic.DateConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DoubleConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FloatConverter;
import org.codehaus.plexus.component.configurator.converters.basic.IntConverter;
import org.codehaus.plexus.component.configurator.converters.basic.LongConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ShortConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringBufferConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringConverter;
import org.codehaus.plexus.component.configurator.converters.composite.CollectionConverter;
import org.codehaus.plexus.component.configurator.converters.composite.CompositeConverter;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.composite.PlexusConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.PropertiesConverter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class DefaultConverterLookup implements ConverterLookup
{
    private List basicConverters = new LinkedList();

    private List compositeConverters = new LinkedList();

    private Map basicConverterMap = new HashMap();

    private Map compositeConverterMap = new HashMap();

    public DefaultConverterLookup()
    {
        registerDefaultBasicConverters();

        registerDefaultCompositeConverters();
    }

    public void registerBasicConverter( Converter converter )
    {
        basicConverters.add( converter );
    }

    public void registerCompositeConverter( CompositeConverter converter )
    {
        compositeConverters.add( converter );
    }

    public CompositeConverter lookupCompositeConverterForType( Class type )
    {
        CompositeConverter retValue = null;

        if ( compositeConverterMap.containsKey( type ) )
        {
            retValue = ( CompositeConverter ) compositeConverterMap.get( type );
        }
        else
        {
            for ( Iterator iterator = compositeConverters.iterator(); iterator.hasNext(); )
            {
                CompositeConverter converter = ( CompositeConverter ) iterator.next();

                if ( converter.canConvert( type ) )
                {
                    retValue = converter;

                    break;
                }
            }

            compositeConverterMap.put( type, retValue );
        }

        System.out.println( "Lookup: " + type + " --> " + retValue );
        
        if ( retValue == null )
        {
            //@todo fix me
            throw new RuntimeException( "foo" );
        }


        return retValue;
    }

    public Converter lookupBasicConverterForType( Class type )
    {
        Converter retValue = null;

        if ( basicConverterMap.containsKey( type ) )
        {
            retValue = ( Converter ) basicConverterMap.get( type );
        }
        else
        {
            for ( Iterator iterator = basicConverters.iterator(); iterator.hasNext(); )
            {
                Converter converter = ( Converter ) iterator.next();

                if ( converter.canConvert( type ) )
                {
                    retValue = converter;

                    break;
                }
            }
            basicConverterMap.put( type, retValue );
        }
        if ( retValue == null )
        {
            //@todo fix me
            throw new RuntimeException( "foo" );
        }
        return retValue;
    }


    private void registerDefaultBasicConverters()
    {
        registerBasicConverter( new BooleanConverter() );

        registerBasicConverter( new ByteConverter() );

        registerBasicConverter( new CharConverter() );

        registerBasicConverter( new DoubleConverter() );

        registerBasicConverter( new FloatConverter() );

        registerBasicConverter( new IntConverter() );

        registerBasicConverter( new LongConverter() );

        registerBasicConverter( new ShortConverter() );

        registerBasicConverter( new StringBufferConverter() );

        registerBasicConverter( new StringConverter() );

        registerBasicConverter( new DateConverter() );

        //registerBasicConverter( new BigIntegerConverter() );
    }

    private void registerDefaultCompositeConverters()
    {
        registerCompositeConverter( new CollectionConverter() );

        registerCompositeConverter( new PropertiesConverter() );

        registerCompositeConverter( new PlexusConfigurationConverter() );

        registerCompositeConverter( new ObjectWithFieldsConverter() );
    }

}
