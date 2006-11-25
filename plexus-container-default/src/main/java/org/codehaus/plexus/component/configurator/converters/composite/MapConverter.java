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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Converter for <code>java.util.Properties</code>.
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class MapConverter
    extends AbstractConfigurationConverter
{
    public boolean canConvert( Class type )
    {
        return Map.class.isAssignableFrom( type ) && !Properties.class.isAssignableFrom( type );
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassRealm classRealm, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Object retValue;

        String expression = configuration.getValue( null );

        if ( expression == null )
        {
            Map map = new TreeMap();

            PlexusConfiguration[] children = configuration.getChildren();

            for ( int i = 0; i < children.length; i++ )
            {
                PlexusConfiguration child = children[i];

                String name = child.getName();

                map.put( name, fromExpression( child, expressionEvaluator ) );
            }
            retValue = map;
        }
        else
        {
            retValue = fromExpression( configuration, expressionEvaluator );
        }
        return retValue;
    }

}
