package org.codehaus.plexus.logging;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;

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
    /** XML element used to start the logger configuration. */
    private static final String LOGGER_TAG = "logger";

    /** XML element used to set the threshold of the console logger. */
    private static final String THRESHOLD_TAG = "threshold";

    /** Message of this level or higher will be logged. */
    private int thresholdLevel;
    
    /** The console logger used by the manager. */
    private ConsoleLogger consoleLogger;

    public void configure( Configuration configuration )
    {
        setThresholdLevel(
            configuration
                .getChild( LOGGER_TAG )
                .getChild( THRESHOLD_TAG )
                .getValue( "info" )
                .trim()
                .toLowerCase() );
    }

    public void initialize()
        throws Exception
    {
        consoleLogger = new ConsoleLogger( thresholdLevel );
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

    /**
     * Sets the threshold for the console logger created by this
     * manager.
     *
     * @param text The threshold level specified as a string which can
     * be one of the following: debug, info, warn, error, fatal,
     * disabled.
     */
    private void setThresholdLevel( String text )
    {
        if ( text.equals( "debug" ) )
        {
            thresholdLevel = ConsoleLogger.LEVEL_DEBUG;
        }
        else if ( text.equals( "info" ) )
        {
            thresholdLevel = ConsoleLogger.LEVEL_INFO;            
        }
        else if ( text.equals( "warn" ) )
        {
            thresholdLevel = ConsoleLogger.LEVEL_WARN;
        }
        else if ( text.equals( "error" ) )
        {
            thresholdLevel = ConsoleLogger.LEVEL_ERROR;
        }
        else if ( text.equals( "fatal" ) )
        {
            thresholdLevel = ConsoleLogger.LEVEL_FATAL;
        }
        else if ( text.equals( "disabled" ) )
        {
            thresholdLevel = ConsoleLogger.LEVEL_DISABLED;
        }
    }
}
