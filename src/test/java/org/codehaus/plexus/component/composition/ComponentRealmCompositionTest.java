package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.util.List;
import java.util.Arrays;
import java.io.File;

/** @author Jason van Zyl */
public class ComponentRealmCompositionTest
    extends PlexusTestCase
{
    public void testComposition()
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

        // Create ClassRealm plugin0 with plugin0 -> A, plugin0 -> B

        List plugin0Jars = Arrays.asList( new Object[]{p0, a, b} );

        ClassRealm plugin0Realm = container.createComponentRealm( "plugin0Realm", plugin0Jars );

        // Create ClassRealm plugin1 with plugin1 -> A, plugin1 -> C

        List plugin1Jars = Arrays.asList( new Object[]{p1, a, c} );

        ClassRealm plugin1Realm = container.createComponentRealm( "plugin1Realm", plugin1Jars );

        // Lookups

        Object plugin0 = container.lookup( "org.codehaus.plexus.plugins.Plugin0" );

        Object plugin1 = container.lookup( "org.codehaus.plexus.plugins.Plugin1" );
    }
}
