package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;
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
    {
        String c1Role = "c1";

        String c2Role = "c2";

        String c3Role = "c3";

        CompositionResolver cr = new CompositionResolver();

        ComponentDescriptor c1 = new ComponentDescriptor();

        c1.setRole( c1Role );

        c1.addRequirement( c2Role );

        c1.addRequirement( c3Role );

        ComponentDescriptor c2 = new ComponentDescriptor();

        c2.setRole( c2Role );

        ComponentDescriptor c3 = new ComponentDescriptor();

        c3.setRole( c3Role );

        cr.addComponentDescriptor( c1 );

        cr.addComponentDescriptor( c2 );

        cr.addComponentDescriptor( c3 );

        List dependencies = cr.getComponentDependencies( c1.getComponentKey() );

        assertTrue( dependencies.contains( c2Role ) );

        assertTrue( dependencies.contains( c3Role ) );

        assertEquals( 2, dependencies.size() );
    }
}
