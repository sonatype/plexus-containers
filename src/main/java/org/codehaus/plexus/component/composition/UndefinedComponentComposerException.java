package org.codehaus.plexus.component.composition;

/**
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class UndefinedComponentComposerException
    extends Exception
{
    public UndefinedComponentComposerException( String message )
    {
        super( message );
    }

    public UndefinedComponentComposerException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public UndefinedComponentComposerException( Throwable cause )
    {
        super( cause );
    }
}
