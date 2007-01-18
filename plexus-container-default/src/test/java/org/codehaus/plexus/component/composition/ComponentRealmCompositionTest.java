package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Arrays;
import java.io.File;

/** @author Jason van Zyl */
public class ComponentRealmCompositionTest
    extends PlexusTestCase
{
    /*
     * We are testing that when the same component implementation exists in more then one
     * realm and components depend on those implementations, that the right realm is used
     * to wire up the components.
     *
     * An example of this in practice are Maven plugins where each plugin is loaded into
     * a separate realm and the plugin may have dependencies on other components. We want
     * to make sure that a requirement, say a JarArchiver, for a given component, say the
     * maven-jar-plugin, is wired up with a JarArchiver taken from the same realm as the
     * maven-jar-plugin and not a different realm.
     */
    public void testCompositionWhereTheSameImplementationExistsInDifferentRealms()
        throws Exception
    {
        File p0 = new File( getBasedir(), "src/test/test-components/plugin0-1.0-SNAPSHOT.jar" );

        assertTrue( p0.exists() );

        File p1 = new File( getBasedir(), "src/test/test-components/plugin1-1.0-SNAPSHOT.jar" );

        assertTrue( p1.exists() );

        File a = new File( getBasedir(), "src/test/test-components/component-a-1.0-SNAPSHOT.jar" );

        assertTrue( a.exists() );

        File b = new File( getBasedir(), "src/test/test-components/component-b-1.0-SNAPSHOT.jar" );

        assertTrue( b.exists() );

        File c = new File( getBasedir(), "src/test/test-components/component-c-1.0-SNAPSHOT.jar" );

        assertTrue( c.exists() );

        File archiver = new File( getBasedir(), "src/test/test-components/plexus-archiver-1.0-alpha-8.jar" );

        assertTrue( archiver.exists() );

        // Create ClassRealm plugin0 with plugin0 -> A, plugin0 -> B

        List plugin0Jars = Arrays.asList( new Object[]{p0, a, b, archiver} );

        ClassRealm plugin0Realm = container.createComponentRealm( "plugin0Realm", plugin0Jars );

        // Create ClassRealm plugin1 with plugin1 -> A, plugin1 -> C

        List plugin1Jars = Arrays.asList( new Object[]{p1, a, c, archiver } );

        ClassRealm plugin1Realm = container.createComponentRealm( "plugin1Realm", plugin1Jars );

        // Lookups

        try
        {
            container.lookup( "org.codehaus.plexus.plugins.Plugin0" );
            fail("Expected component lookup failure");
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }

        container.lookup( "org.codehaus.plexus.plugins.Plugin0", plugin0Realm );


        try
        {
            container.lookup( "org.codehaus.plexus.plugins.Plugin1" );
            fail("Expected component lookup failure");
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }

        container.lookup( "org.codehaus.plexus.plugins.Plugin1", plugin1Realm );
    }
}
