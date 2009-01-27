package org.codehaus.plexus.component.collection;

import static com.google.common.collect.Sets.newHashSet;
import junit.framework.TestCase;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.collections.LiveMap;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class LiveMapTest extends TestCase
{
    private DefaultPlexusContainer container;
    private Component unknown;

    protected void setUp() throws Exception
    {
        super.setUp();
        container = new DefaultPlexusContainer();
        unknown = new Component( "Unknown" );
    }

    public void testEmpty() throws Exception
    {

        LiveMap<Component> map = new LiveMap<Component>( container, Component.class, null, null );

        Map<String, Component> all = emptyMap();

        // isEmpty and size
        assertTrue( map.isEmpty() );
        assertEquals( 0, map.size() );

        // get
        assertNull( map.get( "unknown" ) );

        // contains
        assertFalse( map.containsKey( "unknown" ) );
        assertFalse( map.containsValue( unknown ) );

        // keySet, values, entrySet
        assertEquals( all.keySet(), map.keySet() );
        assertEquals( newHashSet( all.values() ), newHashSet( map.values() ) );
        assertEquals( all.entrySet(), map.entrySet() );

        // hashCode, equals, and toString
        assertEquals( emptyMap().hashCode(), map.hashCode() );
        assertTrue( map.equals( emptyMap() ) );
        assertNotNull( map.toString() );
        assertTrue( map.toString().length() > 0 );

        // List must be immutable
        assertImmutable( map );
    }

    public void testSingleComponent() throws Exception
    {

        LiveMap<Component> map = new LiveMap<Component>( container, Component.class, null, null );

        Component zero = addComponent( "zero" );
        Map<String, Component> all = singletonMap( "zero", zero );

        // isEmpty and size
        assertFalse( map.isEmpty() );
        assertEquals( 1, map.size() );

        // get
        assertSame( zero, map.get( "zero" ) );
        assertNull( map.get( "unknown" ) );

        // contains
        assertTrue( map.containsKey( "zero" ) );
        assertFalse( map.containsKey( "unknown" ) );
        assertTrue( map.containsValue( zero ) );
        assertFalse( map.containsValue( unknown ) );

        // keySet, values, entrySet
        assertEquals( all.keySet(), map.keySet() );
        assertEquals( newHashSet( all.values() ), newHashSet( map.values() ) );
        assertEquals( all.entrySet(), map.entrySet() );

        // hashCode, equals and toString
        assertEquals( all.hashCode(), map.hashCode() );
        assertTrue( map.equals( all ) );
        assertNotNull( map.toString() );
        assertTrue( map.toString().length() > 0 );

        // List must be immutable
        assertImmutable( map );
    }

    public void testAllHints() throws Exception
    {
        // components are in the order they are added to the container
        LiveMap<Component> map = new LiveMap<Component>( container, Component.class, null, null );

        Map<String, Component> all = new LinkedHashMap<String, Component>();
        all.put( "zero", addComponent( "zero" ) );
        all.put( "one", addComponent( "one" ) );
        all.put( "two", addComponent( "two" ) );
        all.put( "three", addComponent( "three" ) );
        all.put( "four", addComponent( "four" ) );

        assertZeroToFour( all, map );
    }

    public void testSpecificHints() throws Exception
    {
        LiveMap<Component> map = new LiveMap<Component>( container,
            Component.class,
            asList( "zero", "one", "two", "three", "four" ),
            null );

        Map<String, Component> all = new LinkedHashMap<String, Component>();
        all.put( "zero", addComponent( "zero" ) );
        all.put( "one", addComponent( "one" ) );
        all.put( "two", addComponent( "two" ) );
        all.put( "three", addComponent( "three" ) );
        all.put( "four", addComponent( "four" ) );

        Component five = addComponent( "five" );

        assertZeroToFour( all, map );
    }

    public void testSpecificHintsWithRelease() throws Exception
    {
        LiveMap<Component> map = new LiveMap<Component>( container,
            Component.class,
            asList( "zero", "one", "two", "three", "four" ),
            null );

        Map<String, Component> all = new LinkedHashMap<String, Component>();

        // add each component in a child realm
        ClassRealm realmZero = container.createChildRealm( "zero" );
        all.put( "zero", addComponent( "zero", realmZero ) );

        ClassRealm realmOne = container.createChildRealm( "one" );
        all.put( "one", addComponent( "one", realmOne ) );

        ClassRealm realmTwo = container.createChildRealm( "two" );
        all.put( "two", addComponent( "two", realmTwo ) );

        ClassRealm realmThree = container.createChildRealm( "three" );
        all.put( "three", addComponent( "three", realmThree ) );

        ClassRealm realmFour = container.createChildRealm( "four" );
        all.put( "four", addComponent( "four", realmFour ) );

        // verify we have a normal 0-4 list now
        assertZeroToFour( all, map );

        // remove realm 4 which contains component 4
        container.removeComponentRealm( realmFour );
        all.remove( "four" );

        // verify we now have a 0-3 list
        assertEquals( all, map );

        // remove realm 2 which contains component 2
        container.removeComponentRealm( realmTwo );
        all.remove( "two" );

        // verify we now have a 0,1,3 list
        assertEquals( all, map );

        // remove realm 0 which contains component 0
        container.removeComponentRealm( realmZero );
        all.remove( "zero" );

        // verify we now have a 1,3 list
        assertEquals( all, map );

        // add them back
        all.put( "zero", addComponent( "zero", realmZero ) );
        all.put( "four", addComponent( "four", realmFour ) );
        all.put( "two", addComponent( "two", realmTwo ) );

        // verify we have a normal 0-4 list again
        assertZeroToFour( all, map );
    }

    private void assertZeroToFour( Map<String, Component> expected, LiveMap<Component> map )
    {
        // get input values
        assertEquals( 5, expected.size() );
        Component zero = expected.get( "zero" );
        Component one = expected.get( "one" );
        Component two = expected.get( "two" );
        Component three = expected.get( "three" );
        Component four = expected.get( "four" );

        // isEmpty and size
        assertFalse( map.isEmpty() );
        assertEquals( 5, map.size() );

        // get
        assertSame( zero, map.get( "zero" ) );
        assertSame( one, map.get( "one" ) );
        assertSame( two, map.get( "two" ) );
        assertSame( three, map.get( "three" ) );
        assertSame( four, map.get( "four" ) );
        assertNull( map.get( "unknown" ) );

        // contains
        assertTrue( map.containsKey( "zero" ) );
        assertTrue( map.containsKey( "two" ) );
        assertTrue( map.containsKey( "four" ) );
        assertFalse( map.containsKey( "unknown" ) );
        assertTrue( map.containsValue( zero ) );
        assertTrue( map.containsValue( two ) );
        assertTrue( map.containsValue( four ) );
        assertFalse( map.containsValue( unknown ) );

        // keySet, values, entrySet
        assertEquals( expected.keySet(), map.keySet() );
        assertEquals( newHashSet( expected.values() ), newHashSet( map.values() ) );
        assertEquals( expected.entrySet(), map.entrySet() );

        // hashCode, equals and toString
        assertEquals( expected.hashCode(), map.hashCode() );
        assertTrue( map.equals( expected ) );
        assertNotNull( map.toString() );
        assertTrue( map.toString().length() > 0 );

        // List must be immutable
        assertImmutable( map );
    }

    private Component addComponent( String name )
        throws ComponentRepositoryException
    {
        return addComponent( name, container.getContainerRealm() );
    }

    private Component addComponent( String name, ClassRealm containerRealm )
        throws ComponentRepositoryException
    {
        Component component = new Component( name );
        container.addComponent( component, Component.class, name, containerRealm );
        return component;
    }

    private static void assertImmutable( ConcurrentMap<String, Component> map )
    {
        Component component = new Component( "component" );
        Map<String, Component> all = singletonMap( "component", component );

        try
        {
            map.put( "component", component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            map.putIfAbsent( "component", component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            map.putAll( all );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            map.remove( "component" );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            map.remove( "component", component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            map.replace( "component", component, component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            map.replace( "component", component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            map.clear();
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

    }

    public static class Component
    {
        public final String name;

        public Component( String name )
        {
            this.name = name;
        }

        public String toString()
        {
            return name;
        }
    }
}