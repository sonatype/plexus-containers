/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.plexus.logging.log4j;

import junit.framework.TestCase;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4JLoggerTest
    extends TestCase
{
    public void testLog4JLoggerEmptyCtor()
        throws Exception
    {
        try
        {
            new Log4JLogger( null );
        }
        catch( NullPointerException npe )
        {
            assertEquals( "npe.getMessage()", "logger", npe.getMessage() );
        }
    }

    public void testLog4JLoggerGetChildLogger()
        throws Exception
    {
        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, Level.DEBUG );

        assertNotSame( "logger.getChildLogger == logger",
                       logger,
                       logger.getChildLogger( "whatever" ) );
    }

    public void testLog4JLoggerDebugEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.DEBUG;
        final String message = "Meep!";
        final Throwable throwable = null;
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.debug( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerDebugDisabled()
        throws Exception
    {
        final Level level = Level.ERROR;
        final String message = "Meep!";

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.debug( message );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerDebugWithExceptionEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.DEBUG;
        final String message = "Meep!";
        final Throwable throwable = new Throwable();
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.debug( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerDebugWithExceptionDisabled()
        throws Exception
    {
        final Level level = Level.ERROR;
        final String message = "Meep!";
        final Throwable throwable = new Throwable();

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.debug( message, throwable );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerInfoEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.INFO;
        final String message = "Meep!";
        final Throwable throwable = null;
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.info( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerInfoDisabled()
        throws Exception
    {
        final Level level = Level.ERROR;
        final String message = "Meep!";

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.info( message );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerInfoWithExceptionEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.INFO;
        final String message = "Meep!";
        final Throwable throwable = new Throwable();
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.info( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerInfoWithExceptionDisabled()
        throws Exception
    {
        final Level level = Level.ERROR;
        final String message = "Meep!";
        final Throwable throwable = new Throwable();

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.info( message, throwable );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerWarnEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.WARN;
        final String message = "Meep!";
        final Throwable throwable = null;
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.warn( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerWarnDisabled()
        throws Exception
    {
        final Level level = Level.ERROR;
        final String message = "Meep!";

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.warn( message );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerWarnWithExceptionEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.WARN;
        final String message = "Meep!";
        final Throwable throwable = new Throwable();
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.warn( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerWarnWithExceptionDisabled()
        throws Exception
    {
        final Level level = Level.ERROR;
        final String message = "Meep!";
        final Throwable throwable = new Throwable();

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.warn( message, throwable );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerErrorEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.ERROR;
        final String message = "Meep!";
        final Throwable throwable = null;
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.error( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerErrorWithExceptionEnabled()
        throws Exception
    {
        final Level level = Level.DEBUG;
        final Level type = Level.ERROR;
        final String message = "Meep!";
        final Throwable throwable = new Throwable();
        final boolean output = true;

        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, level );
        logger.error( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testConsoleLevelComparisonWithDebugEnabled()
        throws Exception
    {
        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, Level.DEBUG );

        //assertEquals( "logger.isTraceEnabled()", true, logger.isTraceEnabled() );
        assertEquals( "logger.isDebugEnabled()", true, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", true, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", true, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
    }

    public void testConsoleLevelComparisonWithInfoEnabled()
        throws Exception
    {
        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, Level.INFO );

        //assertEquals( "logger.isTraceEnabled()", false, logger.isTraceEnabled() );
        assertEquals( "logger.isDebugEnabled()", false, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", true, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", true, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
    }

    public void testConsoleLevelComparisonWithWarnEnabled()
        throws Exception
    {
        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, Level.WARN );

        //assertEquals( "logger.isTraceEnabled()", false, logger.isTraceEnabled() );
        assertEquals( "logger.isDebugEnabled()", false, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", false, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", true, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
    }

    public void testConsoleLevelComparisonWithErrorEnabled()
        throws Exception
    {
        final MockAppender target = new MockAppender();
        final Log4JLogger logger = createLogger( target, Level.ERROR );

        //assertEquals( "logger.isTraceEnabled()", false, logger.isTraceEnabled() );
        assertEquals( "logger.isDebugEnabled()", false, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", false, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", false, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
    }

    private Log4JLogger createLogger( final Appender target,
                                      final Level priority )
    {
        final Logger log4jLogger = Logger.getLogger( "test" );
        log4jLogger.removeAllAppenders();
        log4jLogger.addAppender( target );
        log4jLogger.setLevel( priority );
        return new Log4JLogger( log4jLogger );
    }

    private void checkLogger( final MockAppender target,
                              final boolean output,
                              final String message,
                              final Throwable throwable,
                              final Level priority )
    {
        assertEquals( "logger.m_message == message", message, target.m_message );
        assertEquals( "logger.m_output == output", output, target.m_output );
        assertEquals( "logger.m_throwable == null", throwable, target.m_throwable );
        assertEquals( "logger.m_priority == null", priority, target.m_priority );
    }
}
