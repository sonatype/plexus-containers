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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

class ComponentLookupThread
    extends Thread
{
    final PlexusContainer container;

    private SlowComponent component;

    public ComponentLookupThread( PlexusContainer container )
    {
        this.container = container;
    }

    public void run()
    {
        try
        {
            SlowComponent tmpComponent = (SlowComponent) container.lookup( SlowComponent.ROLE );

            synchronized ( this )
            {
                this.component = tmpComponent;
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public SlowComponent getComponent()
    {
        synchronized ( this )
        {
            return component;
        }
    }
}

/**
 * @author Ben Walding
 * @version $Id$
 */
public class ClassicSingletonComponentManagerTest
    extends PlexusTestCase
{
    public void testThreads1()
        throws Exception
    {
        test( 1 );
    }

    /**
     * Tests that multiple concurrent threads don't acquire different components.
     * @todo [BP] I've seen this fail at random
     */
    public void testThreads10()
        throws Exception
    {
        test( 10 );
    }

    public void test( int count )
        throws Exception
    {
        ComponentLookupThread components[] = new ComponentLookupThread[ count ];
        //Start them
        for ( int i = 0; i < count; i++ )
        {
            components[ i ] = new ComponentLookupThread( getContainer() );
            components[ i ].start();
        }

        //Wait for them to finish
        for ( int i = 0; i < count; i++ )
        {
            while ( components[ i ].getComponent() == null )
            {
                Thread.sleep( 100 );
            }
        }

        //Get master component
        SlowComponent masterComponent = (SlowComponent) lookup( SlowComponent.ROLE );

        //Verify them
        for ( int i = 0; i < count; i++ )
        {
            assertSame( "components[" + i + "].getComponent()", masterComponent, components[ i ].getComponent() );
        }
    }
}
