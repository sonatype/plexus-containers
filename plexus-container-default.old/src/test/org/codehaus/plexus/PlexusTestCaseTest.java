package org.codehaus.plexus;

import java.io.File;

import junit.framework.TestCase;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PlexusTestCaseTest
    extends TestCase
{
    private String basedir;

    public void setUp()
    {
        basedir = System.getProperty( "basedir" );
    }

    public void testPlexusTestCase()
        throws Exception
    {
        PlexusTestCase tc = new PlexusTestCase( "foo" );

        tc.setUp();

        try
        {
            tc.lookup( "foo", "bar" );

            fail( "Exception should be thrown." );
        }
        catch ( ComponentLookupException ex )
        {
            // do nothing.
        }

        // This component is discovered from src/test/META-INF/plexus/components.xml
        tc.lookup( "org.codehaus.plexus.component.discovery.DiscoveredComponent" );

        assertNotNull( tc.getClassLoader() );

        File file;

       // ----------------------------------------------------------------------
       // getTestFile()
       // ----------------------------------------------------------------------

        file = tc.getTestFile( "project.xml" );

        assertTrue( file.exists() );

        file = tc.getTestFile( basedir, "project.xml" );

        assertTrue( file.exists() );

        // ----------------------------------------------------------------------
        // getTestPath()
        // ----------------------------------------------------------------------

        file = new File( tc.getTestPath( "project.xml" ) );

        assertTrue( file.exists() );

        file = new File( tc.getTestPath( basedir, "project.xml" ) );

        assertTrue( file.exists() );

        tc.tearDown();
    }
}
