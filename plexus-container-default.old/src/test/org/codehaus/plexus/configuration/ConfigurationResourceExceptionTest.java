package org.codehaus.plexus.configuration;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ConfigurationResourceExceptionTest
    extends TestCase
{
    public void testException()
    {
        PlexusConfigurationResourceException e = new PlexusConfigurationResourceException( "bad doggy!" );

        assertEquals( "bad doggy!", e.getMessage() );
    }
}
