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

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DirectoryConfigurationResourceHandlerTest
    extends PlexusTestCase
{
    public void testFileConfigurationResourceHandler()
        throws Exception
    {
        String basedir = getBasedir();

        ConfigurationResourceHandler h = new DirectoryConfigurationResourceHandler();

        Map parameters = new HashMap();

        parameters.put( "source", new File( basedir, "src/test/resources/inline-configurations" ).getPath() );

        PlexusConfiguration[] processed = h.handleRequest( parameters );

        PlexusConfiguration p = processed[0];

        assertEquals( "jason", p.getChild( "first-name" ).getValue() );

        assertEquals( "van zyl", p.getChild( "last-name" ).getValue() );
    }
}
