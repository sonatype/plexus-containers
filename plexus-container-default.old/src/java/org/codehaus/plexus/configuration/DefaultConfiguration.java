package org.codehaus.plexus.configuration;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the default <code>Configuration</code> implementation.
 *
 * @author <a href="mailto:dev@avalon.codehaus.org">Avalon Development Team</a>
 * @version CVS $Revision$ $Date$
 */
public class DefaultConfiguration
    extends AbstractConfiguration
    implements Serializable
{
    /**
     * An empty (length zero) array of configuration objects.
     */
    protected static Configuration[] EMPTY_ARRAY = new Configuration[0];

    private String name;
    private String location;
    private String namespace;
    private String prefix;
    private HashMap attributes;
    private ArrayList children;
    private String value;
    private boolean readOnly;

    /**
     * Create a new <code>DefaultConfiguration</code> manager.
     * @param name a <code>String</code> value
     */
    public DefaultConfiguration( String name )
    {
        this( name, null, "", "" );
    }

    /**
     * Create a new <code>DefaultConfiguration</code> manager.
     * @param name a <code>String</code> value
     * @param location a <code>String</code> value
     */
    public DefaultConfiguration( String name, String location )
    {
        this( name, location, "", "" );
    }

    /**
     * Create a new <code>DefaultConfiguration</code> manager.
     * @param name config node name
     * @param location Builder-specific locator string
     * @param ns Namespace string (typically a URI). Should not be null; use ""
     * if no namespace.
     * @param prefix A short string prefixed to element names, associating
     * elements with a longer namespace string. Should not be null; use "" if no
     * namespace.
     * @since 4.1
     */
    public DefaultConfiguration( String name,
                                 String location,
                                 String ns,
                                 String prefix )
    {
        this.name = name;
        this.location = location;
        this.namespace = ns;
        this.prefix = prefix;  // only used as a serialization hint. Cannot be null
    }

    /**
     * Returns the name of this configuration element.
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the namespace of this configuration element
     * @return a <code>String</code> value
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     * @since 4.1
     */
    public String getNamespace() throws ConfigurationException
    {
        if ( null != namespace )
        {
            return namespace;
        }
        else
        {
            throw new ConfigurationException
                ( "No namespace (not even default \"\") is associated with the "
                  + "configuration element \"" + getName()
                  + "\" at " + getLocation() );
        }
    }

    /**
     * Returns the prefix of the namespace
     * @return a <code>String</code> value
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if prefix is not present (<code>null</code>).
     * @since 4.1
     */
    protected String getPrefix() throws ConfigurationException
    {
        if ( null != prefix )
        {
            return prefix;
        }
        else
        {
            throw new ConfigurationException
                ( "No prefix (not even default \"\") is associated with the "
                  + "configuration element \"" + getName()
                  + "\" at " + getLocation() );
        }

    }

    /**
     * Returns a description of location of element.
     * @return a <code>String</code> value
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return a <code>String</code> value
     */
    public String getValue( String defaultValue )
    {
        if ( null != value )
        {
            return value;
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     *
     * @return a <code>String</code> value
     * @throws org.apache.avalon.framework.configuration.ConfigurationException If the value is not present.
     */
    public String getValue() throws ConfigurationException
    {
        if ( null != value )
        {
            return value;
        }
        else
        {
            throw new ConfigurationException( "No value is associated with the "
                                              + "configuration element \"" + getName()
                                              + "\" at " + getLocation() );
        }
    }

    /**
     * Return an array of all attribute names.
     * @return a <code>String[]</code> value
     */
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

    /**
     * Return an array of <code>Configuration</code>
     * elements containing all node children.
     *
     * @return The child nodes with name
     */
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

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>.
     *
     * @param name a <code>String</code> value
     * @return a <code>String</code> value
     * @throws org.apache.avalon.framework.configuration.ConfigurationException If the attribute is not present.
     */
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
                + getName() + "\" at " + getLocation() );
        }
    }

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name.
     * @param name a <code>String</code> value
     * @param createNew a <code>boolean</code> value
     * @return a <code>Configuration</code> value
     */
    public Configuration getChild( String name, boolean createNew )
    {
        if ( null != children )
        {
            int size = children.size();
            for ( int i = 0; i < size; i++ )
            {
                Configuration configuration = (Configuration) children.get( i );
                if ( name.equals( configuration.getName() ) )
                {
                    return configuration;
                }
            }
        }

        if ( createNew )
        {
            return new DefaultConfiguration( name, "-" );
        }
        else
        {
            return null;
        }
    }

    /**
     * Return an array of <code>Configuration</code> objects
     * children of this associated with the given name.
     * <br>
     * The returned array may be empty but is never <code>null</code>.
     *
     * @param name The name of the required children <code>Configuration</code>.
     * @return a <code>Configuration[]</code> value
     */
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

    /**
     * Set the value of this <code>Configuration</code> object to the specified string.
     *
     * @param value a <code>String</code> value
     */
    public void setValue( String value )
    {
        checkWriteable();

        this.value = value;
    }

    /**
     * Set the value of the specified attribute to the specified string.
     *
     * @param name name of the attribute to set
     * @param value a <code>String</code> value
     */
    public void setAttribute( String name, String value )
    {
        checkWriteable();

        if ( null == attributes )
        {
            attributes = new HashMap();
        }
        attributes.put( name, value );
    }

    /**
     * Add a child <code>Configuration</code> to this configuration element.
     * @param configuration a <code>Configuration</code> value
     */
    public void addChild( Configuration configuration )
    {
        checkWriteable();

        if ( null == children )
        {
            children = new ArrayList();
        }

        children.add( configuration );
    }

    /**
     * Add all the attributes, children and value
     * from specified configuration element to current
     * configuration element.
     *
     * @param other the {@link org.apache.avalon.framework.configuration.Configuration} element
     */
    public void addAll( Configuration other )
    {
        checkWriteable();

        setValue( other.getValue( null ) );
        addAllAttributes( other );
        addAllChildren( other );
    }

    /**
     * Add all attributes from specified configuration
     * element to current configuration element.
     *
     * @param other the {@link org.apache.avalon.framework.configuration.Configuration} element
     */
    public void addAllAttributes( Configuration other )
    {
        checkWriteable();

        String[] attributes = other.getAttributeNames();
        for ( int i = 0; i < attributes.length; i++ )
        {
            String name = attributes[i];
            String value = other.getAttribute( name, null );
            setAttribute( name, value );
        }
    }

    /**
     * Add all child <code>Configuration</code> objects from specified
     * configuration element to current configuration element.
     *
     * @param other the other {@link org.apache.avalon.framework.configuration.Configuration} value
     */
    public void addAllChildren( Configuration other )
    {
        checkWriteable();

        Configuration[] children = other.getChildren();
        for ( int i = 0; i < children.length; i++ )
        {
            addChild( children[i] );
        }
    }

    /**
     * Remove a child <code>Configuration</code> to this configuration element.
     * @param configuration a <code>Configuration</code> value
     */
    public void removeChild( Configuration configuration )
    {
        checkWriteable();

        if ( null == children )
        {
            return;
        }
        children.remove( configuration );
    }

    /**
     * Return count of children.
     * @return an <code>int</code> value
     */
    public int getChildCount()
    {
        if ( null == children )
        {
            return 0;
        }

        return children.size();
    }

    /**
     * Make this configuration read-only.
     *
     */
    public void makeReadOnly()
    {
        readOnly = true;
    }

    /**
     * heck if this configuration is writeable.
     *
     * @throws java.lang.IllegalStateException if this configuration s read-only
     */
    protected void checkWriteable()
        throws IllegalStateException
    {
        if ( readOnly )
        {
            throw new IllegalStateException
                ( "Configuration is read only and can not be modified" );
        }
    }
}
