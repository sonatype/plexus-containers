package org.codehaus.plexus.component.manager;

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

import org.codehaus.plexus.util.SweeperPool;

/**
 * Pools comnponents
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PoolableComponentManager
    extends AbstractComponentManager
{
    private SweeperPool pool;

    private int maxCapacity = 30;
    private int minCapacity = 3;
    private int initialCapacity = 10;
    private int sweepInterval = 5;
    private int triggerSize = 15;

    public void initialize()
        throws Exception
    {
        super.initialize();

        pool = new SweeperPool( maxCapacity, minCapacity, initialCapacity, sweepInterval, triggerSize );
    }

    public void release( Object component )
        throws Exception
    {
        pool.put( component );
    }

    public void dispose()
        throws Exception
    {
        //@todo really need to wait for all components to be returned.
        //however blocking on this call may prevent plexus servicing
        //other requests and hence prevent cleanup. Have to look
        //at this. For now just assume all connections have been
        //released.
        pool.dispose();

    }

    public Object getComponent()
        throws Exception
    {
        Object component = pool.get();

        if ( component == null )
        {
            component = createComponentInstance();
        }

        return component;
    }
}
