/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */

package org.codehaus.plexus.logging;

import org.apache.avalon.framework.logger.Logger;

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

    private final int m_logLevel;

    /**
     * Creates a new ConsoleLogger with the priority set to DEBUG.
     */
    public ConsoleLogger()
    {
        this( LEVEL_DEBUG );
    }

    /**
     * Creates a new ConsoleLogger.
     * @param logLevel log level typecode
     */
    public ConsoleLogger( final int logLevel )
    {
        m_logLevel = logLevel;
    }

    /**
     * Logs a debugging message.
     *
     * @param message a <code>String</code> value
     */
    public void debug( final String message )
    {
        debug( message, null );
    }

    /**
     * Logs a debugging message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void debug( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_DEBUG )
        {
            System.out.print( "[DEBUG] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if debug-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if debug-level logging
     */
    public boolean isDebugEnabled()
    {
        return m_logLevel <= LEVEL_DEBUG;
    }

    /**
     * Logs an informational message.
     *
     * @param message a <code>String</code> value
     */
    public void info( final String message )
    {
        info( message, null );
    }

    /**
     * Logs an informational message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void info( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_INFO )
        {
            System.out.print( "[INFO] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if info-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if info-level logging is enabled
     */
    public boolean isInfoEnabled()
    {
        return m_logLevel <= LEVEL_INFO;
    }

    /**
     * Logs a warning message.
     *
     * @param message a <code>String</code> value
     */
    public void warn( final String message )
    {
        warn( message, null );
    }

    /**
     * Logs a warning message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void warn( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_WARN )
        {
            System.out.print( "[WARNING] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if warn-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if warn-level logging is enabled
     */
    public boolean isWarnEnabled()
    {
        return m_logLevel <= LEVEL_WARN;
    }

    /**
     * Logs an error message.
     *
     * @param message a <code>String</code> value
     */
    public void error( final String message )
    {
        error( message, null );
    }

    /**
     * Logs an error message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void error( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_ERROR )
        {
            System.out.print( "[ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if error-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if error-level logging is enabled
     */
    public boolean isErrorEnabled()
    {
        return m_logLevel <= LEVEL_ERROR;
    }

    /**
     * Logs a fatal error message.
     *
     * @param message a <code>String</code> value
     */
    public void fatalError( final String message )
    {
        fatalError( message, null );
    }

    /**
     * Logs a fatal error message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void fatalError( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_FATAL )
        {
            System.out.print( "[FATAL ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if fatal-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if fatal-level logging is enabled
     */
    public boolean isFatalErrorEnabled()
    {
        return m_logLevel <= LEVEL_FATAL;
    }

    /**
     * Just returns this logger (<code>ConsoleLogger</code> is not hierarchical).
     *
     * @param name ignored
     * @return this logger
     */
    public Logger getChildLogger( final String name )
    {
        return this;
    }
}
