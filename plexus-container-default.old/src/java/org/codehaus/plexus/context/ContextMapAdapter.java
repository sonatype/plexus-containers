package org.codehaus.plexus.context;


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
