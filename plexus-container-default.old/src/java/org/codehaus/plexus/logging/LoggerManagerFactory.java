package org.codehaus.plexus.logging;

import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.factory.AbstractPlexusFactory;

/**
 *
 */
public class LoggerManagerFactory
    extends AbstractPlexusFactory
{
    /** XML element used to select the logger manager implementation. */
    private static final String IMPLEMENTATION_TAG = "implementation";

    public static LoggerManager create( Configuration configuration,
                                        ClassLoader classLoader )
        throws Exception
    {
        String implementation = configuration.getChild( IMPLEMENTATION_TAG ).getValue( null );

        LoggerManager lm = (LoggerManager) getInstance( implementation, classLoader );

        lm.configure( configuration );

        lm.initialize();

        lm.start();

        return lm;
    }
}
