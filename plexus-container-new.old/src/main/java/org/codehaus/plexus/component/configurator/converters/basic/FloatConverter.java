package org.codehaus.plexus.component.configurator.converters.basic;

public class FloatConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( float.class ) || type.equals( Float.class );
    }

    public Object fromString( String str )
    {
        return Float.valueOf( str );
    }

}
