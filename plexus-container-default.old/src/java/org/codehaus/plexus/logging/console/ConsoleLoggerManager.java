package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;


/**
 * Sample configuration.
 *
 * <pre>
 * <logging>
 *   <implementation>org.codehaus.plexus.logging.ConsoleLoggerManager</implementation>
 *   <logger>
 *     <threshold>DEBUG</threshold>
 *   </logger>
 * </logging>
 * </pre>
 */
public class ConsoleLoggerManager
    implements LoggerManager
{
    /** Message of this level or higher will be logged. */
    private int threshold;

    /** The console logger used by the manager. */
    private ConsoleLogger consoleLogger;

    public ConsoleLoggerManager()
    {
        consoleLogger = new ConsoleLogger( threshold );

        setThreshold( "info" );
    }

    public void initialize()
        throws Exception
    {
        consoleLogger = new ConsoleLogger( threshold );
    }

    public void start()
        throws Exception
    {
    }

    public void stop()
    {
    }

    public Logger getRootLogger()
    {
        return consoleLogger;
    }

    public Logger getLogger( String name )
    {
        return consoleLogger.getChildLogger( name );
    }

    public void setThreshold( String text )
    {
        if ( text.equals( "debug" ) )
        {
            threshold = ConsoleLogger.LEVEL_DEBUG;
        }
        else if ( text.equals( "info" ) )
        {
            threshold = ConsoleLogger.LEVEL_INFO;
        }
        else if ( text.equals( "warn" ) )
        {
            threshold = ConsoleLogger.LEVEL_WARN;
        }
        else if ( text.equals( "error" ) )
        {
            threshold = ConsoleLogger.LEVEL_ERROR;
        }
        else if ( text.equals( "fatal" ) )
        {
            threshold = ConsoleLogger.LEVEL_FATAL;
        }
    }
}
