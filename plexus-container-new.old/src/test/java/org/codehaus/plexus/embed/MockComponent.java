package org.codehaus.plexus.embed;

import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Context;

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

    /** @see Contextualizable#contextualize */
    public void contextualize( Context context )
        throws ContextException
    {
        foo = (String) context.get( "foo" );

    }
}
