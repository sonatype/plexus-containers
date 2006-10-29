package org.codehaus.plexus.component.repository.exception;

/**
 * Exception that is thrown when the class(es) required for a component
 * implementation are not available.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentManagerImplementationNotFoundException
    extends Exception
{
    public ComponentManagerImplementationNotFoundException( String message )
    {
        super( message );
    }

    public ComponentManagerImplementationNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
