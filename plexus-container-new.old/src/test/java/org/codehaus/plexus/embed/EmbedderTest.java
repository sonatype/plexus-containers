package org.codehaus.plexus.embed;

import junit.framework.TestCase;
import org.codehaus.plexus.PlexusContainer;

import java.net.URL;

/**
 * @author  Ben Walding
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class EmbedderTest extends TestCase
{
    /**
     * @deprecated this test is only required while Embedder has the old setConfiguration(String) method
     * @throws Exception
     */
    public void testConfigurationByClassLoader()
        throws Exception
    {
        Embedder embedder = new Embedder();

        // We should not be able to get the container before the embedder
        // is started. It should throw an illegal state exception.

        PlexusContainer container = null;

        try
        {
            container = embedder.getContainer();
        }
        catch ( IllegalStateException e )
        {
            assertNull( container );
        }


        //This really only works because the EmbedderTest is in the same package as the Embedder
        embedder.setConfiguration( "EmbedderTest.xml" );

        embedder.addContextValue( "foo", "bar" );

        try
        {
            embedder.stop();

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }

        embedder.start();

        try
        {
            embedder.start();

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }

        try
        {
            embedder.setConfiguration( "EmbedderTest.xml" );

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }

        try
        {
            embedder.addContextValue( "key", "value" );

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }

        assertTrue( embedder.hasService( MockComponent.ROLE ) );

        container = embedder.getContainer();

        assertNotNull( container );

        MockComponent component = (MockComponent) embedder.lookup( MockComponent.ROLE );

        assertEquals( "bar", component.getFoo() );

        assertNotNull( component );

        assertEquals( "I AM MOCKCOMPONENT", component.toString() );

        assertTrue( embedder.hasService( MockComponent.ROLE, "foo" ) );

        MockComponent componentWithHint = (MockComponent) embedder.lookup( MockComponent.ROLE, "foo" );

        assertNotNull( componentWithHint );

        embedder.release( component );

        embedder.release( componentWithHint );

        embedder.stop();

        try
        {
            embedder.stop();

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }

        try
        {
            embedder.start();

            fail();
        }
        catch ( IllegalStateException e )
        {
            // do nothing
        }
    }

    public void testConfigurationByURL()
        throws Exception
    {
        Embedder embed = new Embedder();

        embed.setConfiguration( getClass().getResource( "EmbedderTest.xml" ) );

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

    public void testEmbedderWithNonExistentConfiguration()
        throws Exception
    {
        Embedder embed = new Embedder();

        embed.setConfiguration( "dummy.xml" );

        try
        {
            embed.start();

            fail();
        }
        catch ( Exception e )
        {
            // do nothing
        }
    }

    public void testEmbedderWithNonExistentURLConfiguration()
        throws Exception
    {
        Embedder embed = new Embedder();

        embed.setConfiguration( new URL( "file:///dummy.xml" ) );

        try
        {
            embed.start();

            fail();
        }
        catch ( Exception e )
        {
            // do nothing
        }
    }

}
