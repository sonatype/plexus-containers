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
            Object value = context.get( key );
            
            if ( value instanceof String )
            {
                return value;
            }
            else
            {
                return null;
            }
        }
        catch ( ContextException e )
        {
            return null;
        }
    }
}
