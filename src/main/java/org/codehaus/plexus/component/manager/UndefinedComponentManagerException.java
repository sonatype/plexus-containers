package org.codehaus.plexus.component.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class UndefinedComponentManagerException
    extends Exception
{
    public UndefinedComponentManagerException( String message )
    {
        super( message );
    }
}
