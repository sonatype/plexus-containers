/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.plexus.logging;


/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision$ $Date$
 */
class MockLogger
    implements Logger
{
    private final String m_name;

    MockLogger( String name )
    {
        m_name = name;
    }

    public String getName()
    {
        return m_name;
    }

    public Logger getChildLogger( final String name )
    {
        return new MockLogger( getName() + "." + name );
    }

    public void trace( String message )
    {
    }

    public void trace( String message, Throwable throwable )
    {
    }

    public boolean isTraceEnabled()
    {
        return false;
    }

    public void debug( String message )
    {
    }

    public void debug( String message, Throwable throwable )
    {
    }

    public boolean isDebugEnabled()
    {
        return false;
    }

    public void info( String message )
    {
    }

    public void info( String message, Throwable throwable )
    {
    }

    public boolean isInfoEnabled()
    {
        return false;
    }

    public void warn( String message )
    {
    }

    public void warn( String message, Throwable throwable )
    {
    }

    public boolean isWarnEnabled()
    {
        return false;
    }

    public boolean isFatalErrorEnabled()
    {
        return false;
    }

    public void fatalError( String message )
    {
    }

    public void fatalError( String message, Throwable throwable )
    {
    }

    public void error( String message )
    {
    }

    public void error( String message, Throwable throwable )
    {
    }

    public boolean isErrorEnabled()
    {
        return false;
    }

    public int getThreshold()
    {
        return 0;
    }
}
