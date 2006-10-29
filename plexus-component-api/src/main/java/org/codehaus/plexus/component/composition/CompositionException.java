package org.codehaus.plexus.component.composition;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class CompositionException
    extends Exception
{

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public CompositionException( String s )
    {
        super( s );
    }

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message   The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public CompositionException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
