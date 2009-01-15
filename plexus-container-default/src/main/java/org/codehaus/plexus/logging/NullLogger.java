package org.codehaus.plexus.logging;

public class NullLogger extends AbstractLogger
{
    public NullLogger( )
    {
        super( LEVEL_DISABLED, null );
    }

    public void debug( String message, Throwable throwable )
    {
    }

    public void info( String message, Throwable throwable )
    {
    }

    public void warn( String message, Throwable throwable )
    {
    }

    public void error( String message, Throwable throwable )
    {
    }

    public void fatalError( String message, Throwable throwable )
    {
    }

    public Logger getChildLogger( String name )
    {
        return this;
    }
}
