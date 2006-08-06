/* Created on Oct 7, 2004 */
package org.codehaus.plexus.embed;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.logging.LoggerManager;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

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

    void release( Object service )
        throws ComponentLifecycleException;

    void stop();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void setLoggerManager( LoggerManager loggerManager );    
}
