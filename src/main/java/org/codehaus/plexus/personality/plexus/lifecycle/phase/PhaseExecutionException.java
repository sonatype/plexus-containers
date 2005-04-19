package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/**
 * Describes an error that has occurred during the execution of a phase.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class PhaseExecutionException extends Exception
{
    public PhaseExecutionException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
