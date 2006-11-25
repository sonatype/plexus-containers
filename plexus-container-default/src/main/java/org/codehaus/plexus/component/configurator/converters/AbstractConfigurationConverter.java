package org.codehaus.plexus.component.configurator.converters;

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
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractConfigurationConverter
    implements ConfigurationConverter
{
    private static final String IMPLEMENTATION = "implementation";

    /**
     * We will check if user has provided a hint which class should be used for given field.
     * So we will check if something like <foo implementation="com.MyFoo"> is present in configuraion.
     * If 'implementation' hint was provided we will try to load correspoding class
     * If we are unable to do so error will be reported
     */
    protected Class getClassForImplementationHint( Class type, PlexusConfiguration configuration,
                                                   ClassRealm classRealm )
        throws ComponentConfigurationException
    {
        Class retValue = type;

        String implementation = configuration.getAttribute( IMPLEMENTATION, null );

        if ( implementation != null )
        {
            try
            {
                retValue = classRealm.loadClass( implementation );

            }
            catch ( ClassNotFoundException e )
            {
                String msg =
                    "Class name which was explicitly given in configuration using 'implementation' attribute: '" +
                        implementation + "' cannot be loaded";

                throw new ComponentConfigurationException( msg, e );
            }
        }

        return retValue;
    }

    protected Class getImplementationClass( Class type, Class baseType, PlexusConfiguration configuration, ClassRealm classRealm )
        throws ComponentConfigurationException
    {
        // if there's an implementation hint, try that.

        Class childType = getClassForImplementationHint( null, configuration, classRealm );

        if ( childType != null )
        {
            return childType;
        }

        // try using the fieldname to determine the implementation.

        String configEntry = configuration.getName();

        String name = fromXML( configEntry );

        // First, see whether the fieldname might be a fully qualified classname

        if ( name.indexOf( '.' ) > 0 )
        {
            try
            {
                return classRealm.loadClass( name );
            }
            catch ( ClassNotFoundException e )
            {
                // not found, continue processing
            }
        }

        // Next, try to find a class in the package of the object we're configuring

        String className = constructClassName( baseType, name );

        Exception lastException = null;

        try
        {
            return classRealm.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            // the guessed class does not exist. Store exception for later use. Continue processing.
            lastException = e;
        }

        // if the given type is not null, just return that

        if ( type != null )
        {
            return type;
        }
        else
        {
            // type is only null if we have a Collection and this method is called
            // for the compound type of that collection.

            if ( configuration.getChildCount() == 0 )
            {
                // If the configuration has no children but only text, try a String.
                // TODO: If we had generics we could try that instead - or could the component descriptor list an impl?
                return String.class;
            }
            else
            {
                // there are no options left. Our best guess is that the fieldname
                // indicates a class in the component's package, so report that.

                throw new ComponentConfigurationException( "Error loading class '" + className + "'", lastException );
            }
        }
    }

    /**
     * Constructs a classname from a class and a fieldname.
     * For example, baseType is 'package.Component',
     * field is 'someThing', then it constructs 'package.SomeThing'.
     *
     */
    private String constructClassName( Class baseType, String name )
    {
        String baseTypeName = baseType.getName();

        // Some classloaders don't create Package objects for classes
        // so we have to resort to slicing up the class name

        int lastDot = baseTypeName.lastIndexOf( '.' );

        String className;

        if ( lastDot == -1 )
        {
            className = name;
        }
        else
        {
            String basePackage = baseTypeName.substring( 0, lastDot );

            className = basePackage + "." + StringUtils.capitalizeFirstLetter( name );
        }

        return className;
    }

    protected Class loadClass( String classname, ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        Class retValue;

        try
        {
            retValue = classLoader.loadClass( classname );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ComponentConfigurationException( "Error loading class '" + classname + "'", e );
        }

        return retValue;
    }

    protected Object instantiateObject( String classname, ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        Class clazz = loadClass( classname, classLoader );

        return instantiateObject( clazz );
    }

    protected Object instantiateObject( Class clazz )
        throws ComponentConfigurationException
    {
        Object retValue;

        try
        {
            retValue = clazz.newInstance();

            return retValue;
        }
        catch ( IllegalAccessException e )
        {
            throw new ComponentConfigurationException( "Class '" + clazz.getName() + "' cannot be instantiated", e );
        }
        catch ( InstantiationException e )
        {
            throw new ComponentConfigurationException( "Class '" + clazz.getName() + "' cannot be instantiated", e );
        }
    }


    // first-name --> firstName
    protected String fromXML( String elementName )
    {
        return StringUtils.lowercaseFirstLetter( StringUtils.removeAndHump( elementName, "-" ) );
    }

    // firstName --> first-name
    protected String toXML( String fieldName )
    {
        return StringUtils.addAndDeHump( fieldName );
    }

    protected Object fromExpression( PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator,
                                     Class type )
        throws ComponentConfigurationException
    {
        Object v = fromExpression( configuration, expressionEvaluator );
        if ( v != null )
        {
            if ( !type.isAssignableFrom( v.getClass() ) )
            {
                String msg = "Cannot assign configuration entry '" + configuration.getName() + "' to '" + type +
                    "' from '" + configuration.getValue( null ) + "', which is of type " + v.getClass();
                throw new ComponentConfigurationException( configuration, msg );
            }
        }
        return v;
    }

    protected Object fromExpression( PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        Object v = null;
        String value = configuration.getValue( null );
        if ( value != null && value.length() > 0 )
        {
            // Object is provided by an expression
            // This seems a bit ugly... canConvert really should return false in this instance, but it doesn't have the
            //   configuration to know better
            try
            {
                v = expressionEvaluator.evaluate( value );
            }
            catch ( ExpressionEvaluationException e )
            {
                String msg = "Error evaluating the expression '" + value + "' for configuration value '" +
                    configuration.getName() + "'";
                throw new ComponentConfigurationException( configuration, msg, e );
            }
        }
        if ( v == null )
        {
            value = configuration.getAttribute( "default-value", null );
            if ( value != null && value.length() > 0 )
            {
                try
                {
                    v = expressionEvaluator.evaluate( value );
                }
                catch ( ExpressionEvaluationException e )
                {
                    String msg = "Error evaluating the expression '" + value + "' for configuration value '" +
                        configuration.getName() + "'";
                    throw new ComponentConfigurationException( configuration, msg, e );
                }
            }
        }
        return v;
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassRealm classRealm, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        return fromConfiguration( converterLookup, configuration, type, baseType, classRealm, expressionEvaluator,
                                  null );
    }
}
