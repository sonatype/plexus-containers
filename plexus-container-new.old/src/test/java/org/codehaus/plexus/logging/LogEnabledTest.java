/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.plexus.logging;

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
