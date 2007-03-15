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

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.ComponentValueSetter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class ObjectWithFieldsConverter
    extends AbstractConfigurationConverter
{

    private static final String HINT = "hint";

    private static final String ROLE = "role";

    private PlexusContainer plexusContainer;

    /**
     * @param plexusContainer container is mandatory in case of configuration field with role and/or hint
     */
    public ObjectWithFieldsConverter( PlexusContainer plexusContainer )
    {
        this.plexusContainer = plexusContainer;
    }

    /**
     * @param type
     * @return
     * @todo I am not sure what should go into this method
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

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Object retValue = fromExpression( configuration, expressionEvaluator, type );
        if ( retValue == null )
        {
            try
            {
                // it is a "composite" - we compose it from its children. It does not have a value of its own
                retValue = getObjectForImplementationHint( type, configuration, classLoader );

                processConfiguration( converterLookup, retValue, classLoader, configuration, expressionEvaluator,
                                      listener );
            }
            catch ( ComponentLookupException e )
            {
                throw new ComponentConfigurationException( configuration, e );
            }
            catch ( ComponentConfigurationException e )
            {
                if ( e.getFailedConfiguration() == null )
                {
                    e.setFailedConfiguration( configuration );
                }

                throw e;
            }
        }
        return retValue;
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
            if ( hint != null || role != null )
            {
                String msg = "The attributes '" + IMPLEMENTATION + "' and '" + HINT + "' or '" + ROLE
                    + "' are mutually exclusive.";
                throw new ComponentConfigurationException( msg );
            }
            Class c = getClassForImplementationHint( type, configuration, classLoader );
            return instantiateObject( c );
        }
        if ( role != null || hint != null )
        {
            PlexusContainer pc = this.plexusContainer;
            if ( pc == null )
            {
                String msg = "Component lookup requires that the container is set.";
                throw new ComponentConfigurationException( msg );
            }
            if ( role == null )
            {
                return this.plexusContainer.lookup( type, hint );
            }
            else
            {
                if ( hint == null )
                {
                    return this.plexusContainer.lookup( role );
                }
                else
                {
                    return this.plexusContainer.lookup( role, hint );
                }
            }
        }
        return instantiateObject( type );
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassLoader classLoader,
                                      PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        processConfiguration( converterLookup, object, classLoader, configuration, null );
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassLoader classLoader,
                                      PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        processConfiguration( converterLookup, object, classLoader, configuration, expressionEvaluator, null );
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassLoader classLoader,
                                      PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator,
                                      ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        int items = configuration.getChildCount();

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration childConfiguration = configuration.getChild( i );

            String elementName = childConfiguration.getName();

            ComponentValueSetter valueSetter = new ComponentValueSetter( fromXML( elementName ), object,
                                                                         converterLookup, listener );

            valueSetter.configure( childConfiguration, classLoader, expressionEvaluator );
        }
    }
}
