package org.codehaus.plexus.configuration.source;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

public class OverridingConfigurationSourceTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        // we have plexus.xml with configSource that overrides the Plexus default one, so the container 
        // should use it instead of the "default" one

        ConfigurationSource cs = ( (MutablePlexusContainer) getContainer() ).getConfigurationSource();

        assertNotNull( cs );

        assertEquals( ADummyConfigurationSource.class.getName(), cs.getClass().getName() );
    }

}
