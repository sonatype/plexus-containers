package org.codehaus.plexus.configuration;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class DefaultPlexusConfiguration
    implements PlexusConfiguration
{
    private String name;

    private String value;

    private Map attributes;

    private List children;

    private PlexusConfiguration parent;

    public DefaultPlexusConfiguration( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
        throws PlexusConfigurationException
    {
        if ( null != value )
        {
            return value;
        }
        else
        {
            throw new PlexusConfigurationException( "No value is associated with the "
                                              + "configuration element \"" + getName() + "\"." );
        }
    }

    public String getValue( String defaultValue )
    {
        try
        {
            return getValue();
        }
        catch ( PlexusConfigurationException ce )
        {
            return defaultValue;
        }
    }


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

    public PlexusConfiguration[] getChildren()
    {
        if ( null == children )
        {
            return new PlexusConfiguration[0];
        }
        else
        {
            return (PlexusConfiguration[]) children.toArray( new PlexusConfiguration[0] );
        }
    }

    public String getAttribute( String name )
        throws PlexusConfigurationException
    {
        String value =
            ( null != attributes ) ? (String) attributes.get( name ) : null;

        if ( null != value )
        {
            return value;
        }
        else
        {
            throw new PlexusConfigurationException(
                "No attribute named \"" + name + "\" is "
                + "associated with the configuration element \""
                + getName() + "." );
        }
    }

    public PlexusConfiguration getChild( int i )
    {
        return (PlexusConfiguration) children.get( i );
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        if ( null == children )
        {
            return new PlexusConfiguration[0];
        }
        else
        {
            ArrayList children = new ArrayList();
            int size = this.children.size();

            for ( int i = 0; i < size; i++ )
            {
                PlexusConfiguration configuration = (PlexusConfiguration) this.children.get( i );
                if ( name.equals( configuration.getName() ) )
                {
                    children.add( configuration );
                }
            }

            return (PlexusConfiguration[]) children.toArray( new PlexusConfiguration[0] );
        }
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public void setAttribute( String name, String value )
    {
        if ( null == attributes )
        {
            attributes = new HashMap();
        }
        attributes.put( name, value );
    }

    public void addChild( PlexusConfiguration configuration )
    {
        if ( null == children )
        {
            children = new ArrayList();
        }

        configuration.setParent( this );

        children.add( configuration );
    }

    public void addAll( PlexusConfiguration other )
    {
        setValue( other.getValue( null ) );

        addAllAttributes( other );

        addAllChildren( other );
    }

    public void addAllAttributes( PlexusConfiguration other )
    {
        String[] attributes = other.getAttributeNames();

        for ( int i = 0; i < attributes.length; i++ )
        {
            String name = attributes[i];

            String value = other.getAttribute( name, null );

            setAttribute( name, value );
        }
    }

    public void addAllChildren( PlexusConfiguration other )
    {
        PlexusConfiguration[] children = other.getChildren();

        for ( int i = 0; i < children.length; i++ )
        {
            addChild( children[i] );
        }
    }

    public void removeChild( PlexusConfiguration configuration )
    {
        if ( null == children )
        {
            return;
        }
        children.remove( configuration );
    }

    public int getChildCount()
    {
        if ( null == children )
        {
            return 0;
        }

        return children.size();
    }

    public String getAttribute( String name, String defaultValue )
    {
        try
        {
            return getAttribute( name );
        }
        catch ( PlexusConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public PlexusConfiguration getChild( String name )
    {
        return getChild( name, true );
    }

    public PlexusConfiguration getChild( String name, boolean create )
    {
        PlexusConfiguration[] children = getChildren( name );

        if ( children.length > 0 )
        {
            return children[0];
        }
        else
        {
            if ( create )
            {
                return new DefaultPlexusConfiguration( name );
            }
            else
            {
                return null;
            }
        }
    }

    public PlexusConfiguration getParent()
    {
        return parent;
    }

    public void setParent( PlexusConfiguration parent )
    {
        this.parent = parent;
    }
}
