package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentConfigurationException
    extends Exception
{
    private PlexusConfiguration failedConfiguration;

    public ComponentConfigurationException( String message )
    {
        super( message );
    }

    public ComponentConfigurationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ComponentConfigurationException( Throwable cause )
    {
        super( cause );
    }
    
    public ComponentConfigurationException( PlexusConfiguration failedConfiguration, String message )
    {
        super( message );
        this.failedConfiguration = failedConfiguration;
    }

    public ComponentConfigurationException( PlexusConfiguration failedConfiguration, String message, Throwable cause )
    {
        super( message, cause );
        this.failedConfiguration = failedConfiguration;
    }

    public ComponentConfigurationException( PlexusConfiguration failedConfiguration, Throwable cause )
    {
        super( cause );
        this.failedConfiguration = failedConfiguration;
    }
    
    public void setFailedConfiguration( PlexusConfiguration failedConfiguration )
    {
        this.failedConfiguration = failedConfiguration;
    }
    
    public PlexusConfiguration getFailedConfiguration()
    {
        return failedConfiguration;
    }
}
