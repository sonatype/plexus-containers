package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.PlexusContainer;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class ClassicSingletonComponentManagerTest
    extends PlexusTestCase
{
    public void testSequentialLookupsReturnTheSameInstance()
        throws Exception
    {
        Component a,b,c,d;

        a = (Component) lookup( Component.ROLE );

        b = (Component) lookup( Component.ROLE );

        c = (Component) lookup( Component.ROLE );

        d = (Component) lookup( Component.ROLE );

        assertEquals( a, b );

        assertEquals( a, c );

        assertEquals( a, d );
    }
}
