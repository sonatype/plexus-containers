package org.codehaus.plexus.component.configurator.converters.basic;

public class BooleanConverter extends AbstractBasicConverter
{

    public boolean canConvert( Class type )
    {
        return type.equals( boolean.class ) || type.equals( Boolean.class );
    }

    public Object fromString( String str )
    {
        return str.equals( "true" ) ? Boolean.TRUE : Boolean.FALSE;
    }

}
