package org.codehaus.plexus.classloader;

import junit.framework.TestCase;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.configuration.builder.XmlPullConfigurationBuilder;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultResourceManagerTest
    extends TestCase
{
    /** Base directory for tests. */
    private String basedir;

    public void setUp()
    {
        basedir = System.getProperty( "basedir" );
    }

    public void testResourceManager()
        throws Exception
    {
        File repo = new File( basedir, "src/test-input/jar-repository" );

        DefaultResourceManager rm = new DefaultResourceManager();

        rm.setClassRealm( new ClassWorld().newRealm("core", Thread.currentThread().getContextClassLoader()) );

        InputStream is = rm.getResourceAsStream( "org/codehaus/plexus/classloader/resource.xml" );

        assertNotNull( is );

        is.close();

        rm.addJarRepository( repo );

//        URL[] urls = rm.getURLs();
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/a.jar" ).toURL(), urls[0] );
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/b.jar" ).toURL(), urls[1] );
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/c.jar" ).toURL(), urls[2] );
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/d.jar" ).toURL(), urls[3] );
    }

    public void testResourceManagerWithConfiguration()
        throws Exception
    {
        File repo = new File( basedir, "src/test-input/jar-repository" );

        DefaultResourceManager rm = new DefaultResourceManager();

        rm.setClassRealm( new ClassWorld().newRealm("core", Thread.currentThread().getContextClassLoader()) );
        
        rm.enableLogging( new ConsoleLogger() );

        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        String xml = "<resources>" +
            "<jar-repository>" + repo.getPath() + "</jar-repository>" +
            "<jar-repository>non-existent-repo</jar-repository>" +
            "<nothing>nothing</nothing>" +
            "</resources>";

        PlexusConfiguration configuration = builder.parse( new StringReader( xml ) );

        rm.configure( configuration );

//        URL[] urls = rm.getURLs();
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/a.jar" ).toURL(), urls[0] );
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/b.jar" ).toURL(), urls[1] );
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/c.jar" ).toURL(), urls[2] );
//
//        assertEquals( new File( basedir, "src/test-input/jar-repository/d.jar" ).toURL(), urls[3] );
    }
}
