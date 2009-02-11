package org.codehaus.plexus.component.collection;

import com.google.common.collect.Iterators;
import junit.framework.TestCase;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.collections.LiveList;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;

import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.util.List;

public class LiveListTest extends TestCase
{
    private DefaultPlexusContainer container;

    protected void setUp() throws Exception
    {
        super.setUp();
        container = new DefaultPlexusContainer();
    }

    public void testEmptyList() throws Exception
    {
        LiveList<Component> list = new LiveList<Component>( container, Component.class, null, null );

        Component unknown = new Component( "Unknown" );

        // isEmpty and size
        assertTrue( list.isEmpty() );
        assertEquals( 0, list.size() );

        // get
        assertGetOutOfBounds( list, 0 );
        assertGetOutOfBounds( list, -1 );
        assertGetOutOfBounds( list, 1 );

        // contains
        assertFalse( list.contains( unknown ) );
        assertFalse( list.containsAll( asList( unknown ) ) );

        // indexOfs
        assertEquals( -1, list.indexOf( unknown ) );
        assertEquals( -1, list.lastIndexOf( unknown ) );

        // subList
        assertTrue( list.subList( 0, 0 ).isEmpty() );
        assertSubListOutOfBounds( list, 0, 1 );
        assertSubListOutOfBounds( list, -1, 0 );

        // iterators
        assertTrue( Iterators.elementsEqual( emptyList().iterator(), list.iterator() ) );
        assertTrue( Iterators.elementsEqual( emptyList().listIterator(), list.listIterator() ) );
        assertTrue( Iterators.elementsEqual( emptyList().listIterator(), list.listIterator( 0 ) ) );
        assertListIteratorOutOfBounds( list, -1 );
        assertListIteratorOutOfBounds( list, 1 );

        // toArrays
        assertTrue( Arrays.equals( new Object[0], list.toArray() ) );
        assertTrue( Arrays.equals( new Object[0], list.toArray( new Component[0] ) ) );

        // hashCode, equals, and toString
        assertEquals( emptyList().hashCode(), list.hashCode() );
        assertTrue( list.equals( emptyList() ) );
        assertNotNull( list.toString() );
        assertTrue( list.toString().length() > 0 );

        // List must be immutable
        assertImmutable( list );
    }

    public void testSingleComponentList() throws Exception
    {
        LiveList<Component> list = new LiveList<Component>( container, Component.class, null, null );

        Component zero = addComponent( "zero" );

        // isEmpty and size
        assertFalse( list.isEmpty() );
        assertEquals( 1, list.size() );

        // get
        assertSame( zero, list.get( 0 ) );
        assertGetOutOfBounds( list, -1 );
        assertGetOutOfBounds( list, 1 );

        // contains
        assertTrue( list.contains( zero ) );
        assertTrue( list.containsAll( asList( zero ) ) );

        // indexOfs
        assertEquals( 0, list.indexOf( zero ) );
        assertEquals( 0, list.lastIndexOf( zero ) );

        // subList
        assertEquals( asList( zero ), list.subList( 0, 1 ) );
        assertSubListOutOfBounds( list, 0, 2 );
        assertSubListOutOfBounds( list, -1, 0 );

        // iterators
        assertTrue( Iterators.elementsEqual( asList( zero ).iterator(), list.iterator() ) );
        assertTrue( Iterators.elementsEqual( asList( zero ).listIterator(), list.listIterator() ) );
        assertTrue( Iterators.elementsEqual( asList( zero ).listIterator(), list.listIterator( 0 ) ) );
        assertTrue( Iterators.elementsEqual( emptyList().listIterator(), list.listIterator( 1 ) ) );
        assertListIteratorOutOfBounds( list, -1 );
        assertListIteratorOutOfBounds( list, 2 );

        // toArrays
        assertTrue( Arrays.equals( new Object[]{zero}, list.toArray() ) );
        assertTrue( Arrays.equals( new Object[]{zero}, list.toArray( new Component[1] ) ) );

        // hashCode, equals and toString
        assertEquals( asList( zero ).hashCode(), list.hashCode() );
        assertTrue( list.equals( asList( zero ) ) );
        assertNotNull( list.toString() );
        assertTrue( list.toString().length() > 0 );

        // List must be immutable
        assertImmutable( list );
    }

