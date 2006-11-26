package org.codehaus.plexus.embed;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    void setClassWorld( ClassWorld classWorld );

    void setConfiguration( URL configuration ) throws IOException;

    void setConfiguration( Reader configuration ) throws IOException;

    void addContextValue( Object key, Object value );

    void setProperties( Properties properties );

    void setProperties( File file );

    void start( ClassWorld classWorld )
        throws PlexusContainerException, PlexusConfigurationResourceException;

    void start()
        throws PlexusContainerException, PlexusConfigurationResourceException;

    void stop();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void setLoggerManager( LoggerManager loggerManager );    
}
