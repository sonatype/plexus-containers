package org.codehaus.plexus.embed;

import junit.framework.TestCase;
import org.codehaus.plexus.PlexusContainer;

import java.net.URL;

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

        embed.stop();
    }
}
