package org.codehaus.plexus.personality.avalon;

import org.apache.avalon.framework.service.ServiceSelector;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.ServiceA;
import org.codehaus.plexus.ServiceC;
import org.codehaus.plexus.personality.avalon.AvalonServiceSelector;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class AvalonServiceSelectorTest
    extends PlexusTestCase
{

    /**
     * @param testName
     */
    public AvalonServiceSelectorTest(String testName)
    {
        super(testName);
    }

    public void testDefaultSelector()
        throws Exception
    {
        ServiceSelector selector = (ServiceSelector) lookup( ServiceA.ROLE + "Selector" );
        
        assertTrue( selector != null );

        assertTrue( selector.isSelectable( "only-instance" ) );

        assertTrue( selector instanceof AvalonServiceSelector );
        
        ServiceA serviceA = (ServiceA) selector.select( "only-instance" );

        assertTrue( serviceA != null );

        selector.release( serviceA );
        
        release( selector );
    }

    public void testCustomSelector()
        throws Exception
    {
        ServiceSelector selector = (ServiceSelector) lookup( ServiceC.ROLE + "Selector" );
        
        assertTrue( selector != null );

        assertTrue( selector.isSelectable( "only-instance" ) );

        assertTrue( selector instanceof AvalonServiceSelector );
        
        ServiceC serviceC = (ServiceC) selector.select( "only-instance" );

        assertTrue( serviceC != null );

        selector.release( serviceC );
        
        release( selector );
    }
}
