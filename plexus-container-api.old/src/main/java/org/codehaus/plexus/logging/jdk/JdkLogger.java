package org.codehaus.plexus.logging.jdk;

/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */

import java.util.logging.Level;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

/**
 * A logger for those who want to use the java 1.4+ logging facilities.
 * 
 * The mapping of the logging levels:
 * <ul>
 *   <li>LEVEL_DEBUG &lt;==&gt; Level.CONFIG</li>
 *   <li>LEVEL_DEBUG &lt;==&gt; Level.ALL</li>
 *   <li>LEVEL_DEBUG &lt;==&gt; Level.FINEST</li>
 *   <li>LEVEL_DEBUG &lt;==&gt; Level.FINER</li>
 *   <li>LEVEL_DEBUG &lt;==&gt; Level.FINE</li>
 *   <li>LEVEL_INFO &lt;==&gt; Level.INFO</li>
 *   <li>LEVEL_WARN &lt;==&gt; Level.WARNING</li>
 *   <li>LEVEL_ERROR &lt;==&gt; Level.SEVERE</li>
 *   <li>LEVEL_OFF &lt;==&gt; Level.OFF</li>
 * </ul>
 * I don't know if debug is the right level for config -- Trygve.
 *
 * @author <a href="www.jcontainer.org">The JContainer Group</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Revision$ $Date$
 */
public class JdkLogger
    extends AbstractLogger
{
    private java.util.logging.Logger logger;

    public JdkLogger( java.util.logging.Logger logger )
    {
        super( jdkLoggerLevelToThreshold( logger.getLevel() ), logger.getName());

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

    public void debug( String message )
    {
        logger.log( Level.FINE, message );
    }

    public void debug( String message, Throwable throwable )
    {
        logger.log( Level.FINE, message, throwable );
    }

    public void info( String message )
    {
        logger.log( Level.INFO, message );
    }

    public void info( String message, Throwable throwable )
    {
        logger.log( Level.INFO, message, throwable );
    }

    public void warn( String message )
    {
        logger.log( Level.WARNING, message );
    }

    public void warn( String message, Throwable throwable )
    {
        logger.log( Level.WARNING, message, throwable );
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

    public Logger getChildLogger( String name )
    {
        String childName = logger.getName() + "." + name;

        return new JdkLogger( java.util.logging.Logger.getLogger( childName ) );
    }

    private static int jdkLoggerLevelToThreshold( Level level )
    {
        if( level == Level.CONFIG ) 
            return LEVEL_DEBUG;
        else if( level == Level.FINEST ) 
            return LEVEL_DEBUG;
        else if( level == Level.FINER ) 
            return LEVEL_DEBUG;
        else if( level == Level.FINE ) 
            return LEVEL_DEBUG;
        else if( level == Level.INFO ) 
            return LEVEL_INFO;
        else if( level == Level.WARNING ) 
            return LEVEL_WARN;
        else if( level == Level.SEVERE ) 
            return LEVEL_ERROR;
        else if( level == Level.OFF ) 
            return LEVEL_DISABLED;
        else
            return LEVEL_DEBUG;
    }
}
