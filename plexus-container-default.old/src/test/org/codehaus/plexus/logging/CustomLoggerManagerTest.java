package org.codehaus.plexus.logging;

/*
 * LISENCE
 */

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class CustomLoggerManagerTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        LoggerManager manager = (LoggerManager)lookup( LoggerManager.class.getName() );

        assertNotNull( manager );

        assertEquals( MockLoggerManager.class.getName(), manager.getClass().getName() );
    }
}
