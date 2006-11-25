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
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;


public interface ConfigurationConverter
{
    boolean canConvert( Class type );

    /**
     * @param converterLookup     Repository of available converters
     * @param configuration
     * @param type                the type of object to read
     * @param baseType            the type of object the the source is
     * @param classRealm          ClassRealm which should be used for loading classes
     * @param expressionEvaluator the expression evaluator to use for expressions
     * @return the object
     * @throws ComponentConfigurationException
     *
     * @todo a better way, instead of baseType, would be to pass in a factory for new classes that could be based from the given package
     */
    Object fromConfiguration( ConverterLookup converterLookup,
                              PlexusConfiguration configuration,
                              Class type,
                              Class baseType,
                              ClassRealm classRealm,
                              ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException;

    /**
     * @param converterLookup     Repository of available converters
     * @param configuration
     * @param type                the type of object to read
     * @param baseType            the type of object the the source is
     * @param classRealm          ClassRealm which should be used for loading classes
     * @param expressionEvaluator the expression evaluator to use for expressions
     * @return the object
     * @throws ComponentConfigurationException
     *
     * @todo a better way, instead of baseType, would be to pass in a factory for new classes that could be based from the given package
     */
    Object fromConfiguration( ConverterLookup converterLookup,
                              PlexusConfiguration configuration,
                              Class type,
                              Class baseType,
                              ClassRealm classRealm,
                              ExpressionEvaluator expressionEvaluator,
                              ConfigurationListener listener )
        throws ComponentConfigurationException;
}
