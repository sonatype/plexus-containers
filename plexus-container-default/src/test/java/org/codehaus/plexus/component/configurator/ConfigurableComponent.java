package org.codehaus.plexus.component.configurator;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ConfigurableComponent
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
