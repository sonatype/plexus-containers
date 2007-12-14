package org.codehaus.plexus.configuration.source;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

public class DefaultConfigurationSourceTest
    extends PlexusTestCase
{

    public void testBasic()
        throws Exception
    {
        // we have no plexus.xml, so the container should use the "default" source,
        // which is ContainerConfigurationSource

        ConfigurationSource cs = ( (MutablePlexusContainer) getContainer() ).getConfigurationSource();

        assertNotNull( cs );

        assertEquals( ContainerConfigurationSource.class.getName(), cs.getClass().getName() );
    }

}
