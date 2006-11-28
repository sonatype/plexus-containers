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

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public abstract class AbstractComponentConfigurator
    implements ComponentConfigurator, Contextualizable
{
    protected MutablePlexusContainer container;

    protected ConverterLookup converterLookup = new DefaultConverterLookup();

    public void configureComponent( Object component,
                                    PlexusConfiguration configuration,
                                    ClassRealm classRealm )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, new DefaultExpressionEvaluator(), classRealm );
    }

    public void configureComponent( Object component,
                                    PlexusConfiguration configuration,
                                    ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, createClassRealm( container, classLoader ) );
    }

    public void configureComponent( Object component,
                                    PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator,
                                    ClassRealm classRealm )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, expressionEvaluator, classRealm, null );
    }

    public void configureComponent( Object component,
                                    PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator,
                                    ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, expressionEvaluator, createClassRealm( container, classLoader ) );
    }

    public void configureComponent( Object component,
                                    PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator,
                                    ClassRealm classRealm,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        // TODO: here so extended classes without the method continue to work. should be removed
        // this won't hit the method above going into a loop - instead, it will hit the overridden one
        configureComponent( component, configuration, expressionEvaluator, classRealm );
    }

    public void configureComponent( Object component,
                                    PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator,
                                    ClassLoader classLoader,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, expressionEvaluator, createClassRealm( container, classLoader ),
                            listener );
    }

    public static ClassRealm createClassRealm( MutablePlexusContainer container,
                                               ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        try
        {
            ClassRealm realm = null;

            try
            {
                realm = container.getClassWorld().getRealm( classLoader.toString() );
            }
            catch ( NoSuchRealmException e )
            {
                //
            }

            if ( realm == null )
            {
                realm = container.getClassWorld().newRealm( classLoader.toString(), classLoader );
            }

            return realm;
        }
        catch ( DuplicateRealmException e )
        {
            throw new ComponentConfigurationException( "Error converting ClassLoader to a ClassRealm.", e );
        }
    }

    // ----------------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (MutablePlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
