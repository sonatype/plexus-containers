package org.codehaus.plexus.logging;

import org.apache.avalon.framework.configuration.Configuration;

import org.codehaus.plexus.configuration.DefaultConfiguration;

/**
 * Test for {@link ConsoleLoggerManager} and {@link ConsoleLogger}.
 *
 * @author Mark H. Wilkinson
 * @version $Revision$
 */
public final class ConsoleLoggerManagerTest
    extends AbstractLoggerManagerTest
{
    protected Configuration createConfiguration(String threshold)
    {
        DefaultConfiguration config = new DefaultConfiguration( "logging" );

        DefaultConfiguration loggerNode = new DefaultConfiguration( "logger" );

        config.addChild( loggerNode );

        DefaultConfiguration thresholdNode = new DefaultConfiguration( "threshold" );

        loggerNode.addChild( thresholdNode );

        thresholdNode.setValue( threshold );

        return config;
    }

    protected LoggerManager createLoggerManager()
    {
        return new ConsoleLoggerManager();
    }
}
