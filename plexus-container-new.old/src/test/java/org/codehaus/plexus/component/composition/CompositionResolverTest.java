package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;
import org.codehaus.plexus.PlexusTools;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class CompositionResolverTest
    extends TestCase
{
    public void testSimpleComponentResolution()
        throws Exception
    {
        String cc1 =
            "<component>" +
            "  <role>c1</role>" +
            "  <requirements>" +
            "    <requirement>c2</requirement>" +
            "    <requirement>c3</requirement>" +
            "  </requirements>" +
            "</component>";

        String cc2 =
            "<component>" +
            "  <role>c2</role>" +
            "</component>";

        String cc3 =
            "<component>" +
            "  <role>c3</role>" +
            "</component>";

        CompositionResolver cr = new CompositionResolver();

        ComponentDescriptor c1 = PlexusTools.buildComponentDescriptor( cc1 );

        ComponentDescriptor c2 = PlexusTools.buildComponentDescriptor( cc2 );

        ComponentDescriptor c3 = PlexusTools.buildComponentDescriptor( cc3 );

        cr.addComponentDescriptor( c1 );

        cr.addComponentDescriptor( c2 );

        cr.addComponentDescriptor( c3 );

        List dependencies = cr.getComponentDependencies( c1.getComponentKey() );

        assertTrue( dependencies.contains( c2.getRole() ) );

        assertTrue( dependencies.contains( c3.getRole() ) );

        assertEquals( 2, dependencies.size() );
    }
}
