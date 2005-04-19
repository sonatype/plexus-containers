package org.codehaus.plexus.context;

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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * TestCase for Context.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 */
public class DefaultContextTest
    extends TestCase
{

    public DefaultContextTest( String name )
    {
        super( name );
    }

    public void testContextCreationWithMap()
        throws Exception
    {
        Map map = new HashMap();

        map.put( "name", "jason" );

        DefaultContext context = new DefaultContext( map );

        assertEquals( "jason", (String) context.get( "name" ) );

        assertEquals( map, context.getContextData() );

        // Test removal
        context.put( "name", null );

        // There is no data and no parent context.
        try
        {
            context.get( "name" );
        }
        catch ( ContextException e )
        {
            // do nothing
        }


    }

    public void testAddContext()
        throws Exception
    {
        DefaultContext context = new DefaultContext();
        context.put( "key1", "value1" );
        assertTrue( "value1".equals( context.get( "key1" ) ) );
        context.put( "key1", "" );
        assertTrue( "".equals( context.get( "key1" ) ) );

        context.put( "key1", "value1" );
        context.makeReadOnly();

        try
        {
            context.put( "key1", "" );
            throw new AssertionFailedError( "You are not allowed to change a value after it has been made read only" );
        }
        catch ( IllegalStateException ise )
        {
            assertTrue( "Value is null", "value1".equals( context.get( "key1" ) ) );
        }
    }

    public void testHiddenItems()
        throws ContextException
    {
        DefaultContext parent = new DefaultContext();

        parent.put( "test", "test" );

        parent.makeReadOnly();

        DefaultContext child = new DefaultContext( parent );

        assertNotNull( child.getParent() );

        child.put( "check", "check" );

        Context context = child;

        assertTrue( "check".equals( context.get( "check" ) ) );

        assertTrue( "test".equals( context.get( "test" ) ) );

        child.hide( "test" );
        try
        {
            context.get( "test" );
            fail( "The item \"test\" was hidden in the child context, but could still be retrieved via get()." );
        }
        catch ( ContextException ce )
        {
            assertTrue( true );
        }

        child.makeReadOnly();

        try
        {
            child.hide( "test" );
            fail( "hide() did not throw an exception, even though the context is supposed to be read-only." );
        }
        catch ( IllegalStateException ise )
        {
            assertTrue( true );
        }
    }
}
