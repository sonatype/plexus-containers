package org.codehaus.plexus.component.configurator.converters.basic;

public class ShortConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( short.class ) || type.equals( Short.class );
    }

    public Object fromString( String str )
    {
        return Short.valueOf( str );
    }

}
