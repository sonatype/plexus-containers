package org.codehaus.plexus.configuration.xml.xstream;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class Person
{
    private String id;

    private String name;

    private org.codehaus.plexus.configuration.PlexusConfiguration configuration;

    private String occupation;

    public Person()
    {
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public org.codehaus.plexus.configuration.PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public String getOccupation()
    {
        return occupation;
    }
}
