package org.codehaus.plexus.component.connector;

public interface Provider
{
    void run();

    void execute();

    void setName( String name );
}
