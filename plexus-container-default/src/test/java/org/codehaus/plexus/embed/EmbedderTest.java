package org.codehaus.plexus.embed;

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

import junit.framework.TestCase;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Ben Walding
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class EmbedderTest
    extends TestCase
{
    public void testConfigurationByURL()
        throws Exception
    {
        try
        {
            Map context = new HashMap();

            context.put( "foo", "bar" );

            context.put( "property1", "value1" );

            context.put( "property2", "value2" );

            String configuration = "/" + getClass().getName().replace( '.', '/' ) + ".xml";

            System.out.println( "configuration = " + configuration );

            PlexusEmbedder embed = new Embedder( context, configuration );

            Object o = embed.lookup( MockComponent.ROLE );

            assertEquals( "I AM MOCKCOMPONENT", o.toString() );

            assertNotNull( getClass().getResource( "/test.txt" ) );

            //while ( true )
            //{
            // infinite loop generate an StackTrace stackOverflow
            // due to recreation of component for each lookup
            // Try it by removing comments but not commit this ;-)

            Object m = embed.lookup( MockComponent.ROLE );

            assertEquals( "not same hashCode for singleton component", o.hashCode(), m.hashCode() );

            Object l = embed.lookup( MockComponent.ROLE );

            assertEquals( "not same hashCode for singleton component", l.hashCode(), m.hashCode() );

            assertEquals( "not same hashCode for singleton component", l.hashCode(), m.hashCode() );

            //}
            embed.stop();
        }
        catch ( Exception e )
        {
            System.out.println( "msg  = " + e.getMessage() );
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
}
