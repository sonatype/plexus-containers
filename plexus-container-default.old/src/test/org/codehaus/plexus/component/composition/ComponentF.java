package org.codehaus.plexus.component.composition;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:mmaczka@interia.p">Michal Maczka</a>
 * @version $Id$
 */
public class ComponentF
{
    private ComponentA componentA;
    private ComponentB componentB;
    private ComponentC[] componentC;
    private List componentD;
    private Map componentE;

    public ComponentA getComponentA()
    {
        return componentA;
    }

    public void setComponentA( ComponentA componentA )
    {
        this.componentA = componentA;
    }

    public ComponentB getComponentB()
    {
        return componentB;
    }

    public void setComponentB( ComponentB componentB )
    {
        this.componentB = componentB;
    }

    public ComponentC[] getComponentC()
    {
        return componentC;
    }

    public void setComponentC( ComponentC[] componentC )
    {
        this.componentC = componentC;
    }

    public List getComponentD()
    {
        return componentD;
    }

    public void setComponentD( List componentD )
    {
        this.componentD = componentD;
    }

    public Map getComponentE()
    {
        return componentE;
    }

    public void setComponentE( Map componentE )
    {
        this.componentE = componentE;
    }
}
