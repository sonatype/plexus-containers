package org.codehaus.plexus.test.map;

/*
 * LISENCE
 */

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class NoComponentsMapTest extends PlexusTestCase {
    public NoComponentsMapTest(String name) {
        super(name);
    }

	public void testNoComponents() throws Exception {
        ActivityManager manager;
        
        manager = (ActivityManager)lookup(ActivityManager.ROLE);
        assertEquals(0, manager.getActivityCount());
    }
}
