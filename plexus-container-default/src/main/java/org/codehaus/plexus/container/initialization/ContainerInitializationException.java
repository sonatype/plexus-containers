package org.codehaus.plexus.container.initialization;

/**
 * @author Jason van Zyl
 */
public class ContainerInitializationException
    extends Exception
{
    public ContainerInitializationException( String id )
    {
        super( id );
    }

    public ContainerInitializationException( String id,
                                             Throwable throwable )
    {
        super( id, throwable );
    }

    public ContainerInitializationException( Throwable throwable )
    {
        super( throwable );
    }
}
