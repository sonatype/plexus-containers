package org.codehaus.plexus.configuration.xml;

import com.thoughtworks.xstream.xml.xpp3.Xpp3Dom;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class XmlPlexusConfiguration
    implements PlexusConfiguration
{
    private Xpp3Dom dom;

    public XmlPlexusConfiguration( String name )
    {
        this.dom = new Xpp3Dom( name );
    }

    public XmlPlexusConfiguration( Xpp3Dom dom )
    {
        this.dom = dom;
    }

    public Xpp3Dom getXpp3Dom()
    {
        return dom;
    }

    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    public String getName()
    {
        return dom.getName();
    }

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    public String getValue()
    {
        return dom.getValue();
    }

    public String getValue( String defaultValue )
    {
        String value = dom.getValue();

        if ( value == null )
        {
            value = defaultValue;

        }

        return value;
    }

    public void setValue( String value )
    {
        dom.setValue( value );
    }

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------

    public void setAttribute( String name, String value )
    {
        dom.setAttribute( name, value );
    }

    public String getAttribute( String name, String defaultValue )
    {
        String attribute = getAttribute( name );

        if ( attribute == null )
        {
            attribute = defaultValue;
        }

        return attribute;
    }

    public String getAttribute( String name )
    {
        return dom.getAttribute( name );
    }

    public String[] getAttributeNames()
    {
        return dom.getAttributeNames();
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    // The behaviour of getChild* that we adopted from avalon is that if the child
    // does not exist then we create the child.

    public PlexusConfiguration getChild( String name )
    {
        Xpp3Dom child = dom.getChild( name );

        if ( child == null )
        {
            child = new Xpp3Dom( name );
        }

        return new XmlPlexusConfiguration( child );
    }

    public PlexusConfiguration getChild( String name, boolean value )
    {
        return getChild( name );
    }

    public PlexusConfiguration[] getChildren()
    {
        Xpp3Dom[] doms = dom.getChildren();

        PlexusConfiguration[] children = new XmlPlexusConfiguration[doms.length];

        for ( int i = 0; i < children.length; i++ )
        {
            children[i] = new XmlPlexusConfiguration( doms[i] );
        }

        return children;
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        Xpp3Dom[] doms = dom.getChildren( name );

        PlexusConfiguration[] children = new XmlPlexusConfiguration[doms.length];

        for ( int i = 0; i < children.length; i++ )
        {
            children[i] = new XmlPlexusConfiguration( doms[i] );
        }

        return children;
    }

    public void addChild( PlexusConfiguration configuration )
    {
        dom.addChild( ((XmlPlexusConfiguration)configuration).getXpp3Dom() );
    }

    public void addAllChildren( PlexusConfiguration other )
    {
        PlexusConfiguration[] children = other.getChildren();

        for ( int i = 0; i < children.length; i++ )
        {
            addChild( children[i] );
        }
    }

    public int getChildCount()
    {
        return dom.getChildCount();
    }
}
