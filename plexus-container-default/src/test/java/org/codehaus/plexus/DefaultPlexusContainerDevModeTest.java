package org.codehaus.plexus;

import junit.framework.TestCase;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;

public class DefaultPlexusContainerDevModeTest extends TestCase
{
    public void testDevModeOn() throws PlexusContainerException, NoSuchRealmException
    {
        ClassWorld world = createClassWorld(); 
        ContainerConfiguration cc = new DefaultContainerConfiguration()
        .setClassWorld( world )
        .setDevMode( true );
        
        DefaultPlexusContainer container = new DefaultPlexusContainer( cc );
        
        assertNotNull( "Realm expected to be not null", world.getRealm( "TestRealm" ) );
        
        container.dispose();
        
        assertNotNull( "Realm expected to be not null", world.getRealm( "TestRealm" ) );
    }
    
    public void testDevModeOff() throws PlexusContainerException, NoSuchRealmException
    {
        ClassWorld world = createClassWorld(); 
        ContainerConfiguration cc = new DefaultContainerConfiguration()
        .setClassWorld( world );
        
        DefaultPlexusContainer container = new DefaultPlexusContainer( cc );
        
        assertNotNull( "Realm expected to be not null", world.getRealm( "TestRealm" ) );
        
        container.dispose();
        
        try
        {
            world.getRealm( "TestRealm" );
            assertTrue( "TestRealm should be null", false );
        }
        catch (NoSuchRealmException e)
        {
            assertTrue( true );
        }
    }
    
    protected ClassWorld createClassWorld() throws NoSuchRealmException
    {
        ClassWorld cw =
            new ClassWorld( "TestRealm", DefaultPlexusContainerChildTest.class.getClassLoader() );
        return cw;
    }
}
