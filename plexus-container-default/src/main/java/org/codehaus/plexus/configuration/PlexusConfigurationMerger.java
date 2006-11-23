package org.codehaus.plexus.configuration;

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

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * @todo This merger explicity uses the XML implementation of the plexus configuration but
 * it must work for configurations coming from any source.
 */
public class PlexusConfigurationMerger
{
    // -----------------------------------+-----------------------------------------------------------------
    //  E L E M E N T                     |
    // -----------------------------------+-----------------------------------------------------------------
    // load-on-start                      | user
    // -----------------------------------+-----------------------------------------------------------------
    // system-properties                  | user
    // -----------------------------------+-----------------------------------------------------------------
    // configurations-directory           | user
    // -----------------------------------+-----------------------------------------------------------------
    // logging                            | user wins
    // -----------------------------------+-----------------------------------------------------------------
    // component-repository               | user wins
    // -----------------------------------+-----------------------------------------------------------------
    // resources                          | user wins, but system resources show through
    // -----------------------------------+-----------------------------------------------------------------
    // component-manager-manager          | user ignore
    // -----------------------------------+-----------------------------------------------------------------
    // lifecycle-handler-manager          | user wins, but system lifecycles show through
    // -----------------------------------+-----------------------------------------------------------------
    // components                         | user
    // -----------------------------------+-----------------------------------------------------------------

