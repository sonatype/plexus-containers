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

        assertEquals( 3, ccs.getConfigurationSources().size() );

        assertEquals( ContainerConfigurationSource.class.getName(), ccs
            .getConfigurationSources().get( 0 ).getClass().getName() );

        assertEquals( ADummyConfigurationSource.class.getName(), ccs
            .getConfigurationSources().get( 1 ).getClass().getName() );

        assertEquals( AnotherDummyConfigurationSource.class.getName(), ccs
            .getConfigurationSources().get( 2 ).getClass().getName() );
    }

}
