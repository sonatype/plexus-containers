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

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

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
    /** */
    private static Hidden HIDDEN_MAKER = new Hidden();

    /** Context data. */
    private Map contextData;

    /** Parent Context. */
    private Context parent;

    /** Is the containerContext read only. */
    private boolean readOnly;

    /**
     * Create a Context with specified data and parent.
     *
     * @param contextData the containerContext data
     * @param parent the parent Context (may be null)
     */
    public DefaultContext( Map contextData, Context parent )
    {
        this.parent = parent;

        this.contextData = contextData;
    }

    /**
     * Create a empty Context with specified data.
     *
     * @param contextData the containerContext data
     */
    public DefaultContext( Map contextData )
    {
        this( contextData, null );
    }

    /**
     * Create a Context with specified parent.
     *
     * @param parent the parent Context (may be null)
     */
    public DefaultContext( Context parent )
    {
        this( new Hashtable(), parent );
    }

    /**
     * Create a empty Context with no parent.
     */
    public DefaultContext()
    {
        this( (Context) null );
    }

    /**
     * Returns true if the map or the parent map contains the key.
     * 
     * @param key The key to search for.
     * @return Returns true if the key was found.
     */
    public boolean contains( Object key )
    {

        Object data = contextData.get( key );

        if ( null != data )
        {
            if ( data instanceof Hidden )
            {
                return false;
            }

            return true;
        }

        // If data was null, check the parent
        if ( null == parent )
        {
            return false;
        }

        return parent.contains( key );
    }

    /**
     * Returns the value of the key. If the key can't be found it will throw a exception.
     * 
     * @param key The key of the value to look up.
     * @return Returns 
     * @throws ContextException If the key doesn't exist.
     */
    public Object get( Object key )
        throws ContextException
    {
        Object data = contextData.get( key );

        if ( data != null )
        {
            if ( data instanceof Hidden )
            {
                // Always fail
                throw new ContextException( "Unable to locate " + key );
            }

            return data;
        }

        // If data was null, check the parent
        if ( parent == null )
        {
            // There was no parent, and no data
            throw new ContextException( "Unable to resolve containerContext key: " + key );
        }

        return parent.get( key );
    }

    /**
     * Helper method for adding items to Context.
     *
     * @param key the items key
     * @param value the item
     * @throws java.lang.IllegalStateException if containerContext is read only
     */
    public void put( Object key, Object value )
        throws IllegalStateException
    {
        checkWriteable();

        if ( null == value )
        {
            contextData.remove( key );
        }
        else
        {
            contextData.put( key, value );
        }
    }

    /**
     * Hides the item in the containerContext.
     * 
     * After remove(key) has been called, a get(key)
     * will always fail, even if the parent containerContext
     * has such a mapping.
     *
     * @param key the items key
     * @throws java.lang.IllegalStateException if containerContext is read only
     */
    public void hide( Object key )
        throws IllegalStateException
    {
        checkWriteable();
        contextData.put( key, HIDDEN_MAKER );
    }

    /**
     * Utility method to retrieve containerContext data.
     *
     * @return the containerContext data
     */
    public Map getContextData()
    {
        return contextData;
    }

    /**
     * Get parent containerContext if any.
     *
     * @return the parent Context (may be null)
     */
    protected Context getParent()
    {
        return parent;
    }

    /**
     * Make the containerContext read-only.
     * Any attempt to write to the containerContext via put()
     * will result in an IllegalStateException.
     */
    public void makeReadOnly()
    {
        readOnly = true;
    }

    /**
     * Utility method to check if containerContext is writeable and if not throw exception.
     *
     * @throws java.lang.IllegalStateException if containerContext is read only
     */
    protected void checkWriteable()
        throws IllegalStateException
    {
        if ( readOnly )
        {
            throw new IllegalStateException( "Context is read only and can not be modified" );
        }
    }

    /**
     * This class is only used as a marker in the map to indicate a hidden value.
     */
    private static class Hidden
        implements Serializable
    {
    }
}
