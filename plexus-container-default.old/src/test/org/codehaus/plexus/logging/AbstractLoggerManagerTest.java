package org.codehaus.plexus.logging;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;

import junit.framework.TestCase;

/**
 * Abtract base class for testing implementations of the {@link LoggerManager}
 * and {@link Logger} interfaces.
 *
 * @author Mark H. Wilkinson
 * @version $Revision$
 */
public abstract class AbstractLoggerManagerTest
    extends TestCase
{
    public void testDebugLevelConfiguration() throws Exception
    {
        LoggerManager manager = managerStart( "debug" );

        Logger logger = extractRootLogger( manager );

        checkDebugLevel( logger );

        logger = extractLogger( manager );

        checkDebugLevel( logger );

        managerStop( manager );
    }

    public void testInfoLevelConfiguration() throws Exception
    {
        LoggerManager manager = managerStart( "info" );

        Logger logger = extractRootLogger( manager );

        checkInfoLevel(logger);

        logger = extractLogger( manager );

        checkInfoLevel( logger );

        managerStop( manager );
    }

    public void testWarnLevelConfiguration() throws Exception
    {
        LoggerManager manager = managerStart( "warn" );

        Logger logger = extractRootLogger( manager );

        checkWarnLevel(logger);

        logger = extractLogger( manager );

        checkWarnLevel( logger );

        managerStop( manager );
    }

    public void testErrorLevelConfiguration() throws Exception
    {
        LoggerManager manager = managerStart( "error" );

        Logger logger = extractRootLogger( manager );

        checkErrorLevel(logger);

        logger = extractLogger( manager );

        checkErrorLevel( logger );

        managerStop( manager );
    }

    public void testFatalLevelConfiguration() throws Exception
    {
        LoggerManager manager = managerStart( "fatal" );

        Logger logger = extractRootLogger( manager );

        checkFatalLevel(logger);

        logger = extractLogger( manager );

        checkFatalLevel( logger );

        managerStop( manager );
    }

    public void testDisabledLevelConfiguration() throws Exception
    {
        LoggerManager manager = managerStart( "disabled" );

        Logger logger = extractRootLogger( manager );

        checkDisabled(logger);

        logger = extractLogger( manager );

        checkDisabled( logger );

        managerStop( manager );
    }

    protected abstract Configuration createConfiguration(String threshold)
        throws Exception;

    protected abstract LoggerManager createLoggerManager() throws Exception;

    private LoggerManager managerStart(String threshold) throws Exception
    {
        Configuration config = createConfiguration( threshold );

        assertNotNull( config );

        LoggerManager manager = createLoggerManager();

        assertNotNull( manager );

        manager.configure( config );

        manager.initialize();

        manager.start();

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

    private void managerStop(LoggerManager manager) throws Exception
    {
        assertNotNull( manager );

        manager.stop();
    }

    private void checkDebugLevel(Logger logger) {
        assertTrue( "debug enabled", logger.isDebugEnabled() );
        assertTrue( "info enabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkInfoLevel(Logger logger) {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertTrue( "info enabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkWarnLevel(Logger logger) {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkErrorLevel(Logger logger) {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertFalse( "warn disabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkFatalLevel(Logger logger) {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertFalse( "warn disabled", logger.isWarnEnabled() );
        assertFalse( "error disabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkDisabled(Logger logger) {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertFalse( "warn disabled", logger.isWarnEnabled() );
        assertFalse( "error disabled", logger.isErrorEnabled() );
        assertFalse( "fatal disabled", logger.isFatalErrorEnabled() );
    }
}
