package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

class ComponentLookupThread extends Thread
{

    final PlexusContainer container;

    private SlowComponent tc;

    public ComponentLookupThread( PlexusContainer container )
    {
        this.container = container;
    }

    public void run()
    {
        try
        {
            this.tc = (SlowComponent) container.lookup( SlowComponent.ROLE );
            System.out.println( SlowComponent.ROLE + " acquired: " + tc );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public synchronized SlowComponent getSlowComponent()
    {
        return tc;
    }
}

/**
 * @author Ben Walding
 */
public class ClassicSingletonComponentManagerTest extends PlexusTestCase
{
    public void testThreads1() throws Exception
    {
        test( 1 );
    }

    /**
     * Tests that multiple concurrent threads don't acquire different components.
     */
    public void testThreads10() throws Exception
    {
        test( 10 );
    }

    public void test( int count ) throws Exception
    {
        ComponentLookupThread tt[] = new ComponentLookupThread[ count ];
        //Start them
        for ( int i = 0; i < count; i++ )
        {
            tt[i] = new ComponentLookupThread( getContainer() );
            tt[i].start();
        }

        //Wait for them to finish
        for ( int i = 0; i < count; i++ )
        {
            while ( tt[i].getSlowComponent() == null )
            {
                Thread.sleep( 100 );
            }
        }

        //Get master component
        SlowComponent masterTC = (SlowComponent) lookup( SlowComponent.ROLE );
        //Verify them
        for ( int i = 0; i < count; i++ )
        {
            assertSame( "tt[" + i + "].getThrashComponent()", masterTC, tt[i].getSlowComponent() );
        }
    }
}