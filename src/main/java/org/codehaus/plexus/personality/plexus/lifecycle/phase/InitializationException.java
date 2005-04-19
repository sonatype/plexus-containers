package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/**
 * Indicates a problem occurred when initialising a component.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 */
public class InitializationException extends Exception
{
    public InitializationException( String message )
    {
        super( message );
    }

    public InitializationException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
