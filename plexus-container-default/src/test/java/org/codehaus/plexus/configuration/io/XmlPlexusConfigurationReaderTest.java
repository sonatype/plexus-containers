package org.codehaus.plexus.configuration.io;

import java.io.StringReader;

import junit.framework.TestCase;

import org.codehaus.plexus.configuration.ConfigurationTestHelper;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class XmlPlexusConfigurationReaderTest
    extends TestCase
{
    public void testRead()
        throws Exception
    {
        StringReader sr = new StringReader( ConfigurationTestHelper.getXmlConfiguration() );

        XmlPlexusConfigurationReader reader = new XmlPlexusConfigurationReader();

        PlexusConfiguration c = reader.read( sr );

        ConfigurationTestHelper.testConfiguration( c );
    }

}
