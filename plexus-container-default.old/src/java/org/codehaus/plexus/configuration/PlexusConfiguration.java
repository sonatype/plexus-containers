package org.codehaus.plexus.configuration;

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