    public void testUnsortedList() throws Exception
    {
        // components are in the order they are added to the container
        LiveList<Component> list = new LiveList<Component>( container, Component.class, null, null );

        Component zero = addComponent( "zero" );
        Component one = addComponent( "one" );
        Component two = addComponent( "two" );
        Component three = addComponent( "three" );
        Component four = addComponent( "four" );

        assertZeroToFourList( asList( zero, one, two, three, four ), list );
    }

    public void testSortedListForwardInsertion() throws Exception
    {
        LiveList<Component> list = new LiveList<Component>( container,
            Component.class,
            asList( "zero", "one", "two", "three", "four" ),
            null );

        Component zero = addComponent( "zero" );
        Component one = addComponent( "one" );
        Component two = addComponent( "two" );
        Component three = addComponent( "three" );
        Component four = addComponent( "four" );
        Component five = addComponent( "five" );

        assertZeroToFourList( asList( zero, one, two, three, four ), list );
    }

    public void testSortedListReverseInsertion() throws Exception
    {
        LiveList<Component> list = new LiveList<Component>( container,
            Component.class,
            asList( "zero", "one", "two", "three", "four" ),
            null );

        Component five = addComponent( "five" );
        Component four = addComponent( "four" );
        Component three = addComponent( "three" );
        Component two = addComponent( "two" );
        Component one = addComponent( "one" );
        Component zero = addComponent( "zero" );

        assertZeroToFourList( asList( zero, one, two, three, four ), list );
    }

    public void testSortedListRandomInsertion() throws Exception
    {
        LiveList<Component> list = new LiveList<Component>( container,
            Component.class,
            asList( "zero", "one", "two", "three", "four" ),
            null );

        Component four = addComponent( "four" );
        Component zero = addComponent( "zero" );
        Component five = addComponent( "five" );
        Component two = addComponent( "two" );
        Component one = addComponent( "one" );
        Component three = addComponent( "three" );

        assertZeroToFourList( asList( zero, one, two, three, four ), list );
    }

    public void testSortedListWithRelease() throws Exception
    {
        LiveList<Component> list = new LiveList<Component>( container,
            Component.class,
            asList( "zero", "one", "two", "three", "four" ),
            null );

        // add each component in a child realm
        ClassRealm realmZero = container.createChildRealm( "zero" );
        Component zero = addComponent( "zero", realmZero );

        ClassRealm realmFour = container.createChildRealm( "four" );
        Component four = addComponent( "four", realmFour );

        ClassRealm realmTwo = container.createChildRealm( "two" );
        Component two = addComponent( "two", realmTwo );

        ClassRealm realmOne = container.createChildRealm( "one" );
        Component one = addComponent( "one", realmOne );

        ClassRealm realmThree = container.createChildRealm( "three" );
        Component three = addComponent( "three", realmThree );

        // verify we have a normal 0-4 list now
        assertZeroToFourList( asList( zero, one, two, three, four ), list );

        // remove realm 4 which contains component 4
        container.removeComponentRealm( realmFour );

        // verify we now have a 0-3 list
        assertEquals( asList( zero, one, two, three ), list );

        // remove realm 2 which contains component 2
        container.removeComponentRealm( realmTwo );

        // verify we now have a 0,1,3 list
        assertEquals( asList( zero, one, three ), list );

        // remove realm 0 which contains component 0
        container.removeComponentRealm( realmZero );

        // verify we now have a 1,3 list
        assertEquals( asList( one, three ), list );

        // add them back
        zero = addComponent( "zero", realmZero );
        four = addComponent( "four", realmFour );
        two = addComponent( "two", realmTwo );

        // verify we have a normal 0-4 list again
        assertZeroToFourList( asList( zero, one, two, three, four ), list );
    }

