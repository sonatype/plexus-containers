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
package org.codehaus.plexus.configuration;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * Test the basic public methods of DefaultConfiguration.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class DefaultConfigurationTest
    extends TestCase
{
    private DefaultConfiguration configuration;

    public DefaultConfigurationTest()
    {
        this( "DefaultConfiguration Test Case" );
    }

    public DefaultConfigurationTest( String name )
    {
        super( name );
    }

    public void setUp()
    {
        configuration = new DefaultConfiguration( "a", "b" );
    }

    public void tearDowm()
    {
        configuration = null;
    }

    public void testDynamicConfigurationCreation()
        throws Exception
    {
        DefaultConfiguration a = new DefaultConfiguration( "a", "a" );

        DefaultConfiguration b = new DefaultConfiguration( "b", "b" );

        a.addAll( b );

        assertNotNull( b.getPrefix() );

        System.out.println( "a.getChildCount() = " + a.getChildCount() );
    }

    public void testGetValue()
        throws Exception
    {
        String orgValue = "Original String";
        configuration.setValue( orgValue );
        assertEquals( orgValue, configuration.getValue() );
    }

    public void testGetValueAsBoolen()
        throws Exception
    {
        boolean b = true;
        configuration.setValue( "TrUe" );
        assertEquals( b, configuration.getValueAsBoolean() );
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

    public void testMakeReadOnly()
    {
        String key = "key";
        String value = "original value";
        String exception = "exception not thrown";
        String exceptionStr = "Configuration is read only";
        configuration.makeReadOnly();

        try
        {
            configuration.setAttribute( key, value );
        }
        catch ( IllegalStateException ise )
        {
            exception = exceptionStr;
        }

        assertEquals( exception, exceptionStr );
    }

    public void testAddRemoveChild()
    {
        String childName = "child";
        Configuration child = new DefaultConfiguration( childName, "child location" );

        configuration.addChild( child );
        assertEquals( child, configuration.getChild( childName ) );

        configuration.removeChild( child );
        assertEquals( null, configuration.getChild( childName, false ) );
    }
}





