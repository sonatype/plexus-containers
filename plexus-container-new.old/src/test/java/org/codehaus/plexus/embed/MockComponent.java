package org.codehaus.plexus.embed;

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;


/**
 * @author  Ben Walding
 * @version $Id$
 */
public class MockComponent
{
    public static final String ROLE = MockComponent.class.getName();

    public String toString()
    {
        return "I AM MOCKCOMPONENT";
    }

    private String foo;

    public String getFoo()
    {
        return foo;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        foo = (String) context.get( "foo" );

    }
}
