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

public interface Context
{
    /**
     * Returns the value of the key. If the key can't be found it will throw a exception.
     * 
     * @param key The key of the value to look up.
     * @return Returns 
     * @throws ContextException If the key doesn't exist.
     */    
    Object get( Object key )
        throws ContextException;
    
    /**
     * Returns true if the map or the parent map contains the key.
     * 
     * @param key The key to search for.
     * @return Returns true if the key was found.
     */
    boolean contains( Object key );

    /**
     * Adds the item to the containerContext.
     *
     * @param key the key of the item
     * @param value the item
     * @throws java.lang.IllegalStateException if containerContext is read only
     */
    public void put( Object key, Object value )throws IllegalStateException;

    /**
     * Hides the item in the containerContext.
     * After remove(key) has been called, a get(key)
     * will always fail, even if the parent containerContext
     * has such a mapping.
     *
     * @param key the items key
     * @throws java.lang.IllegalStateException if containerContext is read only
     */
    void hide( Object key )
        throws IllegalStateException;

    /**
     * Make the containerContext read-only.
     * Any attempt to write to the containerContext via put()
     * will result in an IllegalStateException.
     */
    void makeReadOnly();
}
