package org.codehaus.plexus.component.configurator;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentConfigurationException
    extends Exception
{
    public ComponentConfigurationException( String message )
    {
        super( message );
    }

    public ComponentConfigurationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ComponentConfigurationException( Throwable cause )
    {
        super( cause );
    }
}
