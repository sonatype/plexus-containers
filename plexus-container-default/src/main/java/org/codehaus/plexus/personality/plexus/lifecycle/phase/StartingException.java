package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/**
 * Error occuring while starting a component.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class StartingException
    extends Exception
{
    public StartingException( String message )
    {
        super( message );
    }

    public StartingException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
