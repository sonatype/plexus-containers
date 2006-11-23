package org.codehaus.plexus.component.composition.autowire;

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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.composition.setter.SetterComponentComposer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class AutowireCompositionTest
    extends PlexusTestCase
{
    public void testSetterAutowireUsingSetterComponentComposer()
        throws Exception
    {
        ComponentComposer composer = new SetterComponentComposer();

        Autowire autowire = new Autowire();

        composer.assembleComponent( autowire, null, getContainer() );

        assertNotNull( autowire.getOne() );

        assertNotNull( autowire.getTwo() );
    }

    public void testAutoWireUsingContainer()
        throws Exception
    {
        PlexusContainer container = getContainer();

        Autowire autowire = new Autowire();

        autowire = (Autowire) container.autowire( autowire );

        assertNotNull( autowire.getOne() );

        assertNotNull( autowire.getTwo() );
    }
}
