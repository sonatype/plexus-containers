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

import junit.framework.TestCase;

/**
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision$ $Date$
 */
public class LogEnabledTest
    extends TestCase
{
    public void testGetLogger()
        throws Exception
    {
        MockLogEnabled logEnabled = new MockLogEnabled();
        MockLogger logger = new MockLogger( "base" );
        logEnabled.enableLogging( logger );
        assertEquals( "logger", logger, logEnabled.getLogger() );
    }

    public void testSetupLoggerOnLogEnabled()
        throws Exception
    {
        MockLogEnabled logEnabled = new MockLogEnabled();
        MockLogEnabled childLogEnabled = new MockLogEnabled();
        MockLogger logger = new MockLogger( "base" );
        logEnabled.enableLogging( logger );
        logEnabled.setupLogger( childLogEnabled );
        assertEquals( "logEnabled.logger", logger, logEnabled.getLogger() );
        assertEquals( "childLogEnabled.logger", logger, childLogEnabled.getLogger() );
    }

    public void testSetupLoggerOnNonLogEnabled()
        throws Exception
    {
        MockLogEnabled logEnabled = new MockLogEnabled();
        MockLogger logger = new MockLogger( "base" );
        logEnabled.enableLogging( logger );
        logEnabled.setupLogger( new Object() );
    }

    public void testSetupLoggerWithNameOnLogEnabled()
        throws Exception
    {
        MockLogEnabled logEnabled = new MockLogEnabled();
        MockLogEnabled childLogEnabled = new MockLogEnabled();
        MockLogger logger = new MockLogger( "base" );
        logEnabled.enableLogging( logger );
        logEnabled.setupLogger( childLogEnabled, "child" );
        assertEquals( "logEnabled.logger", logger, logEnabled.getLogger() );
        assertEquals( "childLogEnabled.logger.name",
                      "base.child",
                      ( (MockLogger) childLogEnabled.getLogger() ).getName() );
    }

    public void testSetupLoggerWithNullName()
        throws Exception
    {
        MockLogEnabled logEnabled = new MockLogEnabled();
        MockLogEnabled childLogEnabled = new MockLogEnabled();
        MockLogger logger = new MockLogger( "base" );
        logEnabled.enableLogging( logger );
        try
        {
            logEnabled.setupLogger( childLogEnabled, (String) null );
        }
        catch ( IllegalStateException npe )
        {
            return;
        }
        fail( "Expected to fail setting up child logger with null name" );
    }
}
