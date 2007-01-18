package org.codehaus.plexus.component.manager;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

class ComponentLookupThread
    extends Thread
{
    final PlexusContainer container;

    private SlowComponent component;

    private ClassRealm lookupRealm;

    public ComponentLookupThread( PlexusContainer container )
    {
        this.container = container;
        this.lookupRealm = DefaultPlexusContainer.getLookupRealm();
    }

    public void run()
    {
        try
        {
//            DefaultPlexusContainer.setLookupRealm( lookupRealm );
            SlowComponent tmpComponent = (SlowComponent) container.lookup( SlowComponent.ROLE, lookupRealm );

            synchronized ( this )
            {
                this.component = tmpComponent;
            }
        }
        catch ( Exception e )
        {
            DefaultPlexusContainer.getLookupRealm().display();
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
            components[i].join( 10000 );
        }

        //Get master component
        SlowComponent masterComponent = (SlowComponent) lookup( SlowComponent.ROLE );

        //Verify them
        for ( int i = 0; i < count; i++ )
        {
            assertSame( i + ":" + components[i].getComponent() + " == " + masterComponent,
                        masterComponent,
                        components[i].getComponent() );
        }
    }
}
