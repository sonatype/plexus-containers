package org.codehaus.plexus.configuration;

import org.apache.avalon.framework.configuration.Configuration;

import java.util.Properties;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PropertiesConfiguration
    extends AbstractConfiguration
{
    private Properties properties;

    public PropertiesConfiguration()
    {
    }

    public PropertiesConfiguration( Properties properties )
    {
        this.properties = properties;
    }

    public void setProperty( String key, String value )
    {
        getProperties().setProperty( key, value );
    }

    private Properties getProperties()
    {
        if ( properties == null )
        {
            properties = new Properties();
        }

        return properties;
    }

    public Configuration getChild( String name )
    {
        String value = properties.getProperty( name );

        if ( value == null )
        {
            value = "";
        }

        return new ValueConfiguration( name, value );
    }

    public String getValue()
    {
        throw new UnsupportedOperationException();
    }

    public String getName()
    {
        throw new UnsupportedOperationException();
    }

    public String getAttribute( String name )
    {
        throw new UnsupportedOperationException();
    }

    public String[] getAttributeNames()
    {
        throw new UnsupportedOperationException();
    }

    public Configuration[] getChildren()
    {
        throw new UnsupportedOperationException();
    }

    public Configuration[] getChildren( String name )
    {
        throw new UnsupportedOperationException();
    }

    public String getNamespace()
    {
        throw new UnsupportedOperationException();
    }

    public String getPrefix()
    {
        throw new UnsupportedOperationException();
    }

    public String getLocation()
    {
        throw new UnsupportedOperationException();
    }

    static class ValueConfiguration
        extends AbstractConfiguration
    {
        private String name;

        private String value;

        public ValueConfiguration( String name, String value )
        {
            this.name = name;

            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public Configuration getChild( String name )
        {
            throw new UnsupportedOperationException();
        }

        public String getAttribute( String name )
        {
            throw new UnsupportedOperationException();
        }

        public String[] getAttributeNames()
        {
            throw new UnsupportedOperationException();
        }

        public Configuration[] getChildren()
        {
            throw new UnsupportedOperationException();
        }

        public Configuration[] getChildren( String name )
        {
            throw new UnsupportedOperationException();
        }

        public String getNamespace()
        {
            throw new UnsupportedOperationException();
        }

        public String getPrefix()
        {
            throw new UnsupportedOperationException();
        }

        public String getLocation()
        {
            return "unknown";
        }
    }
}
