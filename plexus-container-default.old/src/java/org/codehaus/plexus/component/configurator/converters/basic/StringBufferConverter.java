package org.codehaus.plexus.component.configurator.converters.basic;

public class StringBufferConverter extends AbstractBasicConverter
{
    public boolean canConvert( Class type )
    {
        return type.equals( StringBuffer.class );
    }

    public Object fromString( String str )
    {
        return new StringBuffer( str );
    }
}
