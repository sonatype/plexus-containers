package org.codehaus.plexus.configuration;

/**
 * A name-value pair used to represent generic property settings in a
 * configurable component.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 */
public final class Property {
    private String name;

    private String value;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name for the property.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value of the property.
     */
    public void setValue( String value ) {
        this.value = value;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append( name );

        buf.append( " = " );

        buf.append( value );

        return buf.toString();
    }
}
