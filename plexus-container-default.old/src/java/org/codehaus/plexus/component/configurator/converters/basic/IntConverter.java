package org.codehaus.plexus.component.configurator.converters.basic;

public class IntConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( int.class ) || type.equals( Integer.class );
    }

    public Object fromString( String str )
    {
        return Integer.valueOf( str );
    }

}
