/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002,2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.codehaus.plexus.context;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Resolvable;

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
    private static class ResolvableString implements Resolvable
    {
        private String content;

        public ResolvableString( String content )
        {
            this.content = content;
        }

        public ResolvableString()
        {
            this( "This is a ${test}." );
        }

        public Object resolve( Context context )
            throws ContextException
        {
            int index = this.content.indexOf( "${" );

            if ( index < 0 )
            {
                return this.content;
            }

            StringBuffer buf = new StringBuffer( this.content.substring( 0, index ) );

            while ( index >= 0 && index <= this.content.length() )
            {
                index += 2;
                int end = this.content.indexOf( "}", index );

                if ( end < 0 )
                {
                    end = this.content.length();
                }

                buf.append( context.get( this.content.substring( index, end ) ) );
                end++;

                index = this.content.indexOf( "${", end ) + 2;

                if ( index < 2 )
                {
                    index = -1;
                    buf.append( this.content.substring( end, this.content.length() ) );
                }

                if ( index >= 0 && index <= this.content.length() )
                {
                    buf.append( this.content.substring( end, index ) );
                }
            }

            return buf.toString();
        }
    }

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

    public void testResolveableObject()
        throws ContextException
    {
        DefaultContext context = new DefaultContext();
        context.put( "key1", new ResolvableString() );
        context.put( "test", "Cool Test" );
        context.makeReadOnly();

        Context newContext = context;
        assertTrue( "Cool Test".equals( newContext.get( "test" ) ) );
        assertTrue( !"This is a ${test}.".equals( newContext.get( "key1" ) ) );
        assertTrue( "This is a Cool Test.".equals( newContext.get( "key1" ) ) );
    }

    public void testCascadingContext()
        throws ContextException
    {
        DefaultContext parent = new DefaultContext();
        parent.put( "test", "ok test" );
        parent.makeReadOnly();
        DefaultContext child = new DefaultContext( parent );
        child.put( "check", new ResolvableString( "This is an ${test}." ) );
        child.makeReadOnly();
        Context context = child;

        assertTrue( "ok test".equals( context.get( "test" ) ) );
        assertTrue( !"This is an ${test}.".equals( context.get( "check" ) ) );
        assertTrue( "This is an ok test.".equals( context.get( "check" ) ) );
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
            // Supposed to be thrown.
        }

        child.makeReadOnly();

        try
        {
            child.hide( "test" );
            fail( "hide() did not throw an exception, even though the context is supposed to be read-only." );
        }
        catch ( IllegalStateException ise )
        {
            // Supposed to be thrown.
        }
    }
}
