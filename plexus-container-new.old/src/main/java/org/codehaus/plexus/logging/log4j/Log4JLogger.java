package org.codehaus.plexus.logging.log4j;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 * ----------------------------------------------------------------------------
 */


import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * The default Log4J wrapper class for Logger.
 *
 * @author <a href="mailto:avalon-dev@jakarta.codehaus.org">Avalon Development Team</a>
 */
class Log4JLogger
    implements org.apache.avalon.framework.logger.Logger
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
    public org.apache.avalon.framework.logger.Logger getChildLogger( String name )
    {
        return new Log4JLogger( Logger.getLogger( logger.getName() + "." + name ) );
    }
}
