package org.codehaus.plexus.logging.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * The default Log4J wrapper class for Logger.
 *
 * @author <a href="mailto:avalon-dev@jakarta.codehaus.org">Avalon Development Team</a>
 */
class Log4JLogger
    implements org.codehaus.plexus.logging.Logger
{
    //underlying implementation
    private Logger logger;

    /**
     * Create a logger that delegates to specified Logger.
     *
     * @param logger the Logger to delegate to
     */
    public Log4JLogger( Logger logger )
    {
        this.logger = logger;
    }

    /**
     * Log a debug message.
     *
     * @param message the message
     */
    public void debug( String message )
    {
        logger.debug( message );
    }

    /**
     * Log a debug message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public void debug( String message, Throwable throwable )
    {
        logger.debug( message, throwable );
    }

    /**
     * Determine if messages of priority "debug" will be logged.
     *
     * @return true if "debug" messages will be logged
     */
    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    /**
     * Log a info message.
     *
     * @param message the message
     */
    public void info( String message )
    {
        logger.info( message );
    }

    /**
     * Log a info message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public void info( String message, Throwable throwable )
    {
        logger.info( message, throwable );
    }

    /**
     * Determine if messages of priority "info" will be logged.
     *
     * @return true if "info" messages will be logged
     */
    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    /**
     * Log a warn message.
     *
     * @param message the message
     */
    public void warn( String message )
    {
        logger.warn( message );
    }

    /**
     * Log a warn message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public void warn( String message, Throwable throwable )
    {
        logger.warn( message, throwable );
    }

    /**
     * Determine if messages of priority "warn" will be logged.
     *
     * @return true if "warn" messages will be logged
     */
    public boolean isWarnEnabled()
    {
        return logger.isEnabledFor( Priority.WARN );
    }

    /**
     * Log a error message.
     *
     * @param message the message
     */
    public void error( String message )
    {
        logger.error( message );
    }

    /**
     * Log a error message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public void error( String message, Throwable throwable )
    {
        logger.error( message, throwable );
    }

    /**
     * Determine if messages of priority "error" will be logged.
     *
     * @return true if "error" messages will be logged
     */
    public boolean isErrorEnabled()
    {
        return logger.isEnabledFor( Priority.ERROR );
    }

    /**
     * Log a fatalError message.
     *
     * @param message the message
     */
    public void fatalError( String message )
    {
        logger.fatal( message );
    }

    /**
     * Log a fatalError message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public void fatalError( String message, Throwable throwable )
    {
        logger.fatal( message, throwable );
    }

    /**
     * Determine if messages of priority "fatalError" will be logged.
     *
     * @return true if "fatalError" messages will be logged
     */
    public boolean isFatalErrorEnabled()
    {
        return logger.isEnabledFor( Priority.FATAL );
    }

    /**
     * Create a new child logger.
     * The name of the child logger is [current-loggers-name].[passed-in-name]
     * Throws <code>IllegalArgumentException</code> if name has an empty element name
     *
     * @param name the subname of this logger
     * @return the new logger
     */
    public org.codehaus.plexus.logging.Logger getChildLogger( String name )
    {
        return new Log4JLogger( Logger.getLogger( logger.getName() + "." + name ) );
    }
}
