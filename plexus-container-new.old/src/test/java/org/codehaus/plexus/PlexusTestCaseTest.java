package org.codehaus.plexus;

import junit.framework.TestCase;

import java.io.File;

/**
 *
 * 
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

        try
        {
            tc.setUp();

            fail( "IllegalStateException should be thrown." );
        }
        catch( IllegalStateException e )
        {
            // do nothing
        }

        try
        {
            tc.lookup( "foo", "bar" );

            fail( "Exception should be thrown." );
        }
        catch( Exception e )
        {
            // do nothing.
        }


        assertNotNull( tc.getClassLoader() );


        File file = new File( tc.getTestFile( "project.xml" ) );

        assertTrue( file.exists() );


        file = new File( tc.getTestFile( basedir, "project.xml" ) );

        assertTrue( file.exists() );
    }
}
