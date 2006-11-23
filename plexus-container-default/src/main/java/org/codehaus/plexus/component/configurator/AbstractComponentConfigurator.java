package org.codehaus.plexus.component.configurator;

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
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public abstract class AbstractComponentConfigurator
    implements ComponentConfigurator
{
    // TODO: configured as a component
    protected ConverterLookup converterLookup = new DefaultConverterLookup();

    public void configureComponent( Object component, PlexusConfiguration configuration, ClassRealm containerRealm )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, new DefaultExpressionEvaluator(), containerRealm );
    }

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, expressionEvaluator, containerRealm, null );
    }

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        // TODO: here so extended classes without the method continue to work. should be removed
        // this won't hit the method above going into a loop - instead, it will hit the overridden one
        configureComponent( component, configuration, expressionEvaluator, containerRealm );
    }
}
