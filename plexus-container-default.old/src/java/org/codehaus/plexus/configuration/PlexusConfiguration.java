package org.codehaus.plexus.configuration;

import org.apache.avalon.framework.configuration.Configuration;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface PlexusConfiguration
    extends Configuration
{
    PlexusConfiguration getParent();

    void setParent( PlexusConfiguration configuration );

    int getChildCount();

    PlexusConfiguration getChild( int i );
}
