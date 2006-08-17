package org.codehaus.plexus.component.composition;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test that setters and fields are correctly injected, using setter injection when needed.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class SetterCompositionTest
    extends PlexusTestCase
{

    public void testLoadPlexus()
        throws Exception
    {
        SetterCompositionChildClass c = (SetterCompositionChildClass) lookup( "test" );

        assertNotNull( "component not created", c );
        assertNotNull( "configuration not injected", c.getX() );
        assertNotNull( "requirement not injected", c.getY() );

        assertEquals( "wrong configuration injected", c.getX(), "test" );
        assertEquals( "wrong requirement injected", c.getY(), "" );
    }
}
