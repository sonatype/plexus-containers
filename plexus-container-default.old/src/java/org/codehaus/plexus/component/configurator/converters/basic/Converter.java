package org.codehaus.plexus.component.configurator.converters.basic;

/**
 * Translates the String representation of a class into
 * an instance of the class and vice versa
 *
 */
public interface Converter
{
    boolean canConvert( Class type );

    /**
     * Parses a given String  and return
     *
     * @param str String representation of the class
     * @return  an instance of the class
     */
    Object fromString( String str );

    String toString( Object obj );
}
