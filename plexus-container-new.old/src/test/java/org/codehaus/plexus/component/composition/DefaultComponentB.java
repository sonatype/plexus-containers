package org.codehaus.plexus.component.composition;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentB
    implements ComponentB
{
    private ComponentC componentC;

    public ComponentC getComponentC()
    {
        return componentC;
    }

    public void setComponentC( ComponentC componentC )
    {
        System.out.println( "Setting componentC:" + componentC );

        this.componentC = componentC;
    }
}
