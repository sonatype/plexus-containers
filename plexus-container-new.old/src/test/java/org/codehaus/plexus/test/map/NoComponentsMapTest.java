package org.codehaus.plexus.test.map;

/*
 * LICENSE
 */

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class NoComponentsMapTest
    extends PlexusTestCase
{
	public void testNoComponents()
        throws Exception
    {
        ActivityManager manager;
        
        manager = (ActivityManager) lookup( ActivityManager.ROLE );

        assertEquals( 0, manager.getActivityCount() );
    }
}
