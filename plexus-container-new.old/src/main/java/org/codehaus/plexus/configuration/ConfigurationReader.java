package org.codehaus.plexus.configuration;

import com.thoughtworks.xstream.xml.XMLReader;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configuration;

import java.util.LinkedList;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ConfigurationReader
    implements XMLReader
{
    private DefaultConfiguration current;
    private LinkedList pointers = new LinkedList();

    public ConfigurationReader( Configuration configuration )
    {
        current = (DefaultConfiguration) configuration;
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
        catch ( ConfigurationException e )
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
        catch ( ConfigurationException e )
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
            current = (DefaultConfiguration) current.getChild( pointer.v );
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
        current = (DefaultConfiguration) current.getParent();
        pointers.removeLast();
    }

    private class Pointer
    {
        public int v;
    }
}
