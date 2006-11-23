package org.codehaus.plexus.component.configurator.converters.composite;

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

import org.codehaus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Properties;

/**
 * Converter for <code>java.util.Properties</code>.
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class PropertiesConverter
    extends AbstractConfigurationConverter
{
    public boolean canConvert( Class type )
    {
        return Properties.class.isAssignableFrom( type );
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassRealm classRealm, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        String element = configuration.getName();

        Properties retValue = new Properties();

        PlexusConfiguration[] children = configuration.getChildren( "property" );

        if ( children != null && children.length > 0 )
        {
            for ( int i = 0; i < children.length; i++ )
            {
                PlexusConfiguration child = children[i];

                addEntry( retValue, element, child, expressionEvaluator );
            }
        }

        return retValue;
    }

    private void addEntry( Properties properties, String element, PlexusConfiguration property,
                           ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        String name;

        name = property.getChild( "name" ).getValue( null );

        if ( name == null )
        {
            String msg = "Converter: java.util.Properties. Trying to convert the configuration element: '" + element +
                "', missing child element 'name'.";

            throw new ComponentConfigurationException( msg );
        }

        Object rawValue = fromExpression( property.getChild( "value" ), expressionEvaluator );
        
        String value = String.valueOf( rawValue );

        properties.put( name, value );
    }
}
