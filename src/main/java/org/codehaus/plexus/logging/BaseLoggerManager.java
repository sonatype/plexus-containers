package org.codehaus.plexus.logging;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class for all LoggerManagers which use cache of Loggers.
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class BaseLoggerManager
        extends AbstractLoggerManager implements Initializable
{
    /** */
    private Map loggerCache = new HashMap();

    private String threshold = "info";

    private int currentThreshold;

    public void initialize()
    {
        currentThreshold = parseThreshold( threshold );

        if ( currentThreshold == -1 )
        {
            currentThreshold = Logger.LEVEL_DEBUG;
        }
    }

    protected int parseThreshold( String text )
    {
        text = text.trim().toLowerCase();

        if ( text.equals( "debug" ) )
        {
            return Logger.LEVEL_DEBUG;
        }
        else if ( text.equals( "info" ) )
        {
            return Logger.LEVEL_INFO;
        }
        else if ( text.equals( "warn" ) )
        {
            return Logger.LEVEL_WARN;
        }
        else if ( text.equals( "error" ) )
        {
            return Logger.LEVEL_ERROR;
        }
        else if ( text.equals( "fatal" ) )
        {
            return Logger.LEVEL_FATAL;
        }

        return -1;
    }

    /**
     * Sets the threshold for all new loggers. It will NOT affect the existing loggers.
     * <p/>
     * This is usually only set once while the logger manager is configured.
     *
     * @param currentThreshold The new threshold.
     */
    public void setThreshold( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;
    }

    /**
     * Sets the threshold for all new loggers. It will NOT affect the existing loggers.
     * <p/>
     * This is usually only set once while the logger manager is configured.
     *
     * @param currentThreshold The new threshold.
     */
    public void setThresholds( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;

        for ( Iterator logs = loggerCache.values().iterator(); logs.hasNext(); )
        {
            Logger logger = (Logger) logs.next();
            logger.setThreshold( currentThreshold );
        }
    }

    /**
     * Returns the current threshold for all new loggers.
     *
     * @return Returns the current threshold for all new loggers.
     */
    public int getThreshold()
    {
        return currentThreshold;
    }

    public void setThreshold( String role, String roleHint, int threshold )
    {
        AbstractLogger logger;

        String key = toMapKey( role, roleHint );

        logger = ( AbstractLogger ) loggerCache.get( key );

        if ( logger == null )
        {
            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint )
    {
        AbstractLogger logger;

        String key = toMapKey( role, roleHint );

        logger = ( AbstractLogger ) loggerCache.get( key );

        if ( logger == null )
        {
            return Logger.LEVEL_DEBUG; // does not return null because that could create a NPE
        }

        return logger.getThreshold();
    }

    public Logger getLoggerForComponent( String role, String roleHint )
    {
        Logger logger;

        String key = toMapKey( role, roleHint );

        logger = ( Logger ) loggerCache.get( key );

        if ( logger != null )
        {
            return logger;
        }

        logger = createLogger( key );

        loggerCache.put( key, logger );

        return logger;
    }

    protected abstract Logger createLogger( String key );

    public void returnComponentLogger( String role, String roleHint )
    {
        Object obj;

        String key = toMapKey( role, roleHint );

        obj = loggerCache.remove( key );

        if ( obj == null )
        {
            // TODO: use a logger!
            System.err.println( "There was no such logger '" + key + "' " + hashCode() + "." );
        }
    }

    public int getActiveLoggerCount()
    {
        return loggerCache.size();
    }

    public String getThresholdAsString()
    {
        return threshold;
    }

}
