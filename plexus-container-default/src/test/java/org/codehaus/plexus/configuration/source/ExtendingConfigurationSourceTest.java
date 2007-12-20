package org.codehaus.plexus.configuration.source;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

public class ExtendingConfigurationSourceTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        // we have plexus.xml with two configSources, so the container should use the "chained" case,
        // which is ChainedConfigurationSource with 3 elem in list: the plexusDefaultConfig source,
        // and the two user provided, in this order: ADummyConfigurationSource, AnotherDummyConfigurationSource

        ConfigurationSource cs = ( (MutablePlexusContainer) getContainer() ).getConfigurationSource();

        assertNotNull( cs );

        assertEquals( ChainedConfigurationSource.class.getName(), cs.getClass().getName() );

        ChainedConfigurationSource ccs = (ChainedConfigurationSource) cs;

        // we have 3 config sources overall
        assertEquals( 3, ccs.getConfigurationSources().size() );

        // and the last in the source list is container source
        assertEquals( ContainerConfigurationSource.class.getName(), ccs
            .getConfigurationSources().get( 2 ).getClass().getName() );

    }

}
