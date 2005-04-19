package org.codehaus.plexus;

/**
 * Exception in the container.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class ArtifactEnabledContainerException extends Exception
{
    public ArtifactEnabledContainerException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
