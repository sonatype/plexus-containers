package org.codehaus.plexus.component.configurator.converters.basic;

public class DoubleConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( double.class ) || type.equals( Double.class );
    }

    public Object fromString( String str )
    {
        return Double.valueOf( str );
    }

}
