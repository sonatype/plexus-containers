package org.codehaus.plexus;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.configuration.ConfigurationResourceException;

import java.io.Reader;

public interface PlexusContainer
{
    Object lookup( String componentKey )
        throws ServiceException;

    Object lookup( String role, String id )
        throws ServiceException;

    boolean hasService( String componentKey );

    boolean hasService( String role, String id );

    void release( Object component );

    void suspend( Object component );

    void resume( Object component );

    void addContextValue( Object key, Object value );

    void setClassWorld( ClassWorld classWorld );

    void setClassLoader( ClassLoader classLoader );

    void setConfigurationResource( Reader configuration )
        throws ConfigurationResourceException;

    ClassLoader getClassLoader();

    void initialize()
        throws Exception;

    void start()
        throws Exception;

    void dispose()
        throws Exception;
}
