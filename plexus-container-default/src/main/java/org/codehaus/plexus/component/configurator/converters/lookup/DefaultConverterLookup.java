package org.codehaus.plexus.component.configurator.converters.lookup;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.BooleanConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ByteConverter;
import org.codehaus.plexus.component.configurator.converters.basic.CharConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DateConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DoubleConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FileConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FloatConverter;
import org.codehaus.plexus.component.configurator.converters.basic.IntConverter;
import org.codehaus.plexus.component.configurator.converters.basic.LongConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ShortConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringBufferConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringConverter;
import org.codehaus.plexus.component.configurator.converters.composite.ArrayConverter;
import org.codehaus.plexus.component.configurator.converters.composite.CollectionConverter;
import org.codehaus.plexus.component.configurator.converters.composite.MapConverter;
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
            throw new ComponentConfigurationException( "Configuration converter lookup failed for type: " + type );
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

        registerConverter( new FileConverter() );

        //registerConverter( new BigIntegerConverter() );
    }

    private void registerDefaultCompositeConverters()
    {
        registerConverter( new MapConverter() );
        
        registerConverter( new ArrayConverter() );

        registerConverter( new CollectionConverter() );

        registerConverter( new PropertiesConverter() );

        registerConverter( new PlexusConfigurationConverter() );

        // this converter should be always registred as the last one
        registerConverter( new ObjectWithFieldsConverter() );
    }

}
