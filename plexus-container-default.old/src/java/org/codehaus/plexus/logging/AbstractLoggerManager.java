package org.codehaus.plexus.logging;




public abstract class AbstractLoggerManager
    implements LoggerManager
{
    public abstract Logger getRootLogger();

    public abstract Logger getLogger( String name );

    public abstract void initialize()
        throws Exception;

    public abstract void start()
        throws Exception;

    public abstract void stop()
        throws Exception;


}
