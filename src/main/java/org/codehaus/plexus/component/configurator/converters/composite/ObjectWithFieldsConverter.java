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
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class ObjectWithFieldsConverter extends AbstractConfigurationConverter
{
    /**
     * @todo I am not sure what should go into this method
     * @param type
     * @return
     */
    public boolean canConvert( Class type )
    {
        boolean retValue = true;

        if ( Dictionary.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }

        else if ( Map.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }
        else if ( Collection.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }

        return retValue;
    }

    public Object fromConfiguration( ConverterLookup converterLookup,
                                     PlexusConfiguration configuration,
                                     Class type,
                                     Class baseType,
                                     ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        // it is a "composite"
        Class implementation = getClassForImplementationHint( type, configuration, classLoader );

        Object retValue = instantiateObject( implementation );

        processConfiguration( converterLookup, retValue, classLoader, configuration );

        return retValue;

    }


    public void processConfiguration( ConverterLookup converterLookup,
                                      Object object,
                                      ClassLoader classLoader,
                                      PlexusConfiguration configuration )
            throws ComponentConfigurationException
    {
        int items = configuration.getChildCount();

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration childConfiguration = configuration.getChild( i );

            String elementName = childConfiguration.getName();

            String fieldName = fromXML( elementName );

            Field field = getFieldByName( fieldName, object );

            Class fieldType = field.getType();

            ConfigurationConverter converter = converterLookup.lookupConverterForType( fieldType );

            Object value = converter.fromConfiguration( converterLookup, childConfiguration, fieldType,
                                                        object.getClass(), classLoader );

            setFieldValue( field, object, value );
        }


    }


    private void setFieldValue( Field field, Object object, Object value )
            throws ComponentConfigurationException
    {
        try
        {
            boolean wasAccessible = field.isAccessible();

            if ( !wasAccessible )
            {
                field.setAccessible( true );
            }

            field.set( object, value );

            if ( !wasAccessible )
            {
                field.setAccessible( false );
            }
        }
        catch ( IllegalAccessException e )
        {
            String msg = "Cannot access field: '"
                    + field.getName() +
                    " in class: '"
                    + object.getClass().getName()
                    + "'";

            throw new ComponentConfigurationException( msg );
        }
    }

    private Field getFieldByName( String fieldName, Object object )
            throws ComponentConfigurationException
    {

        Class clazz = object.getClass();
        Field retValue = ReflectionUtils.getFieldByNameIncludingSuperclasses( fieldName, clazz );

        if ( retValue == null )
        {
            String msg = "Class '" + clazz.getName() + "' does not contain a field named '" + fieldName + "'";

            throw new ComponentConfigurationException( msg );
        }

        return retValue;
    }

}
