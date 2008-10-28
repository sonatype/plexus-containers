package org.codehaus.plexus.context;

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

        // There is no data and no parent containerContext.
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
        // initalize
        DefaultContext context = new DefaultContext();
        context.put( "test", "test" );

        // verify inital state
        assertTrue( "test".equals( context.get( "test" ) ) );

        // hide value and verify
        context.hide( "test" );
        try
        {
            context.get( "test" );
            fail( "The item \"test\" was hidden in the child containerContext, but could still be retrieved via get()." );
        }
        catch ( ContextException ce )
        {
            assertTrue( true );
        }

        // reset to inital state and verify
        context.put( "test", "test" );
        assertTrue( "test".equals( context.get( "test" ) ) );

        // mark context read-only and verify that item can not be hidden
        context.makeReadOnly();
        try
        {
            context.hide( "test" );
            fail( "hide() did not throw an exception, even though the containerContext is supposed to be read-only." );
        }
        catch ( IllegalStateException ise )
        {
            assertTrue( true );
        }

        // verify state did not change in failed hide() invocation
        assertTrue( "test".equals( context.get( "test" ) ) );
    }
}
