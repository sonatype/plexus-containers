package org.codehaus.plexus.internal.util.xml;

public interface XMLWriter
{

    void startElement( String name );

    void addAttribute( String key, String value );

    void writeText( String text );

    void endElement();

}
