package org.codehaus.plexus.logging;

/*
 * LICENSE
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
