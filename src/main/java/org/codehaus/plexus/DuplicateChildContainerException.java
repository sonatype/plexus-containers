package org.codehaus.plexus;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

public class DuplicateChildContainerException
    extends PlexusContainerException
{

    private final String parent;
    private final String child;

    public DuplicateChildContainerException( String parent, String child )
    {
        super( "Cannot create child container, because child named \'" + child + "\' already exists in parent \'" + parent + "\'." );

        this.parent = parent;

        this.child = child;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public String getChild()
    {
        return child;
    }
    
}
