package org.codehaus.plexus.component.configurator.converters.basic;

public class LongConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( long.class ) || type.equals( Long.class );
    }

    public Object fromString( String str )
    {
        return Long.valueOf( str );
    }

}
