package org.codehaus.plexus.component;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class ComponentStackElement
{
    private ComponentDescriptor<?> descriptor;
    private String property;

    public ComponentStackElement( ComponentDescriptor<?> descriptor )
    {
        this.descriptor = descriptor;
    }

    public ComponentStackElement( ComponentStackElement element )
    {
        descriptor = element.descriptor;
        property = element.property;
    }

    public ComponentDescriptor<?> getDescriptor()
    {
        return descriptor;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ComponentStackElement ) )
        {
            return false;
        }

        ComponentStackElement that = (ComponentStackElement) o;

        return descriptor.equals( that.descriptor ) &&
            ( property != null ? property.equals( that.property ) : that.property == null );

    }

    public int hashCode()
    {
        int result;
        result = descriptor.hashCode();
        result = 31 * result + ( property != null ? property.hashCode() : 0 );
        return result;
    }

    public String toString()
    {
        return descriptor.getImplementationClass().getName() +
            ( property != null ? "." + property : "" ) +
            ( descriptor.getSource() != null ? "(" + descriptor.getSource() + ")" : "(Unknown Source)" );
    }
}
