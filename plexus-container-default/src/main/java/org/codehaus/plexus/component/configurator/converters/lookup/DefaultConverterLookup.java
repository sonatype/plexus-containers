package org.codehaus.plexus.component.configurator.converters.lookup;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.basic.BooleanConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ByteConverter;
import org.codehaus.plexus.component.configurator.converters.basic.CharConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ClassConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DateConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DoubleConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FileConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FloatConverter;
import org.codehaus.plexus.component.configurator.converters.basic.IntConverter;
import org.codehaus.plexus.component.configurator.converters.basic.LongConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ShortConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringBufferConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringConverter;
import org.codehaus.plexus.component.configurator.converters.basic.UrlConverter;
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

public class DefaultConverterLookup
    implements ConverterLookup
{
    private List converters = new LinkedList();

    private List customConverters = new LinkedList();

    private Map converterMap = new HashMap();

    public DefaultConverterLookup()
    {
        registerDefaultBasicConverters();

        registerDefaultCompositeConverters();
    }

    public void registerConverter( ConfigurationConverter converter )
    {
        customConverters.add( converter );
    }

    protected void registerDefaultConverter( ConfigurationConverter converter )
    {
        converters.add( converter );
    }

    public ConfigurationConverter lookupConverterForType( Class type )
        throws ComponentConfigurationException
    {
        ConfigurationConverter retValue;

        if ( converterMap.containsKey( type ) )
        {
            retValue = (ConfigurationConverter) converterMap.get( type );
        }
        else
        {
            retValue = findConverterForType( customConverters, type );

            if ( retValue == null )
            {
                retValue = findConverterForType( converters, type );
            }
        }

        if ( retValue == null )
        {
            // this is highly irregular
            throw new ComponentConfigurationException( "Configuration converter lookup failed for type: " + type );
        }

        return retValue;
    }

    private ConfigurationConverter findConverterForType( List converters, Class type )
    {
        for ( Iterator iterator = converters.iterator(); iterator.hasNext(); )
        {
            ConfigurationConverter converter = (ConfigurationConverter) iterator.next();

            if ( converter.canConvert( type ) )
            {
                converterMap.put( type, converter );

                return converter;
            }
        }

        return null;
    }

    private void registerDefaultBasicConverters()
    {
        registerDefaultConverter( new BooleanConverter() );

        registerDefaultConverter( new ByteConverter() );

        registerDefaultConverter( new CharConverter() );

        registerDefaultConverter( new DoubleConverter() );

        registerDefaultConverter( new FloatConverter() );

        registerDefaultConverter( new IntConverter() );

        registerDefaultConverter( new LongConverter() );

        registerDefaultConverter( new ShortConverter() );

        registerDefaultConverter( new StringBufferConverter() );

        registerDefaultConverter( new StringConverter() );

        registerDefaultConverter( new DateConverter() );

        registerDefaultConverter( new FileConverter() );

        registerDefaultConverter( new ClassConverter() );

        registerDefaultConverter( new UrlConverter() );

        //registerDefaultConverter( new BigIntegerConverter() );
    }

    private void registerDefaultCompositeConverters()
    {
        registerDefaultConverter( new MapConverter() );

        registerDefaultConverter( new ArrayConverter() );

        registerDefaultConverter( new CollectionConverter() );

        registerDefaultConverter( new PropertiesConverter() );

        registerDefaultConverter( new PlexusConfigurationConverter() );

        // this converter should be always registred as the last one
        registerDefaultConverter( new ObjectWithFieldsConverter() );
    }
}
