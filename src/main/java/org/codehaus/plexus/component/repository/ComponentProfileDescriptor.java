package org.codehaus.plexus.component.repository;

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

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentProfileDescriptor
{
    /** Component Factory Id. */
    private String componentFactoryId;

    /** Lifecycle Handler Id. */
    private String lifecycleHandlerId;

    /** Component Manager Id. */
    private String componentManagerId;
        
    /** Component Composer Id. */
    private String componentComposerId;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public String getComponentFactoryId()
    {
        return componentFactoryId;
    }

    public void setComponentFactoryId( String componentFactoryId )
    {
        this.componentFactoryId = componentFactoryId;
    }

    public String getLifecycleHandlerId()
    {
        return lifecycleHandlerId;
    }

    public void setLifecycleHandlerId( String lifecycleHandlerId )
    {
        this.lifecycleHandlerId = lifecycleHandlerId;
    }

    public String getComponentManagerId()
    {
        return componentManagerId;
    }

    public void setComponentManagerId( String componentManagerId )
    {
        this.componentManagerId = componentManagerId;
    }
    
    
    public String getComponentComposerId()
    {
        return componentComposerId;
    }
    
    public void setComponentComposerId( String componentComposerId )
    {
        this.componentComposerId = componentComposerId;
    }
}
