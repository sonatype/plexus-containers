/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.codehaus.plexus.configuration;

public interface Configuration
{
    String getName();

    String getLocation();

    String getNamespace() throws ConfigurationException;

    Configuration getChild( String child );

    Configuration getChild( String child, boolean createNew );

    Configuration[] getChildren();

    Configuration[] getChildren( String name );

    String[] getAttributeNames();

    String getAttribute( String paramName ) throws ConfigurationException;

    int getAttributeAsInteger( String paramName ) throws ConfigurationException;

    long getAttributeAsLong( String name ) throws ConfigurationException;

    float getAttributeAsFloat( String paramName ) throws ConfigurationException;

    boolean getAttributeAsBoolean( String paramName ) throws ConfigurationException;

    String getValue() throws ConfigurationException;

    int getValueAsInteger() throws ConfigurationException;

    float getValueAsFloat() throws ConfigurationException;

    boolean getValueAsBoolean() throws ConfigurationException;

    long getValueAsLong() throws ConfigurationException;

    String getValue( String defaultValue );

    int getValueAsInteger( int defaultValue );

    long getValueAsLong( long defaultValue );

    float getValueAsFloat( float defaultValue );

    boolean getValueAsBoolean( boolean defaultValue );

    String getAttribute( String name, String defaultValue );

    int getAttributeAsInteger( String name, int defaultValue );

    long getAttributeAsLong( String name, long defaultValue );

    float getAttributeAsFloat( String name, float defaultValue );

    boolean getAttributeAsBoolean( String name, boolean defaultValue );

    // Additional stuff for hierarchy

    Configuration getParent();

    void setParent( Configuration configuration );

    int getChildCount();

    Configuration getChild( int i );
}
