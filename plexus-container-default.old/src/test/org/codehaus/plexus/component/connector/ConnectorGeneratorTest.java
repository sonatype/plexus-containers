package org.codehaus.plexus.component.connector;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ConnectorGeneratorTest
    extends TestCase
{

    public void testConnectorGenerator()
        throws Exception
    {
        ConnectorGenerator cg = new ConnectorGenerator();

        DefaultProvider provider = new DefaultProvider();

        Provider connector = (Provider) cg.generate( Thread.currentThread().getContextClassLoader(), Provider.class, provider );

        assertNotNull( connector );

        assertFalse( provider.run );

        connector.run();

        assertTrue( provider.run );

        assertFalse( provider.execute );

        connector.execute();

        assertTrue( provider.execute );
    }
}
