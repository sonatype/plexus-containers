package org.codehaus.plexus.component.configurator.converters.basic;

public class CharConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( char.class ) || type.equals( Character.class );
    }

    public Object fromString( String str )
    {
        return new Character( str.charAt( 0 ) );
    }

}
