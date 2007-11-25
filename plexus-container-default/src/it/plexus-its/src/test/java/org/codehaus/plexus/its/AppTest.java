package org.codehaus.plexus.its;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Unit test for simple DefaultApp.
 */
public class AppTest 
    extends PlexusTestCase
{
    public void testApp()
        throws Exception
    {
        App app = (App) lookup( App.class.getName(), "standard" );

        assertNotNull( app.getContainer() );

        assertNotNull( app.getLogger() );

        assertEquals( App.class.getName(), app.getLogger().getName() );

        App logEnabledApp = (App) lookup( App.class.getName(), "log-enabled" );

        assertNotNull( logEnabledApp );

        assertEquals( App.class.getName(), app.getLogger().getName() );
    }
}
