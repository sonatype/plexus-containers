package org.codehaus.plexus.metadata.merge.support;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.metadata.merge.MergeException;
import org.codehaus.plexus.metadata.merge.MergeStrategy;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.filter.Filter;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public abstract class AbstractMergeableSupport
    implements Mergeable
{
    /**
     * Wrapped JDOM element.
     */
    protected Element element;

    /**
     * The default merging strategy used.
     */
    private static final MergeStrategy DEFAULT_MERGE_STRATEGY = MergeStrategies.DEEP;

    public AbstractMergeableSupport( Element element )
    {
        this.element = element;
    }

    public abstract void merge( Mergeable me )
        throws MergeException;

    /**
     * Determines if the passed in {@link Mergeable} was of same type as this
     * class.
     *
     * @param me {@link Mergeable} instance to test.
     * @return <code>true</code> if the passed in Mergeable can be merged with
     *         the current Mergeable.
     */
    protected abstract boolean isExpectedElementType( Mergeable me );

    // ----------------------------------------------------------------------
    // Methods delegated on wrapped JDOM element.
    // ----------------------------------------------------------------------

    public Element addContent( Collection collection )
    {
        return element.addContent( collection );
    }

    public Element addContent( Content child )
    {
        return element.addContent( child );
    }

    public Element addContent( int index, Collection c )
    {
        return element.addContent( index, c );
    }

    public Element addContent( int index, Content child )
    {
        return element.addContent( index, child );
    }

    public Element addContent( String str )
    {
        return element.addContent( str );
    }

    public void addNamespaceDeclaration( Namespace additional )
    {
        element.addNamespaceDeclaration( additional );
    }

    public Object clone()
    {
        return element.clone();
    }

    public List cloneContent()
    {
        return element.cloneContent();
    }

    public Content detach()
    {
        return element.detach();
    }

    public boolean equals( Object obj )
    {
        return element.equals( obj );
    }

    public List getAdditionalNamespaces()
    {
        return element.getAdditionalNamespaces();
    }

    public Attribute getAttribute( String name, Namespace ns )
    {
        return element.getAttribute( name, ns );
    }

    public Attribute getAttribute( String name )
    {
        return element.getAttribute( name );
    }

    public List getAttributes()
    {
        return element.getAttributes();
    }

    /**
     * @see org.jdom.Element#getAttributeValue(java.lang.String,org.jdom.Namespace,java.lang.String)
     */
    public String getAttributeValue( String name, Namespace ns, String def )
    {
        return element.getAttributeValue( name, ns, def );
    }

    /**
     * @see org.jdom.Element#getAttributeValue(java.lang.String,org.jdom.Namespace)
     */
    public String getAttributeValue( String name, Namespace ns )
    {
        return element.getAttributeValue( name, ns );
    }

    /**
     * @see org.jdom.Element#getAttributeValue(java.lang.String,java.lang.String)
     */
    public String getAttributeValue( String name, String def )
    {
        return element.getAttributeValue( name, def );
    }

    /**
     * @see org.jdom.Element#getAttributeValue(java.lang.String)
     */
    public String getAttributeValue( String name )
    {
        return element.getAttributeValue( name );
    }

    /**
     * @return
     * @see org.jdom.Element#getChild(java.lang.String,org.jdom.Namespace)
     */
    public Element getChild( String name, Namespace ns )
    {
        return element.getChild( name, ns );
    }

    /**
     * @see org.jdom.Element#getChild(java.lang.String)
     */
    public Element getChild( String name )
    {
        return element.getChild( name );
    }

    /**
     * @see org.jdom.Element#getChildren()
     */
    public List getChildren()
    {
        return element.getChildren();
    }

    /**
     * @see org.jdom.Element#getChildren(java.lang.String,org.jdom.Namespace)
     */
    public List getChildren( String name, Namespace ns )
    {
        return element.getChildren( name, ns );
    }

    /**
     * @see org.jdom.Element#getChildren(java.lang.String)
     */
    public List getChildren( String name )
    {
        return element.getChildren( name );
    }

    /**
     * @see org.jdom.Element#getChildText(java.lang.String,org.jdom.Namespace)
     */
    public String getChildText( String name, Namespace ns )
    {
        return element.getChildText( name, ns );
    }

    /**
     * @see org.jdom.Element#getChildText(java.lang.String)
     */
    public String getChildText( String name )
    {
        return element.getChildText( name );
    }

    /**
     * @see org.jdom.Element#getChildTextNormalize(java.lang.String,org.jdom.Namespace)
     */
    public String getChildTextNormalize( String name, Namespace ns )
    {
        return element.getChildTextNormalize( name, ns );
    }

    /**
     * @see org.jdom.Element#getChildTextNormalize(java.lang.String)
     */
    public String getChildTextNormalize( String name )
    {
        return element.getChildTextNormalize( name );
    }

    /**
     * @see org.jdom.Element#getChildTextTrim(java.lang.String,org.jdom.Namespace)
     */
    public String getChildTextTrim( String name, Namespace ns )
    {
        return element.getChildTextTrim( name, ns );
    }

    /**
     * @see org.jdom.Element#getChildTextTrim(java.lang.String)
     */
    public String getChildTextTrim( String name )
    {
        return element.getChildTextTrim( name );
    }

    /**
     * @see org.jdom.Element#getContent()
     */
    public List getContent()
    {
        return element.getContent();
    }

    /**
     * @see org.jdom.Element#getContent(org.jdom.filter.Filter)
     */
    public List getContent( Filter filter )
    {
        return element.getContent( filter );
    }

    /**
     * @see org.jdom.Element#getContent(int)
     */
    public Content getContent( int index )
    {
        return element.getContent( index );
    }

    /**
     * @return
     * @see org.jdom.Element#getContentSize()
     */
    public int getContentSize()
    {
        return element.getContentSize();
    }

    /**
     * @see org.jdom.Element#getDescendants()
     */
    public Iterator getDescendants()
    {
        return element.getDescendants();
    }

    /**
     * @see org.jdom.Element#getDescendants(org.jdom.filter.Filter)
     */
    public Iterator getDescendants( Filter filter )
    {
        return element.getDescendants( filter );
    }

    /**
     * @see org.jdom.Content#getDocument()
     */
    public Document getDocument()
    {
        return element.getDocument();
    }

    /**
     * @see org.jdom.Element#getName()
     */
    public String getName()
    {
        return element.getName();
    }

    /**
     * @see org.jdom.Element#getNamespace()
     */
    public Namespace getNamespace()
    {
        return element.getNamespace();
    }

    /**
     * @see org.jdom.Element#getNamespace(java.lang.String)
     */
    public Namespace getNamespace( String prefix )
    {
        return element.getNamespace( prefix );
    }

    /**
     * @see org.jdom.Element#getNamespacePrefix()
     */
    public String getNamespacePrefix()
    {
        return element.getNamespacePrefix();
    }

    /**
     * @see org.jdom.Element#getNamespaceURI()
     */
    public String getNamespaceURI()
    {
        return element.getNamespaceURI();
    }

    /**
     * @see org.jdom.Content#getParent()
     */
    public Parent getParent()
    {
        return element.getParent();
    }

    /**
     * @see org.jdom.Content#getParentElement()
     */
    public Element getParentElement()
    {
        return element.getParentElement();
    }

    /**
     * @see org.jdom.Element#getQualifiedName()
     */
    public String getQualifiedName()
    {
        return element.getQualifiedName();
    }

    /**
     * @see org.jdom.Element#getText()
     */
    public String getText()
    {
        return element.getText();
    }

    /**
     * @see org.jdom.Element#getTextNormalize()
     */
    public String getTextNormalize()
    {
        return element.getTextNormalize();
    }

    /**
     * @see org.jdom.Element#getTextTrim()
     */
    public String getTextTrim()
    {
        return element.getTextTrim();
    }

    /**
     * @see org.jdom.Element#getValue()
     */
    public String getValue()
    {
        return element.getValue();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return element.hashCode();
    }

    /**
     * @see org.jdom.Element#indexOf(org.jdom.Content)
     */
    public int indexOf( Content child )
    {
        return element.indexOf( child );
    }

    /**
     * @see org.jdom.Element#isAncestor(org.jdom.Element)
     */
    public boolean isAncestor( Element element )
    {
        return element.isAncestor( element );
    }

    /**
     * @see org.jdom.Element#isRootElement()
     */
    public boolean isRootElement()
    {
        return element.isRootElement();
    }

    /**
     * @see org.jdom.Element#removeAttribute(org.jdom.Attribute)
     */
    public boolean removeAttribute( Attribute attribute )
    {
        return element.removeAttribute( attribute );
    }

    /**
     * @see org.jdom.Element#removeAttribute(java.lang.String,org.jdom.Namespace)
     */
    public boolean removeAttribute( String name, Namespace ns )
    {
        return element.removeAttribute( name, ns );
    }

    /**
     * @see org.jdom.Element#removeAttribute(java.lang.String)
     */
    public boolean removeAttribute( String name )
    {
        return element.removeAttribute( name );
    }

    /**
     * @see org.jdom.Element#removeChild(java.lang.String,org.jdom.Namespace)
     */
    public boolean removeChild( String name, Namespace ns )
    {
        return element.removeChild( name, ns );
    }

    /**
     * @see org.jdom.Element#removeChild(java.lang.String)
     */
    public boolean removeChild( String name )
    {
        return element.removeChild( name );
    }

    /**
     * @see org.jdom.Element#removeChildren(java.lang.String,org.jdom.Namespace)
     */
    public boolean removeChildren( String name, Namespace ns )
    {
        return element.removeChildren( name, ns );
    }

    /**
     * @see org.jdom.Element#removeChildren(java.lang.String)
     */
    public boolean removeChildren( String name )
    {
        return element.removeChildren( name );
    }

    /**
     * @see org.jdom.Element#removeContent()
     */
    public List removeContent()
    {
        return element.removeContent();
    }

    /**
     * @see org.jdom.Element#removeContent(org.jdom.Content)
     */
    public boolean removeContent( Content child )
    {
        return element.removeContent( child );
    }

    /**
     * @see org.jdom.Element#removeContent(org.jdom.filter.Filter)
     */
    public List removeContent( Filter filter )
    {
        return element.removeContent( filter );
    }

    /**
     * @see org.jdom.Element#removeContent(int)
     */
    public Content removeContent( int index )
    {
        return element.removeContent( index );
    }

    /**
     * @see org.jdom.Element#removeNamespaceDeclaration(org.jdom.Namespace)
     */
    public void removeNamespaceDeclaration( Namespace additionalNamespace )
    {
        element.removeNamespaceDeclaration( additionalNamespace );
    }

    /**
     * @see org.jdom.Element#setAttribute(org.jdom.Attribute)
     */
    public Element setAttribute( Attribute attribute )
    {
        return element.setAttribute( attribute );
    }

    /**
     * @see org.jdom.Element#setAttribute(java.lang.String,java.lang.String,org.jdom.Namespace)
     */
    public Element setAttribute( String name, String value, Namespace ns )
    {
        return element.setAttribute( name, value, ns );
    }

    /**
     * @see org.jdom.Element#setAttribute(java.lang.String,java.lang.String)
     */
    public Element setAttribute( String name, String value )
    {
        return element.setAttribute( name, value );
    }

    /**
     * @see org.jdom.Element#setAttributes(java.util.List)
     */
    public Element setAttributes( List newAttributes )
    {
        return element.setAttributes( newAttributes );
    }

    /**
     * @see org.jdom.Element#setContent(java.util.Collection)
     */
    public Element setContent( Collection newContent )
    {
        return element.setContent( newContent );
    }

    /**
     * @see org.jdom.Element#setContent(org.jdom.Content)
     */
    public Element setContent( Content child )
    {
        return element.setContent( child );
    }

    /**
     * @see org.jdom.Element#setContent(int,java.util.Collection)
     */
    public Parent setContent( int index, Collection collection )
    {
        return element.setContent( index, collection );
    }

    /**
     * @see org.jdom.Element#setContent(int,org.jdom.Content)
     */
    public Element setContent( int index, Content child )
    {
        return element.setContent( index, child );
    }

    /**
     * @see org.jdom.Element#setName(java.lang.String)
     */
    public Element setName( String name )
    {
        return element.setName( name );
    }

    /**
     * @see org.jdom.Element#setNamespace(org.jdom.Namespace)
     */
    public Element setNamespace( Namespace namespace )
    {
        return element.setNamespace( namespace );
    }

    /**
     * @see org.jdom.Element#setText(java.lang.String)
     */
    public Element setText( String text )
    {
        return element.setText( text );
    }

    /**
     * @see org.jdom.Element#toString()
     */
    public String toString()
    {
        return element.toString();
    }

    /**
     * Returns the wrapped up JDom {@link Element} instance.
     */
    public Element getElement()
    {
        return this.element;
    }

    /**
     * Sub classes should override if they wish to provide a different
     * combination of composite keys for determining conflicts.
     */
    protected List getElementNamesForConflictResolution( List defaultList )
    {
        return defaultList;
    }

    /**
     * Returns the default {@link MergeStrategy} instance.
     */
    protected MergeStrategy getDefaultMergeStrategy()
    {
        return DEFAULT_MERGE_STRATEGY;
    }

}
