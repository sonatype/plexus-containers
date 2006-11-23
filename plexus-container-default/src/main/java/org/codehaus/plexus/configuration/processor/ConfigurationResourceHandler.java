package org.codehaus.plexus.configuration.processor;

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

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ConfigurationResourceHandler
{
    /**
     * Attribute used to identify the external configuration source.
     *
     * <pre>
     *
     * <configuration>
     *   <file-configuration-source source="/path/to/configuration.xml"/>
     * </configuration>
     *
     * </pre>
     */
    static final String SOURCE = "source";

    String getId();

    PlexusConfiguration[] handleRequest( Map parameters )
        throws ConfigurationResourceNotFoundException, ConfigurationProcessingException;
}
