package org.codehaus.plexus.component.configurator.converters.basic;

public interface Converter
{
    boolean canConvert( Class type );

    Object fromString( String str );

    String toString( Object obj );
}
