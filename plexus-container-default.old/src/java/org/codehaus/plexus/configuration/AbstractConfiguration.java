package org.codehaus.plexus.configuration;

/**
 * This is an abstract <code>Configuration</code> implementation that deals
 * with methods that can be abstracted away from underlying implementations.
 *
 * @author <a href="mailto:dev@avalon.codehaus.org">Avalon Development Team</a>
 *
 * @version CVS $Revision$ $Date$
 *
 * @todo decouple this from DefaultConfiguration which is inherently xml-centric.
 * @todo remove all xml-centric notions from here.
 */
public abstract class AbstractConfiguration
    implements PlexusConfiguration
{
    protected abstract String getPrefix()
        throws ConfigurationException;

    public int getValueAsInteger()
        throws ConfigurationException
    {
        String value = getValue().trim();

        try
        {
            return Integer.parseInt( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as an integer in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    public int getValueAsInteger( int defaultValue )
    {
        try
        {
            return getValueAsInteger();
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public long getValueAsLong()
        throws ConfigurationException
    {
        String value = getValue().trim();
        try
        {
            return Long.parseLong( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a long in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    public long getValueAsLong( long defaultValue )
    {
        try
        {
            return getValueAsLong();
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public float getValueAsFloat()
        throws ConfigurationException
    {
        String value = getValue().trim();
        try
        {
            return Float.parseFloat( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a float in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    public float getValueAsFloat( float defaultValue )
    {
        try
        {
            return getValueAsFloat();
        }
        catch ( ConfigurationException ce )
        {
            return ( defaultValue );
        }
    }

    public boolean getValueAsBoolean()
        throws ConfigurationException
    {
        String value = getValue().trim();

        if ( isTrue( value ) )
        {
            return true;
        }
        else if ( isFalse( value ) )
        {
            return false;
        }
        else
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a boolean in the configuration element \""
                + getName() + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    public boolean getValueAsBoolean( boolean defaultValue )
    {
        try
        {
            return getValueAsBoolean();
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
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

    public int getAttributeAsInteger( String name )
        throws ConfigurationException
    {
        String value = getAttribute( name ).trim();
        try
        {
            return Integer.parseInt( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as an integer in the attribute \""
                + name + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    public int getAttributeAsInteger( String name, int defaultValue )
    {
        try
        {
            return getAttributeAsInteger( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public long getAttributeAsLong( String name )
        throws ConfigurationException
    {
        String value = getAttribute( name );

        try
        {
            return Long.parseLong( value );
        }
        catch ( Exception nfe )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a long in the attribute \""
                + name + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    public long getAttributeAsLong( String name, long defaultValue )
    {
        try
        {
            return getAttributeAsLong( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public float getAttributeAsFloat( String name )
        throws ConfigurationException
    {
        String value = getAttribute( name );
        try
        {
            return Float.parseFloat( value );
        }
        catch ( Exception e )
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a float in the attribute \""
                + name + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    public float getAttributeAsFloat( String name, float defaultValue )
    {
        try
        {
            return getAttributeAsFloat( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    public boolean getAttributeAsBoolean( String name )
        throws ConfigurationException
    {
        String value = getAttribute( name );

        if ( isTrue( value ) )
        {
            return true;
        }
        else if ( isFalse( value ) )
        {
            return false;
        }
        else
        {
            String message =
                "Cannot parse the value \"" + value
                + "\" as a boolean in the attribute \""
                + name + "\" at " + getLocation();
            throw new ConfigurationException( message );
        }
    }

    private boolean isTrue( String value )
    {
        return value.equalsIgnoreCase( "true" )
            || value.equalsIgnoreCase( "yes" )
            || value.equalsIgnoreCase( "on" )
            || value.equalsIgnoreCase( "1" );
    }

    private boolean isFalse( String value )
    {
        return value.equalsIgnoreCase( "false" )
            || value.equalsIgnoreCase( "no" )
            || value.equalsIgnoreCase( "off" )
            || value.equalsIgnoreCase( "0" );
    }

    public boolean getAttributeAsBoolean( String name, boolean defaultValue )
    {
        try
        {
            return getAttributeAsBoolean( name );
        }
        catch ( ConfigurationException ce )
        {
            return defaultValue;
        }
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

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name. If no such child exists, a new one
     * will be created.
     *
     * @param name the name of the child
     * @return the child Configuration
     */
    public Configuration getChild( String name )
    {
        return getChild( name, true );
    }

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name.
     *
     * @param name the name of the child
     * @param createNew true if you want to create a new Configuration object if none exists
     * @return the child Configuration
     */
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
                return new DefaultConfiguration( name, "-" );
            }
            else
            {
                return null;
            }
        }
    }

    PlexusConfiguration parent;

    public PlexusConfiguration getParent()
    {
        return parent;
    }

    public void setParent( PlexusConfiguration parent )
    {
        this.parent = parent;
    }
}
