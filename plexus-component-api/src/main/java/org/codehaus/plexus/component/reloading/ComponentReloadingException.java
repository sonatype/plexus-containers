package org.codehaus.plexus.component.reloading;

/**
 * @author Jason van Zyl
 * @version $Revision$
 */
public class ComponentReloadingException
    extends Exception
{
    public ComponentReloadingException( String message )
    {
        super( message );
    }

    public ComponentReloadingException( Throwable cause )
    {
        super( cause );
    }

    public ComponentReloadingException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
