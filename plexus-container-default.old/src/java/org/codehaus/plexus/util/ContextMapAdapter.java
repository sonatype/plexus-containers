package org.codehaus.plexus.util;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

import java.util.HashMap;

public class ContextMapAdapter
    extends HashMap
{
    private Context context;

    public ContextMapAdapter( Context context )
    {
        this.context = context;
    }

    public Object get( Object key )
    {
        try
        {
            return context.get( key );
        }
        catch ( ContextException e )
        {
            return null;
        }
    }
}
