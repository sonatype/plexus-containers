package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentComposerTest
    extends TestCase
{
    public void testComponentAssignment()
        throws Exception
    {
        DefaultComponentComposer composer = new DefaultComponentComposer();

        ComponentA a = new ComponentA();

        composer.assignComponent( new ComponentB(), a );

        ComponentB b = a.getComponentB();

        assertNotNull( b );
    }
}
