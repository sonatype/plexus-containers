package org.codehaus.plexus.logging;

import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.factory.AbstractPlexusFactory;

public class LoggerManagerFactory
    extends AbstractPlexusFactory
{
    /** XML element used to start the logging configuration block. */
    public static final String LOGGING_TAG = "logging";

    /** XML element used to select the logger manager implementation. */
    private static final String IMPLEMENTATION_TAG = "implementation";

    public static LoggerManager create( Configuration defaultConfiguration,
                                        Configuration configuration,
                                        ClassLoader classLoader )
        throws Exception
    {
        String implementation;
        boolean loggingWithNoImplementationSpecified = false;

        if ( configuration.getChild( LOGGING_TAG, false ) != null )
        {
            implementation =
                configuration.getChild( LOGGING_TAG ).getChild( IMPLEMENTATION_TAG ).getValue( null );

            if ( implementation == null
                ||
                implementation.trim().length() == 0 )
            {
                loggingWithNoImplementationSpecified = true;

                implementation =
                    defaultConfiguration.getChild( LOGGING_TAG ).getChild( IMPLEMENTATION_TAG ).getValue();
            }
        }
        else
        {
            implementation = defaultConfiguration.getChild( LOGGING_TAG ).getChild( IMPLEMENTATION_TAG ).getValue();
        }

        LoggerManager lm =
            (LoggerManager) getInstance( implementation, classLoader );

        lm.configure( configuration.getChild( LOGGING_TAG ) );
        lm.initialize();
        lm.start();

        if ( loggingWithNoImplementationSpecified )
        {
            lm.getRootLogger().warn( "A logging configuration was provided but no implementation was " +
                                     "specified so the system has fallen back to the console logger manager." );
        }

        return lm;
    }
}