    private void assertZeroToFourList( List<Component> expected, LiveList<Component> list )
    {
        // get input values
        assertEquals( 5, expected.size() );
        Component zero = expected.get( 0 );
        Component one = expected.get( 1 );
        Component two = expected.get( 2 );
        Component three = expected.get( 3 );
        Component four = expected.get( 4 );

        // isEmpty and size
        assertFalse( list.isEmpty() );
        assertEquals( 5, list.size() );

        // get
        assertSame( zero, list.get( 0 ) );
        assertSame( one, list.get( 1 ) );
        assertSame( two, list.get( 2 ) );
        assertSame( three, list.get( 3 ) );
        assertSame( four, list.get( 4 ) );
        assertGetOutOfBounds( list, -1 );
        assertGetOutOfBounds( list, 5 );

        // contains
        assertTrue( list.contains( zero ) );
        assertTrue( list.contains( two ) );
        assertTrue( list.contains( four ) );
        assertTrue( list.containsAll( asList( zero ) ) );
        assertTrue( list.containsAll( asList( zero, two, four ) ) );
        assertTrue( list.containsAll( expected ) );

        // indexOfs
        assertEquals( 0, list.indexOf( zero ) );
        assertEquals( 0, list.lastIndexOf( zero ) );
        assertEquals( 2, list.indexOf( two ) );
        assertEquals( 2, list.lastIndexOf( two ) );
        assertEquals( 4, list.indexOf( four ) );
        assertEquals( 4, list.lastIndexOf( four ) );

        // subList
        assertEquals( asList( zero ), list.subList( 0, 1 ) );
        assertEquals( asList( zero, one, two ), list.subList( 0, 3 ) );
        assertEquals( expected, list.subList( 0, 5 ) );
        assertSubListOutOfBounds( list, 0, 6 );
        assertSubListOutOfBounds( list, -1, 1 );

        // iterators
        assertTrue( Iterators.elementsEqual( expected.iterator(), list.iterator() ) );
        assertTrue( Iterators.elementsEqual( expected.listIterator(), list.listIterator() ) );
        assertTrue( Iterators.elementsEqual( expected.listIterator(), list.listIterator( 0 ) ) );
        assertTrue( Iterators.elementsEqual( expected.listIterator( 2 ), list.listIterator( 2 ) ) );
        assertTrue( Iterators.elementsEqual( emptyList().listIterator(), list.listIterator( 5 ) ) );
        assertListIteratorOutOfBounds( list, -1 );
        assertListIteratorOutOfBounds( list, 6 );

        // toArrays
        assertTrue( Arrays.equals( expected.toArray(), list.toArray() ) );
        assertTrue( Arrays.equals( expected.toArray( new Component[5] ), list.toArray( new Component[5] ) ) );

        // hashCode, equals and toString
        assertEquals( expected.hashCode(), list.hashCode() );
        assertTrue( list.equals( expected ) );
        assertNotNull( list.toString() );
        assertTrue( list.toString().length() > 0 );

        // List must be immutable
        assertImmutable( list );
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

    private static void assertGetOutOfBounds( List<Component> list, int index )
    {
        try
        {
            list.get( index );
            fail( "Expected IndexOutOfBoundsException from list.get(" + index + ")" );
        }
        catch ( IndexOutOfBoundsException expected )
        {
        }
    }

    private static void assertSubListOutOfBounds( List<Component> list, int fromIndex, int toIndex )
    {
        try
        {
            list.subList( fromIndex, toIndex );
            fail( "Expected IndexOutOfBoundsException from list.subList(" + fromIndex + "," + toIndex + ")" );
        }
        catch ( IndexOutOfBoundsException expected )
        {
        }
    }

    private static void assertListIteratorOutOfBounds( List<Component> list, int index )
    {
        try
        {
            list.listIterator( index );
            fail( "Expected IndexOutOfBoundsException from list.listIterator(" + index + ")" );
        }
        catch ( IndexOutOfBoundsException expected )
        {
        }
    }

    private static void assertImmutable( List<Component> list )
    {
        Component component = new Component( "component" );

        try
        {
            list.set( 0, component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.add( component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.add( 0, component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.addAll( asList( component ) );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.addAll( 0, asList( component ) );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.remove( component );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.remove( 0 );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.removeAll( asList( component ) );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.retainAll( asList( component ) );
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( UnsupportedOperationException expected )
        {
        }

        try
        {
            list.clear();
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
