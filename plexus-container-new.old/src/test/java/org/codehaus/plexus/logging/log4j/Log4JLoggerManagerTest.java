package org.codehaus.plexus.logging.log4j;

import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.configuration.DefaultConfiguration;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;
import org.codehaus.plexus.logging.AbstractLoggerManagerTest;
import org.codehaus.plexus.logging.LoggerManager;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;

public class Log4JLoggerManagerTest
    extends AbstractLoggerManagerTest
{
    public void setUp()
    {
        super.setUp();

        String plexusHome = System.getProperty( "plexus.home" );

        File f = new File( plexusHome, "logs" );

        if ( !f.isDirectory() )
        {
            f.mkdir();
        }
    }

    protected Configuration createConfiguration(String threshold)
        throws Exception
    {
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        Configuration c = builder.parse( new InputStreamReader( Log4JLoggerManagerTest.class.getResourceAsStream( "plexus.conf" ) ) );

        if ( threshold.equals( "disabled" ) )
        {
            threshold = "off";
        }

        Configuration priorityNode = c.getChild( "logging" )
                                      .getChild( "logger" )
                                      .getChild( "priority" );
        
        ((DefaultConfiguration) priorityNode).setValue( threshold );

        return c.getChild( "logging" );
    }

    protected LoggerManager createLoggerManager() {
        return new Log4JLoggerManager();
    }

    public void testLog4JLoggerManagerWithRootLoggerProperties()
        throws Exception
    {
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        Log4JLoggerManager loggerManager = new Log4JLoggerManager();

        Configuration c = builder.parse( new InputStreamReader( Log4JLoggerManagerTest.class.getResourceAsStream( "plexus.conf" ) ) );

        loggerManager.configure( c.getChild( DefaultPlexusContainer.LOGGING_TAG ) );

        loggerManager.initialize();

        Properties p = loggerManager.getLog4JProperties();

        assertEquals( "INFO,default", p.getProperty( "log4j.rootLogger" ) );

        assertEquals( "org.apache.log4j.FileAppender", p.getProperty( "log4j.appender.default" ) );

        assertEquals( "org.apache.log4j.PatternLayout", p.getProperty( "log4j.appender.default.layout" ) );

        assertEquals( "true", p.getProperty( "log4j.appender.default.append" ) );

        assertEquals( "${plexus.home}/logs/plexus.log", p.getProperty( "log4j.appender.default.file" ) );

        assertEquals( "%-4r [%t] %-5p %c %x - %m%n", p.getProperty( "log4j.appender.default.layout.conversionPattern" ) );

        assertEquals( "INFO", p.getProperty( "log4j.appender.default.threshold" ) );
    }

    public void testLog4JLoggerManagerWithNoRootLoggerProperties()
        throws Exception
    {
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        Log4JLoggerManager loggerManager = new Log4JLoggerManager();

        Configuration c = builder.parse( new StringReader( "<logging></logging>" ) );

        loggerManager.configure( c );

        loggerManager.initialize();

        Properties p = loggerManager.getLog4JProperties();

        assertEquals( "INFO,console", p.getProperty( "log4j.rootLogger" ) );

        assertEquals( "org.apache.log4j.ConsoleAppender", p.getProperty( "log4j.appender.console" ) );

        assertEquals( "org.apache.log4j.PatternLayout", p.getProperty( "log4j.appender.console.layout" ) );

        assertEquals( "%-4r [%t] %-5p %c %x - %m%n", p.getProperty( "log4j.appender.console.layout.ConversionPattern" ) );

        // This is coming back with a trailing string for some reason
        assertEquals( "INFO", p.getProperty( "log4j.appender.console.threshold" ) );

        assertNotNull( loggerManager.getRootLogger() );

        assertNotNull( loggerManager.getLogger( "foo" ) );

        loggerManager.start();

        loggerManager.stop();
    }
}
