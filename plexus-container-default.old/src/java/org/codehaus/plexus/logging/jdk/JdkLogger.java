package org.codehaus.plexus.logging.jdk;

/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */

import org.codehaus.plexus.logging.Logger;

import java.util.logging.Level;

/**
 * Logging facade implmentation for JDK1.4 logging toolkit.
 * The following lists the mapping between DNA log levels
 * and JDK1.4 log levels.
 *
 * <ul>
 *   <li>trace ==&gt; finest</li>
 *   <li>debug ==&gt; fine</li>
 *   <li>info ==&gt; info</li>
 *   <li>warn ==&gt; warning</li>
 *   <li>error ==&gt; severe</li>
 * </ul>
 *
 * @version $Revision$ $Date$
 */
public class JdkLogger
    implements Logger
{
    private java.util.logging.Logger logger;

    public JdkLogger( java.util.logging.Logger logger )
    {
        if ( null == logger )
        {
            throw new NullPointerException( "logger" );
        }
        this.logger = logger;
    }

    public void trace( String message )
    {
        logger.log( Level.FINEST, message );
    }

    public void trace( String message, Throwable throwable )
    {
        logger.log( Level.FINEST, message, throwable );
    }

    public boolean isTraceEnabled()
    {
        return logger.isLoggable( Level.FINEST );
    }

    public void debug( String message )
    {
        logger.log( Level.FINE, message );
    }

    public void debug( String message, Throwable throwable )
    {
        logger.log( Level.FINE, message, throwable );
    }

    public boolean isDebugEnabled()
    {
        return logger.isLoggable( Level.FINE );
    }

    public void info( String message )
    {
        logger.log( Level.INFO, message );
    }

    public void info( String message, Throwable throwable )
    {
        logger.log( Level.INFO, message, throwable );
    }

    public boolean isInfoEnabled()
    {
        return logger.isLoggable( Level.INFO );
    }

    public void warn( String message )
    {
        logger.log( Level.WARNING, message );
    }

    public void warn( String message, Throwable throwable )
    {
        logger.log( Level.WARNING, message, throwable );
    }

    public boolean isWarnEnabled()
    {
        return logger.isLoggable( Level.WARNING );
    }

    public boolean isFatalErrorEnabled()
    {
        return isErrorEnabled();
    }

    public void fatalError( String message )
    {
        error( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        error( message, throwable );
    }

    public void error( String message )
    {
        logger.log( Level.SEVERE, message );
    }

    public void error( String message, Throwable throwable )
    {
        logger.log( Level.SEVERE, message, throwable );
    }

    public boolean isErrorEnabled()
    {
        return logger.isLoggable( Level.SEVERE );
    }

    public Logger getChildLogger( String name )
    {
        String childName = logger.getName() + "." + name;

        return new JdkLogger( java.util.logging.Logger.getLogger( childName ) );
    }
}
