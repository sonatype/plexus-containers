package org.codehaus.plexus.logging;



public interface LoggerManager
{
    String ROLE = LoggerManager.class.getName();

    void setThreshold( String threshold );

    String getThreshold();

    Logger getRootLogger();

    Logger getLogger( String name );
}
