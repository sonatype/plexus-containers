package org.codehaus.plexus.logging;

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

    public void setThreshold( int threshold )
    {
    }
}
