package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;

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


/**
 * This ensures only a single manager of a a component exists. Once no
 * more connections for this component exists it is disposed.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 *
 * @version $Id$
 */
public class ClassicSingletonComponentManager
    extends AbstractComponentManager
{
    private Object lock = new Object();

    private Object singleton;

    public void release( Object component )
        throws ComponentLifecycleException
    {
        synchronized( lock )
        {
            if ( singleton == component )
            {
                decrementConnectionCount();
    
                if ( !connected() )
                {
                    dispose();
                }
            }
            else
            {
                getLogger().warn( "Component returned which is not the same manager. Ignored. component=" + component );
            }
        }
    }

    public void dispose()
        throws ComponentLifecycleException
    {
        synchronized( lock )
        {
            //wait for all the clients to return all the components
            //Do we do this in a seperate thread? or block the current thread??
            //TODO
            if ( singleton != null )
            {
                endComponentLifecycle( singleton );

                singleton = null;
            }
        }
    }

    public Object getComponent()
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        synchronized( lock )
        {
            if ( singleton == null )
            {
                singleton = createComponentInstance();
            }
    
            incrementConnectionCount();
    
            return singleton;
        }
    }
}
