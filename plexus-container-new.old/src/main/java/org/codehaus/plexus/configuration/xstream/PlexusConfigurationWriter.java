package org.codehaus.plexus.configuration.xstream;

import com.thoughtworks.xstream.xml.XMLWriter;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;

import java.util.LinkedList;

public class PlexusConfigurationWriter implements XMLWriter
{
    private LinkedList elementStack = new LinkedList();

    private DefaultPlexusConfiguration configuration;

    public PlexusConfigurationWriter()
    {

    }

    public DefaultPlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public void startElement( String name )
    {
        DefaultPlexusConfiguration configuration = new DefaultPlexusConfiguration( name );

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

    private DefaultPlexusConfiguration top()
    {
        return (DefaultPlexusConfiguration) elementStack.getLast();
    }
}
