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
import org.codehaus.plexus.component.configurator.converters.ComponentValueSetter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class ObjectWithFieldsConverter
    extends AbstractConfigurationConverter
{
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
                                     Class baseType, ClassRealm classRealm, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Object retValue = fromExpression( configuration, expressionEvaluator, type );
        if ( retValue == null )
        {
            try
            {
                // it is a "composite" - we compose it from its children. It does not have a value of its own
                Class implementation = getImplementationClass( type, baseType, configuration, classRealm );

                retValue = instantiateObject( implementation );

                processConfiguration( converterLookup, retValue, classRealm, configuration, expressionEvaluator,
                                      listener );
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


    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassRealm classRealm,
                                      PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        processConfiguration( converterLookup, object, classRealm, configuration, null );
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassRealm classRealm,
                                      PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        processConfiguration( converterLookup, object, classRealm, configuration, expressionEvaluator, null );
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassRealm classRealm,
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

            valueSetter.configure( childConfiguration, classRealm, expressionEvaluator );
        }
    }
}
