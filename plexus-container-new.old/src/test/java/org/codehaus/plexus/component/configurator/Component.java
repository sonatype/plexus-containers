package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class Component
{
    private int intValue;
    private float floatValue;
    private long longValue;
    private double doubleValue;
    private String stringValue;
    private List importantThings;
    private PlexusConfiguration configuration;

    public int getIntValue()
    {
        return intValue;
    }

    public float getFloatValue()
    {
        return floatValue;
    }

    public long getLongValue()
    {
        return longValue;
    }

    public double getDoubleValue()
    {
        return doubleValue;
    }

    public String getStringValue()
    {
        return stringValue;
    }

    public List getImportantThings()
    {
        return importantThings;
    }

    public PlexusConfiguration getConfiguration()
    {
        return configuration;
    }
}
