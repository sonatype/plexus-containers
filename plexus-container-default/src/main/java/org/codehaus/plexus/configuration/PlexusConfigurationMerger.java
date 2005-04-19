package org.codehaus.plexus.configuration;

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
