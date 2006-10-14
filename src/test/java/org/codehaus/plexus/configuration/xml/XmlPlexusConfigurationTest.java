package org.codehaus.plexus.configuration.xml;

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

import org.codehaus.plexus.configuration.ConfigurationTestHelper;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 * @version $Id$
 */
public final class XmlPlexusConfigurationTest
    extends TestCase
{
    private XmlPlexusConfiguration configuration;

    public void setUp()
    {
        configuration = new XmlPlexusConfiguration( "a" );
    }

    public void testWithHelper()
        throws Exception
    {
        PlexusConfiguration c = ConfigurationTestHelper.getTestConfiguration();

        ConfigurationTestHelper.testConfiguration( c );
    }

    public void testGetValue()
        throws Exception
    {
        String orgValue = "Original String";
        configuration.setValue( orgValue );
        assertEquals( orgValue, configuration.getValue() );
    }

    public void testGetAttribute()
        throws Exception
    {
        String key = "key";
        String value = "original value";
        String defaultStr = "default";
        configuration.setAttribute( key, value );
        assertEquals( value, configuration.getAttribute( key, defaultStr ) );
        assertEquals( defaultStr, configuration.getAttribute( "newKey", defaultStr ) );
    }

    public void testGetChild()
        throws Exception
    {
        XmlPlexusConfiguration child = (XmlPlexusConfiguration) configuration.getChild( "child" );

        assertNotNull( child );

        child.setValue( "child value" );

        assertEquals( 1, configuration.getChildCount() );

        child = (XmlPlexusConfiguration) configuration.getChild( "child" );

        assertNotNull( child );

        assertEquals( "child value", child.getValue() );

        assertEquals( 1, configuration.getChildCount() );
    }

    public void testToString()
       throws Exception
    {
        PlexusConfiguration c = ConfigurationTestHelper.getTestConfiguration();

        assertEquals( "<string string=\"string\">string</string>\n", c.getChild( "string" ).toString() );

        assertEquals( "<singleton attribute=\"attribute\"/>\n", c.getChild( "singleton" ).toString() );
    }
}

