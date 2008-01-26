package org.codehaus.plexus.configuration;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @version $Id$
 */
public class DefaultPlexusConfiguration
    implements PlexusConfiguration
{

    private String name;

    private String value;

    /** private TreeMap<String, String> attributes; */
    private LinkedHashMap attributes;

    /** private TreeMap<String, List<PlexusConfiguration>> children; */
    private LinkedHashMap children;

    protected DefaultPlexusConfiguration()
    {
        this( "configuration" );
    }

    protected DefaultPlexusConfiguration( String name )
    {
        this( name, null );
    }

    protected DefaultPlexusConfiguration( String name, String value )
    {
        super();

        this.name = name;

        this.value = value;

        // J5: this.attributes = new TreeMap<String, String>();
        this.attributes = new LinkedHashMap();

        // J5: this.children = new TreeMap<String, List<PlexusConfiguration>>();
        this.children = new LinkedHashMap();
    }

    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    public String getValue()
    {
        return value;
    }

    public String getValue( String defaultValue )
    {
        String value = getValue();

        if ( value == null )
        {
            value = defaultValue;
        }

        return value;
    }

    public void setValue( String val )
    {
        value = val;
    }

    public PlexusConfiguration setValueAndGetSelf( String val )
    {
        setValue( val );

        return this;
    }

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------
    public void setAttribute( String name, String value )
    {
        attributes.put( name, value );
    }

    public String getAttribute( String name )
    {
        return (String) attributes.get( name );
    }

    public String getAttribute( String name, String defaultValue )
    {
        String value = getAttribute( name );

        if ( value == null )
        {
            value = defaultValue;
        }

        return value;
    }

    public String[] getAttributeNames()
    {
        return (String[]) attributes.keySet().toArray( new String[attributes.size()] );
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    public PlexusConfiguration getChild( String name )
    {
        return getChild( name, true );
    }

    public PlexusConfiguration getChild( int i )
    {
        return getChildren()[i];
    }

    public PlexusConfiguration getChild( String name, boolean createChild )
    {
        List childs = (List) children.get( name );

        boolean noneFound = ( childs == null || childs.size() == 0 );

        if ( noneFound && createChild )
        {
            addChild( name );

            return getChild( name, false );
        }
        else if ( noneFound && !createChild )
        {
            return null;
        }
        else
        {
            return (PlexusConfiguration) childs.get( 0 );
        }
    }

    public PlexusConfiguration[] getChildren()
    {
        ArrayList childs = new ArrayList();

        for ( Iterator i = children.keySet().iterator(); i.hasNext(); )
        {
            List childList = (List) children.get( i.next() );

            if ( childList != null )
            {
                childs.addAll( childList );
            }
        }

        return (PlexusConfiguration[]) childs.toArray( new PlexusConfiguration[childs.size()] );
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        ArrayList childs = new ArrayList();

        List childList = (List) children.get( name );

        if ( childList != null )
        {
            childs.addAll( childList );
        }

        return (PlexusConfiguration[]) childs.toArray( new PlexusConfiguration[childs.size()] );
    }

    public void addChild( PlexusConfiguration child )
    {
        if ( !children.containsKey( child.getName() ) )
        {
            children.put( child.getName(), new ArrayList() );
        }

        ( (List) children.get( child.getName() ) ).add( child );
    }

    public PlexusConfiguration addChild( String name )
    {
        // we are using reflection to try to create same class childs as parent is,
        // since many Maven and Maven plugins stuff casts the incoming result of this call
        // to the evil XmlPlexusConfiguration
        PlexusConfiguration child = null;

        try
        {
            child = (PlexusConfiguration) this.getClass().newInstance();

            child.setName( name );
        }
        catch ( Exception e )
        {
            // we have a PlexusConfiguration that has no constructor(name)
            child = new DefaultPlexusConfiguration( name );
        }

        addChild( child );

        return this;
    }

    public PlexusConfiguration addChild( String name, String value )
    {
        PlexusConfiguration child = new DefaultPlexusConfiguration( name, value );

        addChild( child );

        return this;
    }

    public int getChildCount()
    {
        int result = 0;

        for ( Iterator i = children.keySet().iterator(); i.hasNext(); )
        {
            result += ( (List) children.get( i.next() ) ).size();
        }

        return result;
    }
}
