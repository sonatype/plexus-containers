package org.codehaus.plexus.hierarchy;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Test for {@link org.codehaus.plexus.ComponentPlexusContainer}.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 */
public class PlexusHierarchyTest
    extends PlexusTestCase
{
    public PlexusHierarchyTest( String testName )
    {
        super( testName );
    }

    public void testPlexus()
        throws Exception
    {
        Object service;

        PlexusContainer plexus;

        TestService testService;

        service = lookup( PlexusContainer.ROLE );

        assertNotNull( service );

        plexus = (PlexusContainer) service;

        assertTrue( plexus.hasComponent( TestService.ROLE ) );

        service = plexus.lookup( TestService.ROLE );

        assertNotNull( service );

        testService = (TestService) service;

        assertEquals( "ChildPlexusOne", testService.getPlexusName() );

        assertEquals( "three blind mice", testService.getKnownValue() );
    }

    public void testPlexusWithId()
        throws Exception
    {
        Object service;
        PlexusContainer plexus;
        TestService testService;

        service = lookup( PlexusContainer.ROLE, "two" );
        assertNotNull( service );
        plexus = (PlexusContainer) service;
        assertTrue( plexus.hasComponent( TestService.ROLE ) );
        service = plexus.lookup( TestService.ROLE );
        assertNotNull( service );
        testService = (TestService) service;

        assertEquals( "ChildPlexusTwo", testService.getPlexusName() );

        assertEquals( "see how they run", testService.getKnownValue() );
    }

    public void testSiblingPlexusResolution()
        throws Exception
    {
        Object service;
        PlexusContainer plexus;
        TestService testService;

        service = lookup( PlexusContainer.ROLE );
        assertNotNull( service );
        plexus = (PlexusContainer) service;
        assertTrue( plexus.hasComponent( TestService.ROLE ) );
        service = plexus.lookup( TestService.ROLE );
        assertNotNull( service );
        testService = (TestService) service;
        assertEquals( "see how they run", testService.getSiblingKnownValue( "two" ) );
    }
}
