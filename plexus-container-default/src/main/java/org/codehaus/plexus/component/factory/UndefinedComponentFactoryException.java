package org.codehaus.plexus.component.factory;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class UndefinedComponentFactoryException
    extends Exception
{
    public UndefinedComponentFactoryException( String message )
    {
        super( message );
    }

    public UndefinedComponentFactoryException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public UndefinedComponentFactoryException( Throwable cause )
    {
        super( cause );
    }
}
