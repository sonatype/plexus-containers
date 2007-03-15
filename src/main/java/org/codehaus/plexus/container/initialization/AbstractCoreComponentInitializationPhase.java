package org.codehaus.plexus.container.initialization;

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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public abstract class AbstractCoreComponentInitializationPhase
    extends AbstractContainerInitializationPhase
{
    BasicComponentConfigurator configurator = new BasicComponentConfigurator();

    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        initializeCoreComponent( context );
    }

    protected abstract void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException;

    protected void setupCoreComponent( String role,
                                       BasicComponentConfigurator configurator,
                                       PlexusConfiguration c,
                                       PlexusContainer container )
        throws ContainerInitializationException
    {
        String implementation = c.getAttribute( "implementation", null );

        if ( implementation == null )
        {
            //TODO: put plexus.conf in constants and change to plexus.xml
            String msg = "Core component: '" + role + "' + which is needed by plexus to function properly cannot " +
                "be instantiated. Implementation attribute was not specified in plexus.conf." +
                "This is highly irregular, your plexus JAR is most likely corrupt.";

            throw new ContainerInitializationException( msg );
        }

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( role );

        componentDescriptor.setImplementation( implementation );

        PlexusConfiguration configuration = new XmlPlexusConfiguration( "containerConfiguration" );

        configuration.addChild( c );

        try
        {
            configurator.configureComponent( container, configuration, container.getContainerRealm() );
        }
        catch ( ComponentConfigurationException e )
        {
            // TODO: don't like rewrapping the same exception, but better than polluting this all through the config code
            String message = "Error configuring component: " + componentDescriptor.getHumanReadableKey();
            throw new ContainerInitializationException( message, e );
        }
    }
}
