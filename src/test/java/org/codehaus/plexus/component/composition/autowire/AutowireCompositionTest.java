package org.codehaus.plexus.component.composition.autowire;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.composition.SetterComponentComposer;
import org.codehaus.plexus.component.composition.ComponentComposer;

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
