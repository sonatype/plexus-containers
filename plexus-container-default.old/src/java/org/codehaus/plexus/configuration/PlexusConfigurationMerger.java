package org.codehaus.plexus.configuration;

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
    // resources                          | user
    // -----------------------------------+-----------------------------------------------------------------
    // component-manager-manager          | user ignore
    // -----------------------------------+-----------------------------------------------------------------
    // lifecycle-handler-manager          | user wins, but system lifecycles show through
    // -----------------------------------+-----------------------------------------------------------------
    // components                         | user
    // -----------------------------------+-----------------------------------------------------------------

    public static PlexusConfiguration merge( PlexusConfiguration user, PlexusConfiguration system )
        throws Exception
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

        mergedConfiguration.addChild( user.getChild( "resources" ) );

        // ----------------------------------------------------------------------
        // Component manager manager
        // ----------------------------------------------------------------------

        mergedConfiguration.addChild( system.getChild( "component-manager-manager" ) );

        // ----------------------------------------------------------------------
        // Component discoverer manager
        // ----------------------------------------------------------------------

        mergedConfiguration.addChild( system.getChild( "component-discoverer-manager" ) );

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
