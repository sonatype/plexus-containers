package org.codehaus.plexus.embed;

import junit.framework.TestCase;

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

        //This really only works because the EmbedderTest is in the same package as the Embedder
        embedder.setConfiguration( "EmbedderTest.xml" );

        embedder.addContextValue( "foo", "bar" );

        embedder.start();

        assertTrue( embedder.hasService( MockComponent.ROLE ) );

        assertNotNull( embedder.getContainer() );

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
    }

    public void testConfigurationByURL()
        throws Exception
    {
        Embedder embed = new Embedder();

        embed.setConfiguration( getClass().getResource( "EmbedderTest.xml" ) );

        embed.start();

        Object o = embed.lookup( MockComponent.ROLE );

        assertEquals( "I AM MOCKCOMPONENT", o.toString() );

        embed.stop();
    }
}
