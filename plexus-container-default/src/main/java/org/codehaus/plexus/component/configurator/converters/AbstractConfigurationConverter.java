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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
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

    private static final String HINT = "hint";
    
    private static final String ROLE = "role";

    private PlexusContainer container;

    /**
     * Returns the container.
     */
    public PlexusContainer getContainer() {
        return container;
    }

    /**
     * Sets the container.
     */
    public void setContainer(PlexusContainer pContainer) {
        container = pContainer;
    }

    /**
     * We will check if the user has provided a hint which class should be used for given
     * field. The user may do this by specifying by using something like the following
     * patterns:
     * <pre>
     *   &lt;foo implementation="com.MyFoo"&gt; &lt;!-- Explicit class name --&gt;
     *   &lt;foo hint="bar"&gt; &lt;!-- Lookup via class name as the role and the
     *                                  given hint --&gt;
     *   &lt;foo hint="bar" role="com.MyBar"&ht;
     *                          &lt;!-- Lookup via the given role and hint --&gt;
     * </pre>
     */
    protected Object getObjectForImplementationHint( Class type, PlexusConfiguration configuration,
                                                     ClassLoader classLoader )
        throws ComponentConfigurationException, ComponentLookupException
    {
        String implementation = configuration.getAttribute( IMPLEMENTATION, null );
        String hint = configuration.getAttribute( HINT, null );
        String role = configuration.getAttribute( ROLE, null );

        if ( implementation != null )
        {
            if ( hint != null  ||  role != null )
            {
                String msg = "The attributes '" + IMPLEMENTATION
                    + "' and '" + HINT + "' or '" + ROLE + "' are mutually exclusive.";
                throw new ComponentConfigurationException( msg );
            }
            Class c = getClassForImplementationHint( type, configuration, classLoader );
            return instantiateObject( c );
        }
        if ( role != null || hint != null )
        {
            PlexusContainer pc = getContainer();
            if ( pc == null )
            {
                String msg = "Component lookup requires that the container is set.";
                throw new ComponentConfigurationException( msg );
            }
            if ( role == null )
            {
                return container.lookup( type, hint );
            }
            else
            {
                if ( hint == null )
                {
                    return container.lookup( role );
                }
                else
                {
                    return container.lookup( role, hint );
                }
            }
        }
        return instantiateObject( type );
    }

    /**
     * We will check if user has provided a hint which class should be used for given field.
     * So we will check if something like &lt;foo implementation="com.MyFoo"&gt; is present in configuraion.
     * If 'implementation' hint was provided we will try to load correspoding class
     * If we are unable to do so error will be reported
     */
    protected Class getClassForImplementationHint( Class type, PlexusConfiguration configuration,
                                                   ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        Class retValue = type;

        String implementation = configuration.getAttribute( IMPLEMENTATION, null );

        if ( implementation != null )
        {
            try
            {
                retValue = classLoader.loadClass( implementation );

            }
            catch ( ClassNotFoundException e )
            {
                String msg = "ClassNotFoundException: Class name which was explicitly given in configuration using"
                    + " 'implementation' attribute: '" + implementation + "' cannot be loaded";

                throw new ComponentConfigurationException( msg, e );
            }
            catch ( UnsupportedClassVersionError e )
            {
                String msg = "UnsupportedClassVersionError: Class name which was explicitly given in configuration"
                    + " using 'implementation' attribute: '" + implementation + "' cannot be loaded";

                throw new ComponentConfigurationException( msg, e );
            }
            catch ( LinkageError e )
            {
                String msg = "LinkageError: Class name which was explicitly given in configuration using"
                    + " 'implementation' attribute: '" + implementation + "' cannot be loaded";

                throw new ComponentConfigurationException( msg, e );
            }
        }

        return retValue;
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
                                     Class baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        return fromConfiguration( converterLookup, configuration, type, baseType, classLoader, expressionEvaluator,
                                  null );
    }
}
