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
    // resource-manager                   | user ignore
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
    {
        XmlPlexusConfiguration mergedConfiguration = new XmlPlexusConfiguration( "plexus" );

        PlexusConfiguration loadOnStart = user.getChild( "load-on-start" );

        if ( loadOnStart.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( loadOnStart );
        }

        PlexusConfiguration systemProperties = user.getChild( "system-properties" );

        if ( systemProperties.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( systemProperties );
        }

        PlexusConfiguration[] configurationsDirectories = user.getChildren( "configurations-directory" );

        if ( configurationsDirectories.length != 0 )
        {
            for ( int i = 0; i < configurationsDirectories.length; i++ )
            {
                mergedConfiguration.addChild( configurationsDirectories[i] );
            }
        }

        PlexusConfiguration logging = user.getChild( "logging" );

        if ( logging.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( logging );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "logging" ) );
        }

        PlexusConfiguration componentRepository = user.getChild( "component-repository" );

        if ( componentRepository.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( componentRepository );
        }
        else
        {
            mergedConfiguration.addChild( system.getChild( "component-repository" ) );
        }

        mergedConfiguration.addChild( user.getChild( "resources" ) );

        mergedConfiguration.addChild( system.getChild( "resource-manager" ) );

        mergedConfiguration.addChild( system.getChild( "component-manager-manager" ) );

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

        PlexusConfiguration components = user.getChild( "components" );

        if ( components.getChildCount() != 0 )
        {
            mergedConfiguration.addChild( components );
        }

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
}
