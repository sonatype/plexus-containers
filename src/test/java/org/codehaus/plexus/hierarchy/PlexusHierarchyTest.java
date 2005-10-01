package org.codehaus.plexus.hierarchy;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerManager;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test for {@link org.codehaus.plexus.SimplePlexusContainerManager},
 * and the hierarchical behaviour of
 * {@link org.codehaus.plexus.DefaultPlexusContainer}.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 */
public class PlexusHierarchyTest
    extends PlexusTestCase
{
    private PlexusContainer rootPlexus;

    private PlexusContainer childPlexus;

    private PlexusContainer childPlexus2;

    private PlexusTestService testService;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        PlexusContainerManager manager;

        manager = (PlexusContainerManager) lookup( PlexusContainerManager.ROLE );

        rootPlexus = getContainer();

        childPlexus = manager.getManagedContainers()[0];

        manager = (PlexusContainerManager) lookup( PlexusContainerManager.ROLE, "two" );

        childPlexus2 = manager.getManagedContainers()[0];
    }

    protected void customizeContext()
        throws Exception
    {
        getContainer().addContextValue( "plexus-name", "root" );
    }

    public void testPlexus()
        throws Exception
    {
        assertTrue( childPlexus.hasComponent( PlexusTestService.ROLE ) );

        testService = (PlexusTestService) childPlexus.lookup( PlexusTestService.ROLE );

        assertEquals( "ChildPlexusOne", testService.getPlexusName() );

        assertEquals( "three blind mice", testService.getKnownValue() );
    }

    public void testPlexusWithId()
        throws Exception
    {
        assertTrue( childPlexus2.hasComponent( PlexusTestService.ROLE ) );

        testService = (PlexusTestService) childPlexus2.lookup( PlexusTestService.ROLE );

        assertEquals( "ChildPlexusTwo", testService.getPlexusName() );

        assertEquals( "see how they run", testService.getKnownValue() );
    }

    /*
     * Test that components can find components in other containers, if they
     * know how to navigate the tree of containers. This technique should be
     * considered a hack as it ties a component to a certain container
     * configuration.
     */
    public void testSiblingPlexusResolution()
        throws Exception
    {
        assertTrue( childPlexus.hasComponent( PlexusTestService.ROLE ) );

        testService = (PlexusTestService) childPlexus.lookup( PlexusTestService.ROLE );

        assertEquals( "see how they run", testService.getSiblingKnownValue( "two" ) );

        assertTrue( childPlexus2.hasComponent( PlexusTestService.ROLE ) );

        testService = (PlexusTestService) childPlexus2.lookup( PlexusTestService.ROLE );

        assertEquals( "three blind mice", testService.getSiblingKnownValue( null ) );
    }

    public void testLookups()
        throws Exception
    {
        // Child plexus should override the component with no role-hint

        testService = (PlexusTestService) lookup( PlexusTestService.ROLE );

        assertEquals( "cheesy default service", testService.getKnownValue() );

        release( testService );

        testService = (PlexusTestService) childPlexus.lookup( PlexusTestService.ROLE );

        assertEquals( "three blind mice", testService.getKnownValue() );

        childPlexus.release( testService );

        // Child plexus one should override the hinted component

        testService = (PlexusTestService) lookup( PlexusTestService.ROLE, "hinted" );

        assertEquals( "hinted default service", testService.getKnownValue() );

        release( testService );

        testService = (PlexusTestService) childPlexus.lookup( PlexusTestService.ROLE, "hinted" );

        assertEquals( "plexus one overriding hinted service", testService.getKnownValue() );

        childPlexus.release( testService );

        // Child plexus two should inherit the hinted component

        testService = (PlexusTestService) lookup( PlexusTestService.ROLE, "hinted" );

        assertEquals( "hinted default service", testService.getKnownValue() );

        release( testService );

        testService = (PlexusTestService) childPlexus2.lookup( PlexusTestService.ROLE, "hinted" );

        assertEquals( "hinted default service", testService.getKnownValue() );

        childPlexus2.release( testService );

        // Child plexus should provide access to global component from
        // parent plexus.

        testService = (PlexusTestService) lookup( PlexusTestService.ROLE, "global" );

        assertEquals( "globally visible service", testService.getKnownValue() );

        release( testService );

        testService = (PlexusTestService) childPlexus.lookup( PlexusTestService.ROLE, "global" );

        assertEquals( "globally visible service", testService.getKnownValue() );

        childPlexus.release( testService );

        // Child plexus should provide access to components local to the
        // container.

        try
        {
            testService = (PlexusTestService) lookup( PlexusTestService.ROLE, "local" );

            fail( "found child component through parent container" );
        }
        catch ( ComponentLookupException e )
        {
            assertTrue( true );
        }

        testService = (PlexusTestService) childPlexus.lookup( PlexusTestService.ROLE, "local" );

        assertEquals( "plexus one local service", testService.getKnownValue() );

        childPlexus.release( testService );
    }

    public void testLookupMap()
        throws Exception
    {
        // Root plexus should give us a map containing its components.

        Map componentMap = rootPlexus.lookupMap( PlexusTestService.ROLE );

        assertEquals( 3, componentMap.size() );

        testService = (PlexusTestService) componentMap.get( "global" );

        assertEquals( "globally visible service", testService.getKnownValue() );

        testService = (PlexusTestService) componentMap.get( "hinted" );

        assertEquals( "hinted default service", testService.getKnownValue() );

        assertFalse( componentMap.containsKey( "local" ) );

        rootPlexus.releaseAll( componentMap );

        // Child plexus one should give us a map containing its components
        // and those from its parent.

        componentMap = childPlexus.lookupMap( PlexusTestService.ROLE );

        assertEquals( 4, componentMap.size() );

        testService = (PlexusTestService) componentMap.get( "global" );

        assertEquals( "globally visible service", testService.getKnownValue() );

        testService = (PlexusTestService) componentMap.get( "hinted" );

        assertEquals( "plexus one overriding hinted service", testService.getKnownValue() );

        testService = (PlexusTestService) componentMap.get( "local" );

        assertEquals( "plexus one local service", testService.getKnownValue() );

        childPlexus.releaseAll( componentMap );

        // Child plexus two should give us a map containing its components
        // and those from its parent.

        componentMap = childPlexus2.lookupMap( PlexusTestService.ROLE );

        assertEquals( 4, componentMap.size() );

        testService = (PlexusTestService) componentMap.get( "global" );

        assertEquals( "globally visible service", testService.getKnownValue() );

        testService = (PlexusTestService) componentMap.get( "hinted" );

        assertEquals( "hinted default service", testService.getKnownValue() );

        testService = (PlexusTestService) componentMap.get( "local" );

        assertEquals( "plexus two local service", testService.getKnownValue() );

        childPlexus2.releaseAll( componentMap );
    }

    public void testLookupList()
        throws Exception
    {
        // Root plexus should give us a list containing its components.

        List componentList = rootPlexus.lookupList( PlexusTestService.ROLE );

        assertEquals( 3, componentList.size() );

        String[] strings = new String[]
        {
            "cheesy default service",
            "hinted default service",
            "globally visible service",
        };

        Set expectedValues = new HashSet( Arrays.asList( strings ) );

        for ( Iterator i = componentList.iterator(); i.hasNext(); )
        {
            testService = (PlexusTestService) i.next();

            expectedValues.remove( testService.getKnownValue() );
        }

        assertEquals( 0, expectedValues.size() );

        rootPlexus.releaseAll( componentList );

        // Child plexus one should give us a list containing its components
        // and those from its parent.

        componentList = childPlexus.lookupList( PlexusTestService.ROLE );

        assertEquals( 4, componentList.size() );

        strings = new String[]
        {
            "globally visible service",
            "three blind mice",
            "plexus one overriding hinted service",
            "plexus one local service",
        };

        expectedValues = new HashSet( Arrays.asList( strings ) );

        for ( Iterator i = componentList.iterator(); i.hasNext(); )
        {
            testService = (PlexusTestService) i.next();

            expectedValues.remove( testService.getKnownValue() );
        }

        assertEquals( 0, expectedValues.size() );

        childPlexus.releaseAll( componentList );

        // Child plexus two should give us a map containing its components
        // and those from its parent.

        componentList = childPlexus2.lookupList( PlexusTestService.ROLE );

        assertEquals( 4, componentList.size() );

        strings = new String[]
        {
            "hinted default service",
            "globally visible service",
            "see how they run",
            "plexus two local service",
        };

        expectedValues = new HashSet( Arrays.asList( strings ) );

        for ( Iterator i = componentList.iterator(); i.hasNext(); )
        {
            testService = (PlexusTestService) i.next();

            expectedValues.remove( testService.getKnownValue() );
        }

        assertEquals( 0, expectedValues.size() );

        childPlexus2.releaseAll( componentList );
    }
}
