package org.codehaus.plexus.lifecycle.avalon;

import org.apache.avalon.framework.service.ServiceSelector;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.ServiceC;

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

    public void testSelector() throws Exception
    {
        ServiceSelector selector = (ServiceSelector) lookup( ServiceC.ROLE + "Selector" );
        
        assertTrue( selector != null );
        assertTrue( selector.isSelectable( "only-instance" ) );


        ServiceC serviceC = (ServiceC) selector.select( "only-instance" );
        assertTrue( serviceC != null );

        release( selector );
        release( serviceC );
    }
}
