package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.Logger;


/**
 * Logger sending everything to the standard output streams.
 * This is mainly for the cases when you have a utility that
 * does not have a logger to supply.
 *
 * @author <a href="mailto:dev@avalon.codehaus.org">Avalon Development Team</a>
 * @version CVS $Revision$ $Date$
 */
public final class ConsoleLogger
    implements Logger
{
    /** Typecode for debugging messages. */
    public static final int LEVEL_DEBUG = 0;

    /** Typecode for informational messages. */
    public static final int LEVEL_INFO = 1;

    /** Typecode for warning messages. */
    public static final int LEVEL_WARN = 2;

    /** Typecode for error messages. */
    public static final int LEVEL_ERROR = 3;

    /** Typecode for fatal error messages. */
    public static final int LEVEL_FATAL = 4;

    /** Typecode for disabled log levels. */
    public static final int LEVEL_DISABLED = 5;

    private int logLevel;

    public ConsoleLogger()
    {
        this( LEVEL_DEBUG );
    }

    public ConsoleLogger( int logLevel )
    {
        this.logLevel = logLevel;
    }

    public void debug( String message )
    {
        debug( message, null );
    }

    public void debug( String message, Throwable throwable )
    {
        if ( logLevel <= LEVEL_DEBUG )
        {
            System.out.print( "[DEBUG] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isDebugEnabled()
    {
        return logLevel <= LEVEL_DEBUG;
    }

    public void info( String message )
    {
        info( message, null );
    }

    public void info( String message, Throwable throwable )
    {
        if ( logLevel <= LEVEL_INFO )
        {
            System.out.print( "[INFO] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isInfoEnabled()
    {
        return logLevel <= LEVEL_INFO;
    }

    public void warn( String message )
    {
        warn( message, null );
    }

    public void warn( String message, Throwable throwable )
    {
        if ( logLevel <= LEVEL_WARN )
        {
            System.out.print( "[WARNING] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isWarnEnabled()
    {
        return logLevel <= LEVEL_WARN;
    }

    public void error( String message )
    {
        error( message, null );
    }

    public void error( String message, Throwable throwable )
    {
        if ( logLevel <= LEVEL_ERROR )
        {
            System.out.print( "[ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isErrorEnabled()
    {
        return logLevel <= LEVEL_ERROR;
    }

    public void fatalError( String message )
    {
        fatalError( message, null );
    }

    public void fatalError( String message, Throwable throwable )
    {
        if ( logLevel <= LEVEL_FATAL )
        {
            System.out.print( "[FATAL ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isFatalErrorEnabled()
    {
        return logLevel <= LEVEL_FATAL;
    }

    public void setLogLevel( int logLevel )
    {
        this.logLevel = logLevel;
    }

    public Logger getChildLogger( String name )
    {
        return this;
    }
}
