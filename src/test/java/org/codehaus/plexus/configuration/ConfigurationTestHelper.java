package org.codehaus.plexus.configuration;

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
import org.codehaus.plexus.component.repository.io.PlexusTools;

import java.io.StringReader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class ConfigurationTestHelper
    extends TestCase
{
    public static PlexusConfiguration getTestConfiguration()
        throws Exception
    {
        return PlexusTools.buildConfiguration( "<Test String>", new StringReader( ConfigurationTestHelper.getXmlConfiguration() ) );
    }

    public static String getXmlConfiguration()
    {
        return "<configuration>" +
               "<empty-element></empty-element>" +
               "<singleton attribute='attribute' />" +
               "<string string='string'>string</string>" +
               "<number number='0'>0</number>" +
               "<not-a-number not-a-number='foo'>not-a-number</not-a-number>" +
               "<boolean-true boolean-true='true'>true</boolean-true>" +
               "<boolean-false boolean-false='false'>false</boolean-false>" +
               "<not-a-boolean>not-a-boolean</not-a-boolean>" +
               "</configuration>";
    }

    public static void testConfiguration( PlexusConfiguration c )
        throws Exception
    {
        // Exercise all value/attribute retrieval methods.

        // Values

        assertNull( c.getChild( "singleton" ).getValue( null ) );

        // String

        assertEquals( "string", c.getValue( "string" ) );

        assertEquals( "string", c.getChild( "string" ).getValue() );

        assertEquals( "string", c.getChild( "ne-string" ).getValue( "string" ) );

        assertNull( c.getChild( "not-existing" ).getValue( null ) );

        assertEquals( "''", "'" + c.getChild( "empty-element" ).getValue() + "'" );

        assertEquals( "", c.getChild( "empty-element" ).getValue( null ) );

        // Attributes

        assertEquals( "string", c.getChild( "string" ).getAttribute( "string" ));

        assertEquals( "attribute", c.getChild( "singleton" ).getAttribute( "attribute" ));
    }
}
