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

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.net.MalformedURLException;

/**
 * @author Jason van Zyl
 */
public class InitializeResourcesPhase
    extends AbstractContainerInitializationPhase
{
    //TODO: use constants not string literals
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration[] resourceConfigs = context.getContainer().getConfiguration().getChild( "resources" ).getChildren();

        for ( int i = 0; i < resourceConfigs.length; ++i )
        {
            try
            {
                String name = resourceConfigs[i].getName();

                if ( name.equals( "jar-repository" ) )
                {
                    context.getContainer().addJarRepository( new File( resourceConfigs[i].getValue( null ) ) );
                }
                else if ( name.equals( "directory" ) )
                {
                    File directory = new File( resourceConfigs[i].getValue( null ) );

                    if ( directory.exists() && directory.isDirectory() )
                    {
                        context.getContainer().getContainerRealm().addURL( directory.toURI().toURL() );
                    }
                }
                else
                {
                    context.getContainer().getLogger().warn( "Unknown resource type: " + name );
                }
            }
            catch ( MalformedURLException e )
            {
                String message = "Error configuring resource: " + resourceConfigs[i].getName() + "=" + resourceConfigs[i].getValue( null );

                if ( context.getContainer().getLogger() != null )
                {
                    context.getContainer().getLogger().error( message, e );
                }
                else
                {
                    System.out.println( message );
                }
            }
        }
    }
}
