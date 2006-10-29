package org.codehaus.plexus.component.repository.exception;

/**
 * The exception which is thrown by a component repository when
 * the requested component cannot be found.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentLookupException
    extends Exception
{
    public ComponentLookupException( String message )
    {
        super( message );
    }

    public ComponentLookupException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
