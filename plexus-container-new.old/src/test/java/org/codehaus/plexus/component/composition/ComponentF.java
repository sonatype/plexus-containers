package org.codehaus.plexus.component.composition;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
