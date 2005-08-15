package org.codehaus.plexus.component.configurator.converters;

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
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;


/**
 * @author <a href="mailto:kenney@codehaus.org">Kenney Westerhof</a>
 */
public class ComponentValueSetter
{
    private Object object;
    private String fieldName;
    private ConverterLookup lookup;

    private Method setter;
    private Class setterParamType;
    private ConfigurationConverter setterTypeConverter;

    private Field field;
    private Class fieldType;
    private ConfigurationConverter fieldTypeConverter;

    public ComponentValueSetter( String fieldName, Object object, ConverterLookup lookup )
        throws ComponentConfigurationException
    {
        this.fieldName = fieldName;
        this.object = object;
        this.lookup = lookup;

        if ( object == null )
        {
            throw new ComponentConfigurationException( "Component is null" );
        }

        initSetter();

        initField();

        if ( setter == null && field == null )
        {
            throw new ComponentConfigurationException(
                "Cannot find setter nor field in " + object.getClass().getName() +
                " for '" + fieldName + "'"
            );
        }

        if ( setterTypeConverter == null && fieldTypeConverter == null )
        {
            throw new ComponentConfigurationException(
                "Cannot find converter for " + setterParamType.getName() +
                ( fieldType != null && ! fieldType.equals( setterParamType )
                  ? " or " + fieldType.getName()
                  : ""
                )
            );
        }
    }

    private void initSetter()
    {
        setter = ReflectionUtils.getSetter(
            fieldName, object.getClass()
        );

        if ( setter == null )
        {
            return;
        }

        setterParamType = setter.getParameterTypes()[0];

        try
        {
            setterTypeConverter = lookup.lookupConverterForType( setterParamType );
        }
        catch ( ComponentConfigurationException e)
        {
            // ignore, handle later
        }
    }

    private void initField()
    {
        field = ReflectionUtils.getFieldByNameIncludingSuperclasses(
            fieldName, object.getClass()
        );

        if ( field == null )
        {
            return;
        }

        fieldType = field.getType();

        try
        {
            fieldTypeConverter = lookup.lookupConverterForType( fieldType );
        }
        catch ( ComponentConfigurationException e)
        {
            // ignore, handle later
        }
    }

    private void setValueUsingField( Object value )
        throws ComponentConfigurationException
    {
        String exceptionInfo = object.getClass().getName() +
            "." + field.getName() + "; type: " + value.getClass().getName();

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
            throw new ComponentConfigurationException(
                "Cannot access field: " + exceptionInfo, e
            );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ComponentConfigurationException( 
                "Cannot assign value '" + value + "' to field: " + exceptionInfo, e
            );
        }
    }

    private void setValueUsingSetter( Object value )
        throws ComponentConfigurationException
    {
        if ( setterParamType == null || setter == null )
        {
            throw new ComponentConfigurationException( "No setter found" );
        }

        String exceptionInfo = object.getClass().getName() + "." +
            setter.getName() + "( " + setterParamType.getClass().getName() + " )";

        try
        {
            setter.invoke( object, new Object[] { value } );
        }
        catch ( IllegalAccessException e )
        {
            throw new ComponentConfigurationException(
                "Cannot access method: " + exceptionInfo, e
            );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ComponentConfigurationException( 
                "Invalid parameter supplied while setting '" + value + "' to " +
                exceptionInfo, e
            );
        }
        catch ( InvocationTargetException e )
        {
            throw new ComponentConfigurationException( 
                "Setter " + exceptionInfo + " threw exception when called with parameter '" +
                value + "': " + e.getTargetException().getMessage(),
                e
            );
        }
    }

    public void configure(
        PlexusConfiguration config, ClassLoader cl, ExpressionEvaluator evaluator
    )
        throws ComponentConfigurationException
    {
        Object value = null;

        // try setter converter + method first

        if ( setterTypeConverter != null )
        {
            try
            {
                value = setterTypeConverter.fromConfiguration(
                    lookup, config, setterParamType,
                    object.getClass(), cl, evaluator
                );

                if ( value != null )
                {
                    setValueUsingSetter( value );
                    return;
                }
            }
            catch ( ComponentConfigurationException e )
            {
                if ( fieldTypeConverter == null ||
                    fieldTypeConverter.getClass().equals( setterTypeConverter.getClass() )
                )
                {
                    throw e;
                }
            }
        }


        // try setting field using value found with method
        // converter, if present.

        ComponentConfigurationException savedEx = null;

        if ( value != null )
        {
            try
            {
                setValueUsingField( value );
                return;
            }
            catch ( ComponentConfigurationException e )
            {
                savedEx = e;
            }
        }

        // either no value or setting went wrong. Try
        // new converter.

        value = fieldTypeConverter.fromConfiguration(
            lookup, config, fieldType,
            object.getClass(), cl, evaluator
        );

        if ( value != null )
        {
            setValueUsingField( value );
            return;
        }
        // FIXME: need this?
        else if ( savedEx != null )
        {
            throw savedEx;
        }
    }

}
