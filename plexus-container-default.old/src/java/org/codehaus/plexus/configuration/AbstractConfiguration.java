package org.codehaus.plexus.configuration;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

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
    /**
     * Returns the prefix of the namespace.  This is only used as a serialization
     * hint, therefore is not part of the client API.  It should be included in
     * all Configuration implementations though.
     * @return A non-null String (defaults to "")
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if no prefix was defined (prefix is
     * <code>null</code>.
     * @since 4.1
     */
    protected abstract String getPrefix()
        throws ConfigurationException;

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as an
     * <code>int</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name the name of the attribute
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as an
     * <code>int</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name the name of the attribute
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     *
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>.
     *
     * @param name the name of the attribute
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>.
     *
     * @param name the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>.
     *
     * @param name the name of the attribute
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>.
     *
     * @param name the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>.
     *
     * @param name the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
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
