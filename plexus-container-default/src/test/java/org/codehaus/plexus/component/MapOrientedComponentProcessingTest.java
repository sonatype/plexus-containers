package org.codehaus.plexus.component;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.DefaultPlexusContainer;

import java.util.Map;

public class MapOrientedComponentProcessingTest
    extends TestCase
{

    public void testShouldFindAndInitializeMapOrientedComponent()
        throws Exception
    {
        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( TestMapOrientedComponent.ROLE );
        descriptor.setImplementation( TestMapOrientedComponent.ROLE );
        descriptor.setComponentComposer( "map-oriented" );
        descriptor.setComponentConfigurator( "map-oriented" );

        ComponentRequirement requirement = new ComponentRequirement();
        requirement.setFieldName( "testRequirement" );
        requirement.setRole( LoggerManager.ROLE );

        descriptor.addRequirement( requirement );

        XmlPlexusConfiguration param = new XmlPlexusConfiguration( "testParameter" );
        param.setValue( "testValue" );

        PlexusConfiguration configuration = new XmlPlexusConfiguration( "configuration" );
        configuration.addChild( param );

        descriptor.setConfiguration( configuration );

        PlexusContainer embedder = new DefaultPlexusContainer();

        embedder.addComponentDescriptor( descriptor );

        TestMapOrientedComponent component = (TestMapOrientedComponent) embedder.lookup( TestMapOrientedComponent.ROLE );

        Map context = component.getContext();

        assertTrue( "requirement (LogManager) missing from containerContext.",
                    ( context.get( "testRequirement" ) instanceof LoggerManager ) );
        
        assertEquals( "parameter missing from containerContext.", "testValue", context.get( "testParameter" ) );
    }

}
