package org.codehaus.plexus.embed;

import junit.framework.TestCase;
import org.codehaus.plexus.PlexusContainer;

import java.net.URL;
import java.util.Properties;

/**
 * @author  Ben Walding
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class EmbedderTest
    extends TestCase
{
    public void testConfigurationByURL()
        throws Exception
    {
        Embedder embed = new Embedder();

        embed.setConfiguration( getClass().getResource( "EmbedderTest.xml" ) );

        embed.addContextValue( "foo", "bar" );

        Properties contextProperties = new Properties();

        contextProperties.setProperty( "property1", "value1" );

        contextProperties.setProperty( "property2", "value2" );

        embed.start();

        try
        {
            embed.setConfiguration( getClass().getResource( "EmbedderTest.xml" ) );

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }

        Object o = embed.lookup( MockComponent.ROLE );

        assertEquals( "I AM MOCKCOMPONENT", o.toString() );

        assertNotNull( getClass().getResource( "/test.txt" ) );

        embed.stop();
    }
}
