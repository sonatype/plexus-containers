package org.codehaus.plexus.logging;

/**
 * Test for {@link org.codehaus.plexus.logging.console.ConsoleLoggerManager} and {@link org.codehaus.plexus.logging.console.ConsoleLogger}.
 *
 * @author Mark H. Wilkinson
 * @version $Revision$
 */
public final class ConsoleLoggerManagerTest
    extends AbstractLoggerManagerTest
{
    protected LoggerManager createLoggerManager() throws Exception
    {
        return (LoggerManager)lookup(LoggerManager.ROLE);
    }
}
