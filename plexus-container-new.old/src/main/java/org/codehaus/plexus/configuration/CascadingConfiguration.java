package org.codehaus.plexus.configuration;

/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The CascadingConfiguration is a classic Configuration backed by parent
 * Configuration.  Operations such as getChild return a CascadingConfiguration
 * encapsulating both a primary and parent configuration.  Requests for attribute
 * values are resolved against the base configuration initially.  If the result
 * of the resolution is unsucessful, the request is applied against the parent
 * configuration.  As a parent may also be a CascadingConfiguration, the evaluation
 * will be applied until a value is resolved against a class parent Configuration.
 *
 * @author Stephen McConnell <mcconnell@osm.net>
 *
 */
public class CascadingConfiguration
    implements Configuration
{
    /**
     * The primary configuration.
     */
    private final Configuration base;

    /**
     * The fallback configuration.
     */
    private final Configuration parent;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Create a CascadingConfiguration with specified parent.  The base
     * configuration shall override a parent configuration on request for
     * attribute values and configuration body values.  Unresolved request
     * are redirected up the parent chain until a classic configuration is
     * reached.  Request for child configurations will return a
     * new CascadingConfiguration referencing the child of the base and
     * the child of the primary (i.e. a child configuration chain).
     *
     * @param base the base Configuration
     * @param parent the parent Configuration
     */
    public CascadingConfiguration( final Configuration base, final Configuration parent )
    {
        if( base == null )
        {
            this.base = new DefaultConfiguration( "-", null );
        }
        else
        {
            this.base = base;
        }
        if( parent == null )
        {
            this.parent = new DefaultConfiguration( "-", null );
        }
        else
        {
            this.parent = parent;
        }
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    /**
     * Return the name of the base node.
     * @return name of the <code>Configuration</code> node.
     */
    public String getName()
    {
        return base.getName();
    }

    /**
     * Return a string describing location of the base Configuration.
     * Location can be different for different mediums (ie "file:line" for normal XML files or
     * "table:primary-key" for DB based configurations);
     *
     * @return a string describing location of Configuration
     */
    public String getLocation()
    {
        return base.getLocation();
    }

    /**
     * Returns the namespace the main Configuration node
     * belongs to.
     * @exception ConfigurationException may be thrown by the underlying configuration
     * @since 4.1
     * @return a Namespace identifying the namespace of this Configuration.
     */
    public String getNamespace() throws ConfigurationException
    {
        return base.getNamespace();
    }

    /**
     * Return a new <code>CascadingConfiguration</code> manager encapsulating the
     * specified child node of the base and parent node.
     *
     * @param child The name of the child node.
     * @return Configuration
     */
    public Configuration getChild( String child )
    {
        return new CascadingConfiguration( base.getChild( child ), parent.getChild( child ) );
    }

    /**
     * Return a <code>Configuration</code> manager encapsulating the specified
     * child node.
     *
     * @param child The name of the child node.
     * @param createNew If <code>true</code>, a new <code>Configuration</code>
     * will be created and returned if the specified child does not exist in either
     * the base or parent configuratioin. If <code>false</code>, <code>null</code>
     * will be returned when the specified child doesn't exist in either the base or
     * the parent.
     * @return Configuration
     */
    public Configuration getChild( String child, boolean createNew )
    {
        if( createNew )
        {
            return getChild( child );
        }
        Configuration c = base.getChild( child, false );
        if( c != null )
        {
            return c;
        }
        return parent.getChild( child, false );
    }

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children of both base and parent configurations.
     * The array order will reflect the order in the source config file, commencing
     * with the base configuration.
     *
     * @return All child nodes
     */
    public Configuration[] getChildren()
    {
        Configuration[] b = base.getChildren();
        Configuration[] p = parent.getChildren();
        Configuration[] result = new Configuration[ b.length + p.length ];
        System.arraycopy( b, 0, result, 0, b.length );
        System.arraycopy( p, 0, result, b.length, p.length );
        return result;
    }

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children with the specified name from
     * both base and parent configurations. The array
     * order will reflect the order in the source config file commencing
     * with the base configuration.
     *
     * @param name The name of the children to get.
     * @return The child nodes with name <code>name</code>
     */
    public Configuration[] getChildren( String name )
    {
        Configuration[] b = base.getChildren( name );
        Configuration[] p = parent.getChildren( name );
        Configuration[] result = new Configuration[ b.length + p.length ];
        System.arraycopy( b, 0, result, 0, b.length );
        System.arraycopy( p, 0, result, b.length, p.length );
        return result;
    }

    /**
     * Return an array of all attribute names in both base and parent.
     * <p>
     * <em>The order of attributes in this array can not be relied on.</em> As
     * with XML, a <code>Configuration</code>'s attributes are an
     * <em>unordered</em> set. If your code relies on order, eg
     * <tt>conf.getAttributeNames()[0]</tt>, then it is liable to break if a
     * different XML parser is used.
     * </p>
     * @return an array of all attribute names
     */
    public String[] getAttributeNames()
    {
        java.util.Vector vector = new java.util.Vector();
        String[] names = base.getAttributeNames();
        String[] names2 = parent.getAttributeNames();
        for( int i = 0; i < names.length; i++ )
        {
            vector.add( names[ i ] );
        }
        for( int i = 0; i < names2.length; i++ )
        {
            if( vector.indexOf( names2[ i ] ) < 0 )
            {
                vector.add( names2[ i ] );
            }
        }
        return (String[])vector.toArray( new String[ 0 ] );
    }

    /**
     * Return the value of specified attribute.  If the base configuration
     * does not contain the attribute, the equivialent operation is applied to
     * the parent configuration.
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return String value of attribute.
     * @exception ConfigurationException If no attribute with that name exists.
     */
    public String getAttribute( String paramName ) throws ConfigurationException
    {
        try
        {
            return base.getAttribute( paramName );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttribute( paramName );
        }
    }

    /**
     * Return the <code>int</code> value of the specified attribute contained
     * in this node or the parent.
     * @param paramName The name of the parameter you ask the value of.
     * @return int value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>int</code> fails.
     */
    public int getAttributeAsInteger( String paramName ) throws ConfigurationException
    {
        try
        {
            return base.getAttributeAsInteger( paramName );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsInteger( paramName );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     *
     * @param name The name of the parameter you ask the value of.
     * @return long value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>long</code> fails.
     */
    public long getAttributeAsLong( String name ) throws ConfigurationException
    {
        try
        {
            return base.getAttributeAsLong( name );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsLong( name );
        }
    }

    /**
     * Return the <code>float</code> value of the specified parameter contained
     * in this node.
     * @param paramName The name of the parameter you ask the value of.
     * @return float value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>float</code> fails.
     */
    public float getAttributeAsFloat( String paramName ) throws ConfigurationException
    {
        try
        {
            return base.getAttributeAsFloat( paramName );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsFloat( paramName );
        }
    }

    /**
     * Return the <code>boolean</code> value of the specified parameter contained
     * in this node.
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return boolean value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>boolean</code> fails.
     */
    public boolean getAttributeAsBoolean( String paramName ) throws ConfigurationException
    {
        try
        {
            return base.getAttributeAsBoolean( paramName );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsBoolean( paramName );
        }
    }

    /**
     * Return the <code>String</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException May be raised by underlying
     *                                   base or parent configuration.
     */
    public String getValue() throws ConfigurationException
    {
        try
        {
            return base.getValue();
        }
        catch( ConfigurationException e )
        {
            return parent.getValue();
        }
    }

    /**
     * Return the <code>int</code> value of the node.
     * @return int the value as an integer
     * @exception ConfigurationException If conversion to <code>int</code> fails.
     */
    public int getValueAsInteger() throws ConfigurationException
    {
        try
        {
            return base.getValueAsInteger();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsInteger();
        }
    }

    /**
     * Return the <code>float</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>float</code> fails.
     */
    public float getValueAsFloat() throws ConfigurationException
    {
        try
        {
            return base.getValueAsFloat();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsFloat();
        }
    }

    /**
     * Return the <code>boolean</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>boolean</code> fails.
     */
    public boolean getValueAsBoolean() throws ConfigurationException
    {
        try
        {
            return base.getValueAsBoolean();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsBoolean();
        }
    }

    /**
     * Return the <code>long</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>long</code> fails.
     */
    public long getValueAsLong() throws ConfigurationException
    {
        try
        {
            return base.getValueAsLong();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsLong();
        }
    }

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return String value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public String getValue( String defaultValue )
    {
        try
        {
            return base.getValue();
        }
        catch( ConfigurationException e )
        {
            return parent.getValue( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return int value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public int getValueAsInteger( int defaultValue )
    {
        try
        {
            return base.getValueAsInteger();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsInteger( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return long value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public long getValueAsLong( long defaultValue )
    {
        try
        {
            return base.getValueAsLong();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsLong( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return float value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public float getValueAsFloat( float defaultValue )
    {
        try
        {
            return base.getValueAsFloat();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsFloat( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return boolean value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public boolean getValueAsBoolean( boolean defaultValue )
    {
        try
        {
            return base.getValueAsBoolean();
        }
        catch( ConfigurationException e )
        {
            return parent.getValueAsBoolean( defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return String value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    public String getAttribute( String name, String defaultValue )
    {
        try
        {
            return base.getAttribute( name );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttribute( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>int</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return int value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    public int getAttributeAsInteger( String name, int defaultValue )
    {
        try
        {
            return base.getAttributeAsInteger( name );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsInteger( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return long value of attribute. It will return the default
     *          value if the named attribute does not exist, or if
     *          the value is not set.
     */
    public long getAttributeAsLong( String name, long defaultValue )
    {
        try
        {
            return base.getAttributeAsLong( name );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsLong( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return float value of attribute. It will return the default
     *          value if the named attribute does not exist, or if
     *          the value is not set.
     */
    public float getAttributeAsFloat( String name, float defaultValue )
    {
        try
        {
            return base.getAttributeAsFloat( name );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsFloat( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return boolean value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    public boolean getAttributeAsBoolean( String name, boolean defaultValue )
    {
        try
        {
            return base.getAttributeAsBoolean( name );
        }
        catch( ConfigurationException e )
        {
            return parent.getAttributeAsBoolean( name, defaultValue );
        }
    }
}



