package org.codehaus.plexus.component.composition;

import java.io.InputStream;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:mma@imtf.ch">Michal Maczka</a>
 * @version $Id$
 */
public class DefaultComponentComposerManagerTest
    extends PlexusTestCase
{
    protected InputStream getCustomConfiguration() throws Exception
    {
        System.out.println( "Reading custom configuration" );

        final InputStream retValue = getResourceAsStream( "/org/codehaus/plexus/component/composition/components.xml" );

        assertNotNull( retValue );

        return retValue;
    }

    public void testComposition()
    {
        try
        {
            final ComponentA componentA = ( ComponentA ) lookup( ComponentA.ROLE );

            assertNotNull( componentA );

            final ComponentB componentB = componentA.getComponentB();

            assertNotNull( componentB );

            final ComponentC componentC = componentB.getComponentC();

            assertNotNull( componentC );
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            fail( e.getMessage() );
        }
    }
}
