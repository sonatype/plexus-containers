package org.codehaus.plexus.component.configurator.converters.composite;

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
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Map;
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
        return Map.class.isAssignableFrom( type );
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                    Class baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        String element = configuration.getName();

        Map retValue = new TreeMap();

        PlexusConfiguration[] children = configuration.getChildren();

        if ( children != null && children.length > 0 )
        {
            for ( int i = 0; i < children.length; i++ )
            {
                PlexusConfiguration child = children[i];

                String name = child.getName();

                String expression = child.getValue( null );

                Object value;

                try
                {
                    value = expressionEvaluator.evaluate( expression );
                }
                catch ( ExpressionEvaluationException e )
                {
                    throw new ComponentConfigurationException(
                                                               "Cannot resolve java.util.Map for configuration element: "
                                                                   + element + "(failed to resolve expression: \'"
                                                                   + expression + "\' keyed to: \'" + name + "\')", e );
                }

                retValue.put( name, value );
            }
        }

        return retValue;
    }

}
