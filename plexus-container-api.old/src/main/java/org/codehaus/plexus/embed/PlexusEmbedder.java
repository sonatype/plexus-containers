/* Created on Oct 7, 2004 */
package org.codehaus.plexus.embed;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author jdcasey
 */
public interface PlexusEmbedder
{

    PlexusContainer getContainer();

    Object lookup( String role ) throws ComponentLookupException;

    Object lookup( String role, String id ) throws ComponentLookupException;

    boolean hasComponent( String role );

    boolean hasComponent( String role, String id );

    void release( Object service ) throws Exception;

    void setClassWorld( ClassWorld classWorld );

    void setConfiguration( URL configuration );

    void addContextValue( Object key, Object value );

    void setProperties( Properties properties );

    void setProperties( File file );

    void start( ClassWorld classWorld ) throws Exception;

    void start() throws Exception;

    void stop() throws Exception;
    
}