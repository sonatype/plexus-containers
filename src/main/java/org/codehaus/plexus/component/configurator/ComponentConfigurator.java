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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ComponentConfigurator
{
    String ROLE = ComponentConfigurator.class.getName();

    void configureComponent( Object component,
                             PlexusConfiguration configuration,
                             ClassRealm classRealm )
        throws ComponentConfigurationException;

    /**
     * @deprecated
     */
    void configureComponent( Object component,
                             PlexusConfiguration configuration,
                             ClassLoader classLoader )
        throws ComponentConfigurationException;

    void configureComponent( Object component,
                             PlexusConfiguration configuration,
                             ExpressionEvaluator expressionEvaluator,
                             ClassRealm classRealm )
        throws ComponentConfigurationException;

    /**
     * @deprecated
     */
    void configureComponent( Object component,
                             PlexusConfiguration configuration,
                             ExpressionEvaluator expressionEvaluator,
                             ClassLoader classLoader )
        throws ComponentConfigurationException;

    void configureComponent( Object component,
                             PlexusConfiguration configuration,
                             ExpressionEvaluator expressionEvaluator,
                             ClassRealm classRealm,
                             ConfigurationListener listener )
        throws ComponentConfigurationException;

    /** @deprecated */
    void configureComponent( Object component,
                             PlexusConfiguration configuration,
                             ExpressionEvaluator expressionEvaluator,
                             ClassLoader classLoader,
                             ConfigurationListener listener )
        throws ComponentConfigurationException;
}
