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

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class Xpp3Dom
{
    protected String name;

    protected String value;

    protected Map attributes;

    protected List childList;

    protected Map childMap;

    protected Xpp3Dom parent;

    public Xpp3Dom( String name )
    {
        this.name = name;
        childList = new ArrayList();
        childMap = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------

    public String[] getAttributeNames()
    {
        if ( null == attributes )
        {
            return new String[0];
        }
        else
        {
            return (String[]) attributes.keySet().toArray( new String[0] );
        }
    }

    public String getAttribute( String name )
    {
        return ( null != attributes ) ? (String) attributes.get( name ) : null;
    }

    public void setAttribute( String name, String value )
    {
        if ( null == attributes )
        {
            attributes = new HashMap();
        }

        attributes.put( name, value );
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getChild( int i )
    {
        return (Xpp3Dom) childList.get( i );
    }

    public Xpp3Dom getChild( String name )
    {
        return (Xpp3Dom) childMap.get( name );
    }

    public void addChild( Xpp3Dom xpp3Dom )
    {
        xpp3Dom.setParent( this );
        childList.add( xpp3Dom );
        childMap.put( xpp3Dom.getName(), xpp3Dom );
    }

    public Xpp3Dom[] getChildren()
    {
        if ( null == childList )
        {
            return new Xpp3Dom[0];
        }
        else
        {
            return (Xpp3Dom[]) childList.toArray( new Xpp3Dom[0] );
        }
    }

    public Xpp3Dom[] getChildren( String name )
    {
        if ( null == childList )
        {
            return new Xpp3Dom[0];
        }
        else
        {
            ArrayList children = new ArrayList();
            int size = this.childList.size();

            for ( int i = 0; i < size; i++ )
            {
                Xpp3Dom configuration = (Xpp3Dom) this.childList.get( i );
                if ( name.equals( configuration.getName() ) )
                {
                    children.add( configuration );
                }
            }

            return (Xpp3Dom[]) children.toArray( new Xpp3Dom[0] );
        }
    }

    public int getChildCount()
    {
        if ( null == childList )
        {
            return 0;
        }

        return childList.size();
    }

    // ----------------------------------------------------------------------
    // Parent handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getParent()
    {
        return parent;
    }

    public void setParent( Xpp3Dom parent )
    {
        this.parent = parent;
    }
}
