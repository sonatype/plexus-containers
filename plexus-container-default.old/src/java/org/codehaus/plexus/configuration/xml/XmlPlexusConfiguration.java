package org.codehaus.plexus.configuration.xml;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

        dom.addChild( child );

        return new XmlPlexusConfiguration( child );
    }

    public PlexusConfiguration getChild( int i )
    {
        return new XmlPlexusConfiguration( dom.getChild( i ) );
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
