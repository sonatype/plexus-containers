package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.PlexusContainer;

import java.io.InputStream;

/**
 *
 * @author <a href="mailto:mma@imtf.ch">Michal Maczka</a>
 * @version $Revision$
 */
public class DefaultComponentComposerManagerTest extends PlexusTestCase
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

            assertNotNull( componentB );

        }
        catch ( Exception e )
        {
            e.printStackTrace();

            fail( e.getMessage() );

        }

    }


}
