package org.codehaus.plexus.configuration.xml;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * @version $Id$
 */
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
        return getChild( name, true );
    }

    public PlexusConfiguration getChild( int i )
    {
        return new XmlPlexusConfiguration( dom.getChild( i ) );
    }

    public PlexusConfiguration getChild( String name, boolean createChild )
    {
        Xpp3Dom child = dom.getChild( name );

        if ( child == null )
        {
            if ( createChild )
            {
                child = new Xpp3Dom( name );

                dom.addChild( child );
            }
            else
            {
                return null;
            }
        }

        return new XmlPlexusConfiguration( child );
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
        dom.addChild( ( (XmlPlexusConfiguration) configuration ).getXpp3Dom() );
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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        int depth = 0;

        display( this, sb, depth );

        return sb.toString();
    }

    private void display( PlexusConfiguration c, StringBuffer sb, int depth )
    {
        int count = c.getChildCount();

        if (count == 0)
        {
            displayTag( c, sb, depth );
        }
        else
        {
            sb.append( indent( depth ) ).
                append( '<' ).
                append( c.getName() );

            attributes( c, sb );

            sb.append( '>' ).
                append( '\n' );

            for ( int i = 0; i < count; i++ )
            {
                PlexusConfiguration child = c.getChild( i );

                display( child, sb, depth + 1 );
            }

            sb.append( indent( depth ) ).
                append( '<' ).
                append( '/' ).
                append( c.getName() ).
                append( '>' ).
                append( '\n' );
        }
    }

    private void displayTag( PlexusConfiguration c, StringBuffer sb, int depth )
    {
        String value = c.getValue( null );

        if ( value != null )
        {
            sb.append( indent( depth ) ).
                append( '<' ).
                append( c.getName() );

            attributes( c, sb );

            sb.append( '>' ).
                append( c.getValue( null ) ).
                append( '<' ).
                append( '/' ).
                append( c.getName() ).
                append( '>' ).
                append( '\n' );
        }
        else
        {
            sb.append( indent( depth ) ).
                append( '<' ).
                append( c.getName() );

            attributes( c, sb );

            sb.append( '/' ).
                append( '>' ).
                append( "\n" );
        }
    }

    private void attributes( PlexusConfiguration c, StringBuffer sb )
    {
        String[] names = c.getAttributeNames();

        for ( int i = 0; i < names.length; i++ )
        {
            sb.append( ' ' ).
                append( names[i] ).
                append( '=' ).
                append( '"' ).
                append( c.getAttribute( names[i], null ) ).
                append( '"' );
        }
    }

    private String indent( int depth )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < depth; i++ )
        {
            sb.append( ' ' );
        }

        return sb.toString();
    }
}
