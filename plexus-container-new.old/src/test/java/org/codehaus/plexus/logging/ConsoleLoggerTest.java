package org.codehaus.plexus.logging;

import junit.framework.TestCase;
import org.codehaus.plexus.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ConsoleLoggerTest
    extends TestCase
{
    public void testConsoleLogger()
    {
        ConsoleLogger logger = new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG );

        assertTrue( logger.isDebugEnabled() );

        assertTrue( logger.isInfoEnabled() );

        assertTrue( logger.isWarnEnabled() );

        assertTrue( logger.isErrorEnabled() );

        assertTrue( logger.isFatalErrorEnabled() );

        // Save the original print stream.
        PrintStream original = System.out;

        Throwable t = new Throwable( "throwable" );

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        PrintStream consoleStream = new PrintStream( os );

        System.setOut( consoleStream );


        logger.debug( "debug" );

        assertEquals( "[DEBUG] debug", getMessage( consoleStream, os ) );

        logger.debug( "debug", t );

        assertEquals( "[DEBUG] debug", getMessage( consoleStream, os ) );


        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );

        logger.info( "info" );

        assertEquals( "[INFO] info", getMessage( consoleStream, os ) );

        logger.info( "info", t );

        assertEquals( "[INFO] info", getMessage( consoleStream, os ) );


        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );

        logger.warn( "warn" );

        assertEquals( "[WARNING] warn", getMessage( consoleStream, os ) );

        logger.warn( "warn", t );

        assertEquals( "[WARNING] warn", getMessage( consoleStream, os ) );


        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );

        logger.error( "error" );

        assertEquals( "[ERROR] error", getMessage( consoleStream, os ) );

        logger.error( "error", t );

        assertEquals( "[ERROR] error", getMessage( consoleStream, os ) );


        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );

        logger.fatalError( "error" );

        assertEquals( "[FATAL ERROR] error", getMessage( consoleStream, os ) );

        logger.fatalError( "error", t );

        assertEquals( "[FATAL ERROR] error", getMessage( consoleStream, os ) );

        // Set the original print stream.
        System.setOut( original );
    }

    private String getMessage( PrintStream consoleStream, ByteArrayOutputStream os )
    {
        consoleStream.flush();

        consoleStream.close();

        return StringUtils.chopNewline( os.toString() );
    }
}
