package org.codehaus.plexus.lifecycle.avalon;

import org.apache.avalon.framework.service.ServiceSelector;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.ServiceA;
import org.codehaus.plexus.ServiceC;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class AvalonServiceManagerTest
    extends TestCase
{

    /**
     * @param testName
     */
    public AvalonServiceManagerTest( String testName )
    {
        super( testName );
    }

    public void testAvalonServiceManagerWithNullComponentRepository()
        throws Exception
    {
        try
        {
            AvalonServiceManager asm = new AvalonServiceManager( null );
        }
        catch ( IllegalStateException e )
        {
            assertEquals( "ComponentRespository is null.", e.getMessage() );
        }
    }
}
