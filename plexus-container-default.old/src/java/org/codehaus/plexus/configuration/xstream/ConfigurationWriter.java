package org.codehaus.plexus.configuration.xstream;

import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.LinkedList;

import org.codehaus.plexus.configuration.DefaultConfiguration;

public class ConfigurationWriter implements XMLWriter
{
    private LinkedList elementStack = new LinkedList();

    private DefaultConfiguration configuration;

    public ConfigurationWriter()
    {

    }

    public DefaultConfiguration getConfiguration()
    {
        return configuration;
    }

    public void startElement( String name )
    {
        DefaultConfiguration configuration = new DefaultConfiguration( name );

        if ( this.configuration == null )
        {
            this.configuration = configuration;
        }
        else
        {
            top().addChild( configuration );
        }

        elementStack.addLast( configuration );
    }

    public void writeText( String text )
    {
        top().setValue( text );
    }

    public void addAttribute( String key, String value )
    {
        top().setAttribute( key, value );
    }

    public void endElement()
    {
        elementStack.removeLast();
    }

    private DefaultConfiguration top()
    {
        return (DefaultConfiguration) elementStack.getLast();
    }
}
