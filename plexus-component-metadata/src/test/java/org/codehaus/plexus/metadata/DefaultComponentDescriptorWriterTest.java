/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.metadata;

import java.io.StringWriter;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.metadata.ComponentDescriptorWriter;
import org.codehaus.plexus.metadata.DefaultComponentDescriptorWriter;

/**
 * Test for the {@link DefaultComponentDescriptorWriter} class.
 *
 * @version $Rev$ $Date$
 */
public class DefaultComponentDescriptorWriterTest
    extends PlexusTestCase
{
    private DefaultComponentDescriptorWriter descriptorWriter;

    // @Override
    protected void setUp() throws Exception {
        super.setUp();

        descriptorWriter = (DefaultComponentDescriptorWriter) lookup(ComponentDescriptorWriter.class);
        assertNotNull(descriptorWriter);
    }

    // @Override
    protected void tearDown() throws Exception {
        descriptorWriter = null;

        super.tearDown();
    }

    public void testBasic() throws Exception {
        ComponentSetDescriptor set = new ComponentSetDescriptor();

        ComponentDescriptor component = new ComponentDescriptor();
        component.setRole("foo");
        component.setRoleHint("bar");
        component.setComponentFactory("baz");

        set.addComponentDescriptor(component);

        StringWriter writer = new StringWriter();
        descriptorWriter.writeDescriptorSet(writer, set, false);
        writer.flush();
        writer.close();
        
        String xml = writer.toString();

        assertTrue(xml.length() != 0);

        PlexusConfiguration config = PlexusTools.buildConfiguration(xml);
        assertNotNull(config);
        
        org.codehaus.plexus.component.repository.ComponentSetDescriptor set2 = PlexusTools.buildComponentSet(config);
        assertNotNull(set2);

        List components = set2.getComponents();
        assertNotNull(components);
        assertEquals(1, components.size());

        org.codehaus.plexus.component.repository.ComponentDescriptor component2 =
                (org.codehaus.plexus.component.repository.ComponentDescriptor) components.get(0);
        assertNotNull(component2);
        
        assertEquals(component.getRole(), component2.getRole());
        assertEquals(component.getRoleHint(), component2.getRoleHint());
        assertEquals(component.getComponentFactory(), component2.getComponentFactory());

        //
        // TODO: Verify requirements and configuration too... but I'm too lazy for that right now
        //
    }
}
