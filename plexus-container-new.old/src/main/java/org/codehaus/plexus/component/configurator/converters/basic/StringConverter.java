package org.codehaus.plexus.component.configurator.converters.basic;

public class StringConverter extends AbstractBasicConverter
{
    public boolean canConvert( Class type )
    {
        return type.equals( String.class );
    }

    public Object fromString( String str )
    {
        return str;
    }

}
