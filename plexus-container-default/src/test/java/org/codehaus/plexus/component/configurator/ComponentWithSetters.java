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
 * @version $Id: ConfigurableComponent.java 1323 2004-12-20 23:00:59Z jvanzyl $
 */
public class ComponentWithSetters
{
    private int intValueVariable;
    private float floatValueVariable;
    private long longValueVariable;
    private double doubleValueVariable;
    private String stringValueVariable;
    private List importantThingsVariable;
    private PlexusConfiguration configurationVariable;

    public int getIntValue()
    {
        return intValueVariable;
    }
                                          
    public float getFloatValue()
    {
        return floatValueVariable;
    }

    public long getLongValue()
    {
        return longValueVariable;
    }

    public double getDoubleValue()
    {
        return doubleValueVariable;
    }

    public String getStringValue()
    {
        return stringValueVariable;
    }

    public List getImportantThings()
    {
        return importantThingsVariable;
    }

    public PlexusConfiguration getConfiguration()
    {
        return configurationVariable;
    }

    // ----------------------------------------------------------------------
    // setters
    // ----------------------------------------------------------------------

    boolean intValueSet;
    boolean floatValueSet;
    boolean longValueSet;
    boolean doubleValueSet;
    boolean stringValueSet;
    boolean importantThingsValueSet;
    boolean configurationValueSet;

    public void setIntValue( int intValue )
    {
        this.intValueVariable = intValue;

        intValueSet = true;
    }

    public void setFloatValue( float floatValue )
    {
        this.floatValueVariable = floatValue;

        floatValueSet = true;
    }

    public void setLongValue( long longValue )
    {
        this.longValueVariable = longValue;

        longValueSet = true;
    }

    public void setDoubleValue( double doubleValue )
    {
        this.doubleValueVariable = doubleValue;

        doubleValueSet = true;
    }

    public void setStringValue( String stringValue )
    {
        this.stringValueVariable = stringValue;

        stringValueSet = true;
    }

    public void setImportantThings( List importantThings )
    {
        this.importantThingsVariable = importantThings;

        importantThingsValueSet = true;
    }

    public void setConfiguration( PlexusConfiguration configuration )
    {
        this.configurationVariable = configuration;

        configurationValueSet = true;
    }
}
