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
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MockLoggerManager
    implements LoggerManager
{
    public void setThreshold(int threshold)
    {
    }

    public int getThreshold()
    {
        return 0;
    }

    public void setThreshold(String role, int threshold)
    {
    }

    public void setThreshold(String role, String roleHint, int threshold)
    {
    }

    public int getThreshold(String role)
    {
        return 0;
    }

    public int getThreshold(String role, String roleHint)
    {
        return 0;
    }

    public Logger getLoggerForComponent(String role)
    {
        return new MockLogger(role.getClass().getName());
    }

    public Logger getLoggerForComponent(String role, String roleHint)
    {
        return new MockLogger(role.getClass().getName() + ":" + roleHint);
    }

    public void returnComponentLogger(String role)
    {
    }

    public void returnComponentLogger(String role, String hint)
    {
    }

    public int getActiveLoggerCount()
    {
        return 0;
    }
}
