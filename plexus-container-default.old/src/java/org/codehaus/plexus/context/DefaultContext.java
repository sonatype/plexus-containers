/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */

package org.codehaus.plexus.context;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Resolvable;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * Default implementation of Context.
 * This implementation is a static hierarchial store.
 *
 * @author <a href="mailto:dev@avalon.codehaus.org">Avalon Development Team</a>
 * @version CVS $Revision$ $Date$
 */
public class DefaultContext
    implements Context
{
    private static final class Hidden implements Serializable
    {
    }

    private static final Hidden HIDDEN_MAKER = new Hidden();

    private final Map m_contextData;
    private final Context m_parent;
    private boolean m_readOnly;

    /**
     * Create a Context with specified data and parent.
     *
     * @param contextData the context data
     * @param parent the parent Context (may be null)
     */
    public DefaultContext( final Map contextData, final Context parent )
    {
        m_parent = parent;
        m_contextData = contextData;
    }

    /**
     * Create a Context with specified data.
     *
     * @param contextData the context data
     */
    public DefaultContext( final Map contextData )
    {
        this( contextData, null );
    }

    /**
     * Create a Context with specified parent.
     *
     * @param parent the parent Context (may be null)
     */
    public DefaultContext( final Context parent )
    {
        this( new Hashtable(), parent );
    }

    /**
     * Create a Context with no parent.
     *
     */
    public DefaultContext()
    {
        this( (Context) null );
    }

    /**
     * Retrieve an item from the Context.
     *
     * @param key the key of item
     * @return the item stored in context
     * @throws org.apache.avalon.framework.context.ContextException if item not present
     */
    public Object get( final Object key )
        throws ContextException
    {
        final Object data = m_contextData.get( key );

        if ( null != data )
        {
            if ( data instanceof Hidden )
            {
                // Always fail.
                final String message = "Unable to locate " + key;
                throw new ContextException( message );
            }

            if ( data instanceof Resolvable )
            {
                return ( (Resolvable) data ).resolve( this );
            }

            return data;
        }

        // If data was null, check the parent
        if ( null == m_parent )
        {
            // There was no parent, and no data
            final String message =
                "Unable to resolve context key: " + key;
            throw new ContextException( message );
        }

        return m_parent.get( key );
    }

    /**
     * Helper method fo adding items to Context.
     *
     * @param key the items key
     * @param value the item
     * @throws java.lang.IllegalStateException if context is read only
     */
    public void put( final Object key, final Object value )
        throws IllegalStateException
    {
        checkWriteable();
        if ( null == value )
        {
            m_contextData.remove( key );
        }
        else
        {
            m_contextData.put( key, value );
        }
    }

    /**
     * Hides the item in the context.
     * After remove(key) has been called, a get(key)
     * will always fail, even if the parent context
     * has such a mapping.
     *
     * @param key the items key
     * @throws java.lang.IllegalStateException if context is read only
     */
    public void hide( final Object key )
        throws IllegalStateException
    {
        checkWriteable();
        m_contextData.put( key, HIDDEN_MAKER );
    }

    /**
     * Utility method to retrieve context data.
     *
     * @return the context data
     */
    protected final Map getContextData()
    {
        return m_contextData;
    }

    /**
     * Get parent context if any.
     *
     * @return the parent Context (may be null)
     */
    protected final Context getParent()
    {
        return m_parent;
    }

    /**
     * Make the context read-only.
     * Any attempt to write to the context via put()
     * will result in an IllegalStateException.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Utility method to check if context is writeable and if not throw exception.
     *
     * @throws java.lang.IllegalStateException if context is read only
     */
    protected final void checkWriteable()
        throws IllegalStateException
    {
        if ( m_readOnly )
        {
            final String message =
                "Context is read only and can not be modified";
            throw new IllegalStateException( message );
        }
    }
}
