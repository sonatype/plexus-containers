package org.codehaus.plexus;

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

import junit.framework.TestCase;
import org.codehaus.plexus.component.discovery.DiscoveredComponent;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.test.DefaultLoadOnStartService;

import java.io.File;
import java.io.InputStream;

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
        
        if(basedir == null)
        {
            basedir = new File(".").getAbsolutePath();
        }
    }

    public void testPlexusTestCase()
        throws Exception
    {
        PlexusTestCase tc = new PlexusTestCase() {};

        tc.setUp();

        try
        {
            tc.lookup( "foo", "bar" );

            fail( "Expected ComponentLookupException." );
        }
        catch ( ComponentLookupException ex )
        {
            assertTrue( true );
        }

        // This component is discovered from src/test/META-INF/plexus/components.xml
        Object component = tc.lookup( "org.codehaus.plexus.component.discovery.DiscoveredComponent" );

        assertNotNull( component );

        assertTrue( component instanceof DiscoveredComponent );

        assertNotNull( tc.getClassLoader() );

        tc.tearDown();
    }

    public void testLoadOnStartComponents()
        throws Exception
    {
        final InputStream is = this.getClass().getClassLoader().getResourceAsStream( "org/codehaus/plexus/PlexusTestCaseTest.xml" );

        assertNotNull( "Missing configuration", is );

        PlexusTestCase tc = new PlexusTestCase() {
            protected InputStream getConfiguration()
                throws Exception
            {
                return is;
            }
        };

        tc.setUp();

        // Assert that the load on start component has started.

        assertTrue( "The load on start components haven't been started.", DefaultLoadOnStartService.isStarted );

        tc.tearDown();
    }

    public void testGetFile()
    {
        File file = PlexusTestCase.getTestFile( "pom.xml" );

        assertTrue( file.exists() );

        file = PlexusTestCase.getTestFile( basedir, "pom.xml" );

        assertTrue( file.exists() );
    }

    public void testGetPath()
    {
        File file = new File( PlexusTestCase.getTestPath( "pom.xml" ) );

        assertTrue( file.exists() );

        file = new File( PlexusTestCase.getTestPath( basedir, "pom.xml" ) );

        assertTrue( file.exists() );
    }
}
