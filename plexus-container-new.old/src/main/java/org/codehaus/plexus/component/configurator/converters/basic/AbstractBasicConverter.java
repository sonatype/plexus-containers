package org.codehaus.plexus.component.configurator.converters.basic;

public abstract class AbstractBasicConverter
    implements Converter
{
    public String toString( Object obj )
    {
        return obj.toString();
    }
}
