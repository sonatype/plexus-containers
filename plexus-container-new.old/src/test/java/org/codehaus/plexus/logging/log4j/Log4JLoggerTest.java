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
        catch ( NullPointerException npe )
        {
            assertEquals( "npe.getMessage()", "logger", npe.getMessage() );
        }
    }

    public void testLog4JLoggerGetChildLogger()
        throws Exception
    {
        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, Level.DEBUG );

        assertNotSame( "logger.getChildLogger == logger",
                       logger,
                       logger.getChildLogger( "whatever" ) );
    }

    public void testLog4JLoggerFatalEnabled()
        throws Exception
    {
        Level level = Level.FATAL;
        Level type = Level.FATAL;
        String message = "Meep!";
        Throwable throwable = null;
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.fatalError( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerFatalWithExceptionEnabled()
        throws Exception
    {
        Level level = Level.FATAL;
        Level type = Level.FATAL;
        String message = "Meep!";
        Throwable throwable = new Throwable();
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.fatalError( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }


    public void testLog4JLoggerDebugEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.DEBUG;
        String message = "Meep!";
        Throwable throwable = null;
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.debug( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerDebugDisabled()
        throws Exception
    {
        Level level = Level.ERROR;
        String message = "Meep!";

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.debug( message );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerDebugWithExceptionEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.DEBUG;
        String message = "Meep!";
        Throwable throwable = new Throwable();
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.debug( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerDebugWithExceptionDisabled()
        throws Exception
    {
        Level level = Level.ERROR;
        String message = "Meep!";
        Throwable throwable = new Throwable();

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.debug( message, throwable );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerInfoEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.INFO;
        String message = "Meep!";
        Throwable throwable = null;
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.info( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerInfoDisabled()
        throws Exception
    {
        Level level = Level.ERROR;
        String message = "Meep!";

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.info( message );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerInfoWithExceptionEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.INFO;
        String message = "Meep!";
        Throwable throwable = new Throwable();
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.info( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerInfoWithExceptionDisabled()
        throws Exception
    {
        Level level = Level.ERROR;
        String message = "Meep!";
        Throwable throwable = new Throwable();

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.info( message, throwable );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerWarnEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.WARN;
        String message = "Meep!";
        Throwable throwable = null;
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.warn( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerWarnDisabled()
        throws Exception
    {
        Level level = Level.ERROR;
        String message = "Meep!";

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.warn( message );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerWarnWithExceptionEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.WARN;
        String message = "Meep!";
        Throwable throwable = new Throwable();
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.warn( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerWarnWithExceptionDisabled()
        throws Exception
    {
        Level level = Level.ERROR;
        String message = "Meep!";
        Throwable throwable = new Throwable();

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.warn( message, throwable );
        checkLogger( target, false, null, null, null );
    }

    public void testLog4JLoggerErrorEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.ERROR;
        String message = "Meep!";
        Throwable throwable = null;
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.error( message );
        checkLogger( target, output, message, throwable, type );
    }

    public void testLog4JLoggerErrorWithExceptionEnabled()
        throws Exception
    {
        Level level = Level.DEBUG;
        Level type = Level.ERROR;
        String message = "Meep!";
        Throwable throwable = new Throwable();
        boolean output = true;

        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, level );
        logger.error( message, throwable );
        checkLogger( target, output, message, throwable, type );
    }

    public void testConsoleLevelComparisonWithDebugEnabled()
        throws Exception
    {
        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, Level.DEBUG );

        assertEquals( "logger.isDebugEnabled()", true, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", true, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", true, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
        assertEquals( "logger.isTraceEnabled()", true, logger.isFatalErrorEnabled() );
    }

    public void testConsoleLevelComparisonWithInfoEnabled()
        throws Exception
    {
        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, Level.INFO );

        assertEquals( "logger.isDebugEnabled()", false, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", true, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", true, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
        assertEquals( "logger.isTraceEnabled()", true, logger.isFatalErrorEnabled() );
    }

    public void testConsoleLevelComparisonWithWarnEnabled()
        throws Exception
    {
        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, Level.WARN );

        //assertEquals( "logger.isTraceEnabled()", false, logger.isTraceEnabled() );
        assertEquals( "logger.isDebugEnabled()", false, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", false, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", true, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
    }

    public void testConsoleLevelComparisonWithErrorEnabled()
        throws Exception
    {
        MockAppender target = new MockAppender();
        Log4JLogger logger = createLogger( target, Level.ERROR );

        //assertEquals( "logger.isTraceEnabled()", false, logger.isTraceEnabled() );
        assertEquals( "logger.isDebugEnabled()", false, logger.isDebugEnabled() );
        assertEquals( "logger.isInfoEnabled()", false, logger.isInfoEnabled() );
        assertEquals( "logger.isWarnEnabled()", false, logger.isWarnEnabled() );
        assertEquals( "logger.isErrorEnabled()", true, logger.isErrorEnabled() );
    }

    private Log4JLogger createLogger( Appender target,
                                      Level priority )
    {
        Logger log4jLogger = Logger.getLogger( "test" );
        log4jLogger.removeAllAppenders();
        log4jLogger.addAppender( target );
        log4jLogger.setLevel( priority );
        return new Log4JLogger( log4jLogger );
    }

    private void checkLogger( MockAppender target,
                              boolean output,
                              String message,
                              Throwable throwable,
                              Level priority )
    {
        assertEquals( "logger.m_message == message", message, target.m_message );
        assertEquals( "logger.m_output == output", output, target.m_output );
        assertEquals( "logger.m_throwable == null", throwable, target.m_throwable );
        assertEquals( "logger.m_priority == null", priority, target.m_priority );
    }
}
