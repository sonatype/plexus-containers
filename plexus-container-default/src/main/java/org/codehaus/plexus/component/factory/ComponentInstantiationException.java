package org.codehaus.plexus.component.factory;

/**
 *
 * 
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 *
 * @version $Id$
 */
public class ComponentInstantiationException
    extends Exception
{
    public ComponentInstantiationException( String message )
    {
        super( message );
    }

    public ComponentInstantiationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ComponentInstantiationException( Throwable cause )
    {
        super( cause );
    }
}
