package org.codehaus.plexus.logging;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
}
