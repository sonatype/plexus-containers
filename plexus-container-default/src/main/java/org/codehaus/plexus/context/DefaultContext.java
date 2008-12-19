package org.codehaus.plexus.context;

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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation of Context.
 *
 * This implementation is a static hierarchial store. It has the normal <code>get()</code>
 * and <code>put</code> methods. The <code>hide</code> method will hide a property. When
 * a property has been hidden the containerContext will not search in the parent containerContext for the value.
 *
 * @author <a href="mailto:dev@avalon.codehaus.org">Avalon Development Team</a>
 * @version $Id$
 */
public class DefaultContext
    implements Context
{
    /**
     * Context data.
     */
    private final ConcurrentMap<Object, Object> contextData = new ConcurrentHashMap<Object, Object>();

    /**
     * Is the containerContext read only.
     */
    private final AtomicBoolean readOnly = new AtomicBoolean(false);

    /**
     * Create an empty Context.
     */
    public DefaultContext()
    {
    }

    /**
     * Create a Context with specified data.  The specified data is copied into the context so any subsequent updates
     * to supplied map are not reflected in this context.  Additionally, changes to this context are not reflected in
     * the specified map.
     *
     * @param contextData the containerContext data
     */
    public DefaultContext( Map<Object, Object> contextData )
    {
        if ( contextData == null )
        {
            throw new NullPointerException( "contextData is null" );
        }
        this.contextData.putAll(contextData);
    }

    public boolean contains( Object key )
    {
        Object data = contextData.get( key );

        return data != null;
    }

    public Object get( Object key )
        throws ContextException
    {
        Object data = contextData.get( key );

        if ( data == null )
        {
            // There is no data for the key
            throw new ContextException( "Unable to resolve context key: " + key );
        }
        return data;
    }

    public void put( Object key, Object value )
        throws IllegalStateException
    {
        checkWriteable();

        if ( value == null )
        {
            contextData.remove( key );
        }
        else
        {
            contextData.put( key, value );
        }
    }

    public void hide( Object key )
        throws IllegalStateException
    {
        checkWriteable();
        
        contextData.remove( key );
    }

    /**
     * Utility method to retrieve containerContext data
     *
     * @return the containerContext data
     */
    public Map getContextData()
    {
        return Collections.unmodifiableMap( contextData );

    }

    /**
     * Make the containerContext read-only.
     * Any attempt to write to the containerContext via put()
     * will result in an IllegalStateException.
     */
    public void makeReadOnly()
    {
        readOnly.set( true );
    }

    /**
     * Utility method to check if containerContext is writeable and if not throw exception.
     *
     * @throws java.lang.IllegalStateException if containerContext is read only
     */
    protected void checkWriteable()
        throws IllegalStateException
    {
        if ( readOnly.get() )
        {
            throw new IllegalStateException( "Context is read only and can not be modified" );
        }
    }
    
    public String toString()
    {
        return contextData.toString();
    }
}
