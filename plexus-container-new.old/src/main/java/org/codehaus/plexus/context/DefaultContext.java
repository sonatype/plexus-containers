package org.codehaus.plexus.context;

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
    /** */
    private static Hidden HIDDEN_MAKER = new Hidden();

    /** Context data. */
    private Map contextData;

    /** Parent Context. */
    private Context parent;

    /** Is the context read only. */
    private boolean readOnly;

    /**
     * Create a Context with specified data and parent.
     *
     * @param contextData the context data
     * @param parent the parent Context (may be null)
     */
    public DefaultContext( Map contextData, Context parent )
    {
        this.parent = parent;

        this.contextData = contextData;
    }

    /**
     * Create a Context with specified data.
     *
     * @param contextData the context data
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
     * Create a Context with no parent.
     *
     */
    public DefaultContext()
    {
        this( (Context) null );
    }

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


    public Object get( Object key )
        throws ContextException
    {
        Object data = contextData.get( key );

        if ( null != data )
        {
            if ( data instanceof Hidden )
            {
                // Always fail.
                String message = "Unable to locate " + key;
                throw new ContextException( message );
            }

            return data;
        }

        // If data was null, check the parent
        if ( null == parent )
        {
            // There was no parent, and no data
            String message =
                "Unable to resolve context key: " + key;
            throw new ContextException( message );
        }

        return parent.get( key );
    }

    /**
     * Helper method for adding items to Context.
     *
     * @param key the items key
     * @param value the item
     * @throws java.lang.IllegalStateException if context is read only
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
     * Hides the item in the context.
     * After remove(key) has been called, a get(key)
     * will always fail, even if the parent context
     * has such a mapping.
     *
     * @param key the items key
     * @throws java.lang.IllegalStateException if context is read only
     */
    public void hide( Object key )
        throws IllegalStateException
    {
        checkWriteable();
        contextData.put( key, HIDDEN_MAKER );
    }

    /**
     * Utility method to retrieve context data.
     *
     * @return the context data
     */
    protected Map getContextData()
    {
        return contextData;
    }

    /**
     * Get parent context if any.
     *
     * @return the parent Context (may be null)
     */
    protected Context getParent()
    {
        return parent;
    }

    /**
     * Make the context read-only.
     * Any attempt to write to the context via put()
     * will result in an IllegalStateException.
     */
    public void makeReadOnly()
    {
        readOnly = true;
    }

    /**
     * Utility method to check if context is writeable and if not throw exception.
     *
     * @throws java.lang.IllegalStateException if context is read only
     */
    protected void checkWriteable()
        throws IllegalStateException
    {
        if ( readOnly )
        {
            String message =
                "Context is read only and can not be modified";
            throw new IllegalStateException( message );
        }
    }

    private static class Hidden
        implements Serializable
    {
    }

}
