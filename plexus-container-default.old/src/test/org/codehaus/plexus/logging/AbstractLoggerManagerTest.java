package org.codehaus.plexus.logging;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Abtract base class for testing implementations of the {@link LoggerManager}
 * and {@link Logger} interfaces.
 *
 * @author Mark H. Wilkinson
 * @version $Revision$
 */
public abstract class AbstractLoggerManagerTest
    extends PlexusTestCase
{
    protected abstract LoggerManager createLoggerManager()
        throws Exception;

    public void testDebugLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = managerStart( "debug" );

        Logger logger = extractRootLogger( manager );

        checkDebugLevel( logger );

        logger = extractLogger( manager );

        checkDebugLevel( logger );
    }

    public void testInfoLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = managerStart( "info" );

        Logger logger = extractRootLogger( manager );

        checkInfoLevel( logger );

        logger = extractLogger( manager );

        checkInfoLevel( logger );
    }

    public void testWarnLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = managerStart( "warn" );

        Logger logger = extractRootLogger( manager );

        checkWarnLevel( logger );

        logger = extractLogger( manager );

        checkWarnLevel( logger );
    }

    public void testErrorLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = managerStart( "error" );

        Logger logger = extractRootLogger( manager );

        checkErrorLevel( logger );

        logger = extractLogger( manager );

        checkErrorLevel( logger );
    }

    public void testFatalLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = managerStart( "fatal" );

        Logger logger = extractRootLogger( manager );

        checkFatalLevel( logger );

        logger = extractLogger( manager );

        checkFatalLevel( logger );
    }

    private LoggerManager managerStart( String threshold )
        throws Exception
    {
        LoggerManager manager = createLoggerManager();

        manager.setThreshold( threshold );

        assertNotNull( manager );

        return manager;
    }

    private Logger extractRootLogger( LoggerManager manager )
    {
        Logger logger = manager.getRootLogger();

        assertNotNull( logger );

        return logger;
    }

    private Logger extractLogger( LoggerManager manager )
    {
        Logger logger = manager.getLogger( "foo" );

        assertNotNull( logger );

        return logger;
    }

    private void checkDebugLevel( Logger logger )
    {
        assertTrue( "debug enabled", logger.isDebugEnabled() );
        assertTrue( "info enabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkInfoLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertTrue( "info enabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkWarnLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkErrorLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertFalse( "warn disabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkFatalLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertFalse( "warn disabled", logger.isWarnEnabled() );
        assertFalse( "error disabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }
}
