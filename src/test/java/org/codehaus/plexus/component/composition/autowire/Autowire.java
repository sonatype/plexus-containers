/*
 * Copyright (c) 2006 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.component.composition.autowire;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Autowire
{
    private One one;

    private Two two;

    public One getOne()
    {
        return one;
    }

    public void setOne( One one )
    {
        this.one = one;
    }

    public Two getTwo()
    {
        return two;
    }

    public void setTwo( Two two )
    {
        this.two = two;
    }
}
