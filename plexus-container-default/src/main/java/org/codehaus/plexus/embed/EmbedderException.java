package org.codehaus.plexus.embed;

/**
 * @author Jason van Zyl
 */
public class EmbedderException
    extends Exception
{
    public EmbedderException( String id )
    {
        super( id );
    }

    public EmbedderException( String id,
                              Throwable throwable )
    {
        super( id, throwable );
    }

    public EmbedderException( Throwable throwable )
    {
        super( throwable );
    }
}