    public static PlexusConfiguration merge( PlexusConfiguration user, PlexusConfiguration system )
    {
        XmlPlexusConfiguration mergedConfiguration = new XmlPlexusConfiguration( "plexus" );

        // ----------------------------------------------------------------------
        // Load on start
        // ----------------------------------------------------------------------

        PlexusConfiguration loadOnStart = user.getChild( "load-on-start" );

        if ( loadOnStart.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( loadOnStart );
        }

        // ----------------------------------------------------------------------
        // System properties
        // ----------------------------------------------------------------------

        PlexusConfiguration systemProperties = user.getChild( "system-properties" );

        if ( systemProperties.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( systemProperties );
        }

        // ----------------------------------------------------------------------
        // Configurations directory
        // ----------------------------------------------------------------------

        PlexusConfiguration[] configurationsDirectories = user.getChildren( "configurations-directory" );

        if ( configurationsDirectories.length != 0 )
        {
            for ( int i = 0; i < configurationsDirectories.length; i++ )
            {
                mergedConfiguration.addChild( configurationsDirectories[i] );
            }
        }

        // ----------------------------------------------------------------------
        // Logging
        // ----------------------------------------------------------------------

        PlexusConfiguration logging = user.getChild( "logging" );

        if ( logging.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( logging );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "logging" ) );
        }

        // ----------------------------------------------------------------------
        // Container initialization phases
        // ----------------------------------------------------------------------

        mergedConfiguration.addChild( system.getChild( "container-initialization") );

        mergedConfiguration.addChild( system.getChild( "component-lookup-manager") );

        // ----------------------------------------------------------------------
        // Component repository
        // ----------------------------------------------------------------------

        PlexusConfiguration componentRepository = user.getChild( "component-repository" );

        if ( componentRepository.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( componentRepository );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "component-repository" ) );
        }

        // ----------------------------------------------------------------------
        // Resources
        // ----------------------------------------------------------------------

        copyResources( system, mergedConfiguration );

        copyResources( user, mergedConfiguration );

        // ----------------------------------------------------------------------
        // Component manager manager
        // ----------------------------------------------------------------------

        mergedConfiguration.addChild( system.getChild( "component-manager-manager" ) );

        // ----------------------------------------------------------------------
        // Component discoverer manager
        // ----------------------------------------------------------------------

        PlexusConfiguration componentDiscovererManager =  user.getChild( "component-discoverer-manager" );

        if ( componentDiscovererManager.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( componentDiscovererManager );

            copyComponentDiscoverers( system.getChild( "component-discoverer-manager" ), componentDiscovererManager );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "component-discoverer-manager" ) );
        }

        // ----------------------------------------------------------------------
        // Component factory manager
        // ----------------------------------------------------------------------

        PlexusConfiguration componentFactoryManager =  user.getChild( "component-factory-manager" );

        if ( componentFactoryManager.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( componentFactoryManager );

            copyComponentFactories( system.getChild( "component-factory-manager" ), componentFactoryManager );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "component-factory-manager" ) );
        }

        // ----------------------------------------------------------------------
        // Lifecycle handler managers
        // ----------------------------------------------------------------------

        PlexusConfiguration lifecycleHandlerManager = user.getChild( "lifecycle-handler-manager" );

        if ( lifecycleHandlerManager.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( lifecycleHandlerManager );

            copyLifecycles( system.getChild( "lifecycle-handler-manager" ), lifecycleHandlerManager );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "lifecycle-handler-manager" ) );
        }

        // ----------------------------------------------------------------------
        // Component factory manager
        // ----------------------------------------------------------------------

        PlexusConfiguration componentComposerManager =  user.getChild( "component-composer-manager" );

        if ( componentComposerManager.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( componentComposerManager );

            copyComponentComposers( system.getChild( "component-composer-manager" ), componentComposerManager );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "component-composer-manager" ) );
        }

        // ----------------------------------------------------------------------
        // Components
        // ----------------------------------------------------------------------
        // We grab the system components first and then add the user defined
        // components so that user defined components will win. When component
        // descriptors are processed the last definition wins because the component
        // descriptors are stored in a Map in the component repository.
        // ----------------------------------------------------------------------

        PlexusConfiguration components = system.getChild( "components" );

        mergedConfiguration.addChild( components );

        copyComponents( user.getChild( "components" ), components );

        return mergedConfiguration;
    }

    private static void copyResources( PlexusConfiguration source, PlexusConfiguration destination )
    {
        PlexusConfiguration handlers[] = source.getChild( "resources" ).getChildren();

        XmlPlexusConfiguration dest = (XmlPlexusConfiguration) destination.getChild( "resources" );

        for ( int i = 0; i < handlers.length; i++ )
        {
            dest.addChild( handlers[i] );
        }
    }

    private static void copyComponentDiscoverers( PlexusConfiguration source, PlexusConfiguration destination )
    {
        PlexusConfiguration handlers[] = source.getChild( "component-discoverers" ).getChildren( "component-discoverer" );

        XmlPlexusConfiguration dest = (XmlPlexusConfiguration) destination.getChild( "component-discoverers" );

        for ( int i = 0; i < handlers.length; i++ )
        {
            dest.addChild( handlers[i] );
        }
    }

    private static void copyComponentFactories( PlexusConfiguration source, PlexusConfiguration destination )
    {
        PlexusConfiguration handlers[] = source.getChild( "component-factories" ).getChildren( "component-factory" );

        XmlPlexusConfiguration dest = (XmlPlexusConfiguration) destination.getChild( "component-factories" );

        for ( int i = 0; i < handlers.length; i++ )
        {
            dest.addChild( handlers[i] );
        }
    }

    private static void copyComponentComposers( PlexusConfiguration source, PlexusConfiguration destination )
    {
        PlexusConfiguration composers[] = source.getChild( "component-composers" ).getChildren( "component-composer" );

        XmlPlexusConfiguration dest = (XmlPlexusConfiguration) destination.getChild( "component-composers" );

        for ( int i = 0; i < composers.length; i++ )
        {
            dest.addChild( composers[i] );
        }
    }

    private static void copyLifecycles( PlexusConfiguration source, PlexusConfiguration destination )
    {
        PlexusConfiguration handlers[] = source.getChild( "lifecycle-handlers" ).getChildren( "lifecycle-handler" );

        XmlPlexusConfiguration dest = (XmlPlexusConfiguration) destination.getChild( "lifecycle-handlers" );

        for ( int i = 0; i < handlers.length; i++ )
        {
            dest.addChild( handlers[i] );
        }
    }

    private static void copyComponents( PlexusConfiguration source, PlexusConfiguration destination )
    {
        PlexusConfiguration components[] = source.getChildren( "component" );

        for ( int i = 0; i < components.length; i++ )
        {
            destination.addChild( components[i] );
        }
    }
}
