package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class GoalDescriptor
{
    private String name;

    private XmlPlexusConfiguration configuration;

    private List prereqs;

    private String description;

    public String getName()
    {
        return name;
    }

    public XmlPlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public List getPrereqs()
    {
        return prereqs;
    }

    public String getDescription()
    {
        return description;
    }
}