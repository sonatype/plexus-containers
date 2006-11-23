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

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:kenney@codehaus.org">Kenney Westerhof</a>
 * @version $Id$
 */
public class ComponentWithArrayFields
{
    private String[] stringArray;

    private Integer[] integerArray;

    private ImportantThing[] importantThingArray;

    private Object[] objectArray;

    private AbstractThing[] abstractArray;

    private URL[] urlArray;

    private File[] fileArray;

    private Class[] classArray;

    public String [] getStringArray()
    {
        return stringArray;
    }

    public Integer [] getIntegerArray()
    {
        return integerArray;
    }

    public ImportantThing [] getImportantThingArray()
    {
        return importantThingArray;
    }

    public Object [] getObjectArray()
    {
        return objectArray;
    }

    public AbstractThing [] getAbstractThingArray()
    {
        return abstractArray;
    }

    public URL[] getUrlArray()
    {
        return urlArray;
    }

    public File[] getFileArray()
    {
        return fileArray;
    }

    public Class[] getClassArray()
    {
        return classArray;
    }
}
