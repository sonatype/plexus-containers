package org.codehaus.plexus.logging;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface LoggerManager
{
    String ROLE = LoggerManager.class.getName();

    /**
     * Sets the threshold for all new loggers. It will NOT affect the existing loggers.
     *
     * This is usually only set once while the logger manager is configured.
     * 
     * @param threshold The new threshold.
     */
    void setThreshold( int threshold );

    /**
     * Returns the current threshold for all new loggers.
     *
     * @return Returns the current threshold for all new loggers.
     */
    int getThreshold();

    /**
     * Sets the threshold for all loggers. It affects all the existing loggers
     * as well as future loggers.
     *
     * @param threshold The new threshold.
     */
    void setAllThresholds( int threshold );

    // The new stuff
    void setThreshold( String role, int threshold );

    void setThreshold( String role, String roleHint, int threshold );

    int getThreshold( String role );

    int getThreshold( String role, String roleHint );

    Logger getLoggerForComponent( String role );

    Logger getLoggerForComponent( String role, String roleHint );

    void returnComponentLogger( String role );

    void returnComponentLogger( String role, String hint );

    int getActiveLoggerCount();
}
