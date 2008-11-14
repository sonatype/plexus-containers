package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.test.ComponentA;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultComponentLookupManagerTest
    extends PlexusTestCase
{
    public void testLookupsWithAndWithoutRoleHint()
        throws Exception
    {
        String resource = getConfigurationName( "components.xml" );

        System.out.println( "resource = " + resource );

        assertNotNull( resource );

        ContainerConfiguration c = new DefaultContainerConfiguration()
            .setName( "test" )
            .setContainerConfiguration( resource );

        DefaultPlexusContainer container = new DefaultPlexusContainer( c );

        try
        {
            container.lookup( ComponentA.class );

            fail( "Expected exception" );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }
}
