package org.codehaus.plexus.component.configurator.converters.basic;

public class ByteConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( byte.class ) || type.equals( Byte.class );
    }

    public Object fromString( String str )
    {
        return new Byte( (byte) Integer.parseInt( str ) );
    }

}
