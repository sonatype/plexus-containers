package org.codehaus.plexus.logging;


import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

/**
 * Test for {@link org.codehaus.plexus.logging.console.ConsoleLoggerManager} and {@link org.codehaus.plexus.logging.console.ConsoleLogger}.
 *
 * @author Mark H. Wilkinson
 * @version $Revision$
 */
public final class ConsoleLoggerManagerTest
    extends AbstractLoggerManagerTest
{
    protected PlexusConfiguration createConfiguration( String threshold )
    {
        DefaultPlexusConfiguration config = new DefaultPlexusConfiguration( "logging" );

        DefaultPlexusConfiguration loggerNode = new DefaultPlexusConfiguration( "logger" );

        config.addChild( loggerNode );

        DefaultPlexusConfiguration thresholdNode = new DefaultPlexusConfiguration( "threshold" );

        loggerNode.addChild( thresholdNode );

        thresholdNode.setValue( threshold );

        return config;
    }

    protected LoggerManager createLoggerManager()
    {
        return new ConsoleLoggerManager();
    }
}
