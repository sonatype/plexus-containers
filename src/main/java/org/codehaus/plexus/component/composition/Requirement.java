package org.codehaus.plexus.component.composition;

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

import java.util.List;

/**
 * @author Jason van Zyl
 * @version $Revision: Requirement.java 3041 2006-02-14 17:14:36Z jvanzyl $
 */
public class Requirement
{
    private Object assignment;

    private List componentDescriptors;

    public Requirement( Object assignment, List componentDescriptors )
    {
        this.assignment = assignment;

        this.componentDescriptors = componentDescriptors;
    }

    public Object getAssignment()
    {
        return assignment;
    }

    public List getComponentDescriptors()
    {
        return componentDescriptors;
    }
}
