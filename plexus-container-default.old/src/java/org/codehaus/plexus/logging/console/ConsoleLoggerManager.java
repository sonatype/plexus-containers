package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;


/**
 * Sample configuration:
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
    implements LoggerManager, Initializable
{
    /** Message of this level or higher will be logged. */
    private String threshold = "info";

    /** The console logger used by the manager. */
    private ConsoleLogger consoleLogger;

    public ConsoleLoggerManager()
    {
        initialize();
    }

    public void initialize()
    {
        consoleLogger = new ConsoleLogger( getThreshold( threshold ) );
    }

    public void setThreshold( String threshold )
    {
        this.threshold = threshold;
        consoleLogger.setLogLevel( getThreshold( threshold ) );
    }

    public String getThreshold()
    {
        return threshold;
    }

    public Logger getRootLogger()
    {
        return consoleLogger;
    }

    public Logger getLogger( String name )
    {
        return consoleLogger.getChildLogger( name );
    }

    public int getThreshold( String text )
    {
        text = text.trim().toLowerCase();

        if ( text.equals( "debug" ) )
        {
            return ConsoleLogger.LEVEL_DEBUG;
        }
        else if ( text.equals( "info" ) )
        {
            return ConsoleLogger.LEVEL_INFO;
        }
        else if ( text.equals( "warn" ) )
        {
            return ConsoleLogger.LEVEL_WARN;
        }
        else if ( text.equals( "error" ) )
        {
            return ConsoleLogger.LEVEL_ERROR;
        }
        else if ( text.equals( "fatal" ) )
        {
            return ConsoleLogger.LEVEL_FATAL;
        }

        return ConsoleLogger.LEVEL_INFO;
    }
}
