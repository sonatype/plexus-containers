package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.PlexusTestCase;

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

        assertTrue( a == b );

        assertTrue( a == c );

        assertTrue( a == d );
    }
}
