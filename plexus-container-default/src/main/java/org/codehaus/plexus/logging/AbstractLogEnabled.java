package org.codehaus.plexus.logging;

import org.codehaus.plexus.logging.console.ConsoleLogger;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLogEnabled
    implements LogEnabled
{
    private Logger logger;

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected void setupLogger( Object component )
    {
        setupLogger( component, logger );
    }

    protected void setupLogger( Object component, String subCategory )
    {
        if ( subCategory == null )
        {
            throw new IllegalStateException( "Logging category must be defined." );
        }

        Logger logger = this.logger.getChildLogger( subCategory );

        setupLogger( component, logger );
    }

    protected void setupLogger( Object component, Logger logger )
    {
        if ( component instanceof LogEnabled )
        {
            ( (LogEnabled) component ).enableLogging( logger );
        }
    }
}
