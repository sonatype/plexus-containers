package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

class ComponentLookupThread extends Thread
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
            this.component = (SlowComponent) container.lookup( SlowComponent.ROLE );
            System.out.println( "Acquired: " + component );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public synchronized SlowComponent getComponent()
    {
        return component;
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
        ComponentLookupThread components[] = new ComponentLookupThread[ count ];
        //Start them
        for ( int i = 0; i < count; i++ )
        {
            components[i] = new ComponentLookupThread( getContainer() );
            components[i].start();
        }

        //Wait for them to finish
        for ( int i = 0; i < count; i++ )
        {
            while ( components[i].getComponent() == null )
            {
                Thread.sleep( 100 );
            }
        }

        //Get master component
        SlowComponent masterComponent = (SlowComponent) lookup( SlowComponent.ROLE );
        //Verify them
        for ( int i = 0; i < count; i++ )
        {
            assertSame( "components[" + i + "].getComponent()", masterComponent, components[i].getComponent() );
        }
    }
}