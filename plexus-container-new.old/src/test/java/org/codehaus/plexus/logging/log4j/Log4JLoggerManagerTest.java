package org.codehaus.plexus.logging.log4j;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;
import org.codehaus.plexus.logging.LoggerManagerFactory;
import org.codehaus.plexus.DefaultPlexusContainer;

import java.io.InputStreamReader;
import java.util.Properties;

public class Log4JLoggerManagerTest
    extends TestCase
{
    public Log4JLoggerManagerTest( String name )
    {
        super( name );
    }

    public void testLoggerManager()
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
}
