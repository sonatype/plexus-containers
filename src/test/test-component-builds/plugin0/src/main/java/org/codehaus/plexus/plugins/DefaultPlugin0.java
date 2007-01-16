package org.codehaus.plexus.plugins;

import org.codehaus.plexus.components.A;
import org.codehaus.plexus.components.B;

/**
 * @plexus.component
 */
public class DefaultPlugin0
    implements Plugin0
{
    /** @plexus.requirement */
    private A a;
    
    /** @plexus.requirement */    
    private B b;
    
    public void hello()
    {
        System.out.println( "Hello World!" );
    }
}
