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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andy@handyande.co.uk">Andrew Williams</a>
 * @version $Id: ResourceBundleConfigurationResourceHandlerTest.java 3657 $
 */
public class ResourceBundleConfigurationResourceHandlerTest
    extends TestCase
{
    public void testPropertyConfigurationResourceHandler()
        throws Exception
    {
        ResourceBundleConfigurationResourceHandler h = new ResourceBundleConfigurationResourceHandler();

        Map parameters = new HashMap();

        parameters.put( "source", "inline-configuration" );

        PlexusConfiguration[] processed = h.handleRequest( parameters );

        PlexusConfiguration p = processed[0];

        assertEquals( "andrew", p.getChild( "first-name" ).getValue() );

        assertEquals( "williams", p.getChild( "last-name" ).getValue() );
    }
}
