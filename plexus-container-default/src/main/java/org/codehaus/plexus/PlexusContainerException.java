package org.codehaus.plexus;

/**
 * Container execution exception.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class PlexusContainerException extends Exception
{
    public PlexusContainerException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
    
    public PlexusContainerException( String message )
    {
        super( message );
    }
}
