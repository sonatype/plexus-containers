package org.codehaus.plexus.configuration;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class DefaultConfiguration
    implements Configuration
{
    private String name;

    private Map attributes;

    private List children;

    private String value;

    private Configuration parent;

    public DefaultConfiguration( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
        throws ConfigurationException
    {
        if ( null != value )
        {
            return value;
        }
        else
        {
            throw new ConfigurationException( "No value is associated with the "
                                              + "configuration element \"" + getName() + "." );
        }
    }

    public String getValue( String defaultValue )
    {
        try
        {
            return getValue();
        }
        catch ( ConfigurationException ce )
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

    public Configuration[] getChildren()
    {
        if ( null == children )
        {
            return new Configuration[0];
        }
        else
        {
            return (Configuration[]) children.toArray( new Configuration[0] );
        }
    }

    public String getAttribute( String name )
        throws ConfigurationException
    {
        String value =
            ( null != attributes ) ? (String) attributes.get( name ) : null;

        if ( null != value )
        {
            return value;
        }
        else
        {
            throw new ConfigurationException(
                "No attribute named \"" + name + "\" is "
                + "associated with the configuration element \""
                + getName() + "." );
        }
    }

    public Configuration getChild( int i )
    {
        return (Configuration) children.get( i );
    }

    public Configuration[] getChildren( String name )
    {
        if ( null == children )
        {
            return new Configuration[0];
        }
        else
        {
            ArrayList children = new ArrayList();
            int size = this.children.size();

            for ( int i = 0; i < size; i++ )
            {
                Configuration configuration = (Configuration) this.children.get( i );
                if ( name.equals( configuration.getName() ) )
                {
                    children.add( configuration );
                }
            }

            return (Configuration[]) children.toArray( new Configuration[0] );
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

    public void addChild( Configuration configuration )
    {
        if ( null == children )
        {
            children = new ArrayList();
        }

        configuration.setParent( this );

        children.add( configuration );
    }

    public void addAll( Configuration other )
    {
        setValue( other.getValue( null ) );
        addAllAttributes( other );
        addAllChildren( other );
    }

    public void addAllAttributes( Configuration other )
    {
        String[] attributes = other.getAttributeNames();
        for ( int i = 0; i < attributes.length; i++ )
        {
            String name = attributes[i];
            String value = other.getAttribute( name, null );
            setAttribute( name, value );
        }
    }

    public void addAllChildren( Configuration other )
    {
        Configuration[] children = other.getChildren();
        for ( int i = 0; i < children.length; i++ )
        {
            addChild( children[i] );
        }
    }

    public void removeChild( Configuration configuration )
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
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public Configuration getChild( String name )
    {
        return getChild( name, true );
    }

    public Configuration getChild( String name, boolean createNew )
    {
        Configuration[] children = getChildren( name );
        if ( children.length > 0 )
        {
            return children[0];
        }
        else
        {
            if ( createNew )
            {
                return new DefaultConfiguration( name );
            }
            else
            {
                return null;
            }
        }
    }

    public Configuration getParent()
    {
        return parent;
    }

    public void setParent( Configuration parent )
    {
        this.parent = parent;
    }
}
