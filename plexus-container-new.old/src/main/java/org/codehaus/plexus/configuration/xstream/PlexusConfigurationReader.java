package org.codehaus.plexus.configuration.xstream;

import com.thoughtworks.xstream.xml.XMLReader;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.util.LinkedList;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PlexusConfigurationReader
    implements XMLReader
{
    private PlexusConfiguration current;

    private LinkedList pointers = new LinkedList();

    public PlexusConfigurationReader( PlexusConfiguration configuration )
    {
        current = configuration;

        pointers.addLast( new Pointer() );
    }

    public String name()
    {
        return current.getName();
    }

    public String text()
    {
        String text = null;

        try
        {
            text = current.getValue();
        }
        catch ( PlexusConfigurationException e )
        {
            // do nothing.
        }

        return text;
    }

    public String attribute( String attributeName )
    {
        String text = null;

        try
        {
            text = current.getAttribute( attributeName );
        }
        catch ( PlexusConfigurationException e )
        {
            // do nothing.
        }

        return text;
    }

    public boolean nextChild()
    {
        Pointer pointer = (Pointer) pointers.getLast();

        if ( pointer.v < current.getChildCount() )
        {
            pointers.addLast( new Pointer() );

            current = current.getChild( pointer.v );

            pointer.v++;

            return true;
        }
        else
        {
            return false;
        }
    }

    public void pop()
    {
        current = current.getParent();

        pointers.removeLast();
    }

    public Object peek()
    {
        return current;
    }

    private class Pointer
    {
        public int v;
    }
}
