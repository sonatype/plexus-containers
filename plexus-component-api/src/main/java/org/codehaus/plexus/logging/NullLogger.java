package org.codehaus.plexus.logging;

import java.util.Hashtable;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A Null Logger. This is returned if there is no logging configured.
 * It is designed to stop NullPointerExceptions being thrown if we somehoe fail to configure our logging.
 * The class performs no logging at all but prints a warning message to the error stream stating we are not logging.
 *
 * @author Andrew Williams
 * @since 1.0-alpha-18
 * @version $Id$
 */
public class NullLogger
    implements Logger
{

    private static Hashtable instances = new Hashtable();
    private static NullLogger logger;

    private Class source;

    public static Logger getInstance()
    {
        // Do not instantiate until now, supressing the warnings unless they are valid
        if ( logger == null )
        {
            logger = new NullLogger();
        }

        return logger;
    }

    public static Logger getInstance( Class object )
    {
        if ( object == null )
        {
            return getInstance();
        }

        if ( instances.containsKey( object ) )
        {
            return (Logger) instances.get( object );
        }
        else
        {
            Logger ret = new NullLogger( object );
            instances.put( object, ret );

            return ret;
        }
    }

    protected NullLogger()
    {
        System.err.println( "Warning: No logging has been configured" );
    }

    protected NullLogger( Class object )
    {
        this.source = object;

        System.err.println( "Warning: No logging has been configured for class " + object );
    }

    public void debug( String message )
    {
        /* do nothing */
    }

    public void debug( String message,
                       Throwable throwable )
    {
        /* do nothing */
    }

    public boolean isDebugEnabled()
    {
        return false;
    }

    public void info( String message )
    {
        /* do nothing */
    }

    public void info( String message,
                      Throwable throwable )
    {
        /* do nothing */
    }

    public boolean isInfoEnabled()
    {
        return false;
    }

    public void warn( String message )
    {
        /* do nothing */
    }

    public void warn( String message,
                      Throwable throwable )
    {
        /* do nothing */
    }

    public boolean isWarnEnabled()
    {
        return false;
    }

    public void error( String message )
    {
        /* do nothing */
    }

    public void error( String message,
                       Throwable throwable )
    {
        /* do nothing */
    }

    public boolean isErrorEnabled()
    {
        return false;
    }

    public void fatalError( String message )
    {
        /* do nothing */
    }

    public void fatalError( String message,
                            Throwable throwable )
    {
        /* do nothing */
    }

    public boolean isFatalErrorEnabled()
    {
        return false;
    }

    public Logger getChildLogger( String name )
    {
        return null;
    }

    public int getThreshold()
    {
        return Logger.LEVEL_FATAL;
    }

    public void setThreshold( int threshold )
    {
        /* do nothing */
    }

    public String getName()
    {
        return "NullLogger";
    }
}
