package org.codehaus.plexus.configuration.xstream;

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

    private org.codehaus.plexus.configuration.Configuration configuration;

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

    public org.codehaus.plexus.configuration.Configuration getConfiguration()
    {
        return configuration;
    }

    public String getOccupation()
    {
        return occupation;
    }
}
