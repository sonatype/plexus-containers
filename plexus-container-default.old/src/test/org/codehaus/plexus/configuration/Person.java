package org.codehaus.plexus.configuration;

import org.apache.avalon.framework.configuration.Configuration;

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

    private Configuration configuration;

    private String occupation;

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public String getOccupation()
    {
        return occupation;
    }
}
