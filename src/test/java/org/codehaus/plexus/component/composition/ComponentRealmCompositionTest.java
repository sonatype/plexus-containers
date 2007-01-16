package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.util.List;
import java.io.File;

/** @author Jason van Zyl */
public class ComponentRealmCompositionTest
    extends PlexusTestCase
{
    public void testComposition()
        throws Exception
    {
        File a = new File( getBasedir(), "src/test/test-components/component-a-1.0-SNAPSHOT.jar" );

        File b = new File( getBasedir(), "src/test/test-components/component-b-1.0-SNAPSHOT.jar" );

        File c = new File( getBasedir(), "src/test/test-components/component-c-1.0-SNAPSHOT.jar" );

        // Create ClassRealm plugin0 with plugin0 -> A, plugin0 -> B

        List plugin0Jars = null;

        ClassRealm plugin0Realm = container.createComponentRealm( "c0", plugin0Jars );

        Object plugin0 = container.lookup( "plugin0" );

        // Create ClassRealm plugin1 with plugin1 -> A, plugin1 -> C

        List plugin1Jars = null;

        ClassRealm plugin1Realm = container.createComponentRealm( "c1", plugin1Jars );

        Object plugin1 = container.lookup( "plugin1" );


    }
}
