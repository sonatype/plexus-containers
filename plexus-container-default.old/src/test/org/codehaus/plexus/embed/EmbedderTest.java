package org.codehaus.plexus.embed;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class EmbedderTest extends TestCase
{
    /**
     * @deprecated this test is only required while Embedder has the old setConfiguration(String) method
     * @throws Exception
     */
    public void testConfigurationByClassLoader() throws Exception
    {
        Embedder embed = new Embedder();
        //This really only works because the EmbedderTest is in the same package as the Embedder
        embed.setConfiguration("EmbedderTest.xml");

        embed.start();
        Object o = embed.lookup(MockComponent.ROLE);
        assertEquals("I AM MOCKCOMPONENT", o.toString());
        embed.stop();
    }

    public void testConfigurationByURL() throws Exception
    {
        Embedder embed = new Embedder();
        embed.setConfiguration(getClass().getResource("EmbedderTest.xml"));

        embed.start();
        Object o = embed.lookup(MockComponent.ROLE);
        assertEquals("I AM MOCKCOMPONENT", o.toString());
        embed.stop();
    }
}
