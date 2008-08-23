package org.codehaus.plexus.metadata.merge.support;

/* 
 * ========================================================================
 * 
 * Copyright 2003 The Apache Software Foundation. Code from this file 
 * was originally imported from the Jakarta Cactus project.
 * 
 * Copyright 2004-2006 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */

import java.lang.reflect.Constructor;

import org.jdom.Element;

/**
 * Represents the various top-level tags in a deployment descriptor as a typesafe enumeration.
 *
 * @version $Id$
 */
public class DescriptorTag
{
    /**
     * The tag name.
     */
    private String tagName;

    /**
     * Whether multiple occurrences of the tag in the descriptor are allowed.
     */
    private boolean multipleAllowed;

    /**
     * Class that wraps this tag and provides for merging same tags.
     */
    private Class mergeableClass;

    /**
     * Constructor.
     *
     * @param tagName           The tag name of the element
     */
    public DescriptorTag( String tagName )
    {
        this( tagName, false, null );
    }

    /**
     * Constructor.
     *
     * @param tagName           The tag name of the element
     * @param isMultipleAllowed Whether the element may occur multiple times in the descriptor
     * @deprecated Use {@link #DescriptorTag(String,boolean,Class)} instead
     */
    public DescriptorTag( String tagName, boolean isMultipleAllowed )
    {
        this( tagName, isMultipleAllowed, null );
    }

    /**
     * Constructor.
     *
     * @param tagName           The tag name of the element
     * @param isMultipleAllowed Whether the element may occur multiple times in the descriptor
     * @param mergeableClass    Concrete implementation of {@link Mergeable} that is bound this tag.
     */
    public DescriptorTag( String tagName, boolean isMultipleAllowed, Class mergeableClass )
    {
        this.tagName = tagName;
        this.multipleAllowed = isMultipleAllowed;
        this.mergeableClass = mergeableClass;
    }

    public boolean equals( Object other )
    {
        boolean eq = false;
        if ( other instanceof DescriptorTag )
        {
            DescriptorTag tag = (DescriptorTag) other;
            if ( tag.getTagName().equals( this.tagName ) )
            {
                eq = true;
            }
        }
        return eq;
    }

    public int hashCode()
    {
        return this.getTagName().hashCode();
    }

    public String getTagName()
    {
        return this.tagName;
    }

    /**
     * Returns whether the tag may occur multiple times in the descriptor.
     *
     * @return Whether multiple occurrences are allowed
     */
    public boolean isMultipleAllowed()
    {
        return this.multipleAllowed;
    }

    /**
     * Determines if a particular Tag is mergeable or not.
     * <p/>
     * Basically means if we have a {@link Mergeable} class registered for a tag instance.
     *
     * @return <code>true</code> if this tag is mergeable.
     */
    public boolean isMergeable()
    {
        return null != this.mergeableClass;
    }

    public String toString()
    {
        return getTagName();
    }

    /**
     * Creates an {@link Mergeable} instance from the registered class for this
     * tag instance.
     *
     * @return instance of {@link Mergeable}.
     * @throws Exception if there was an error creating an instance.
     */
    public Mergeable createMergeable( Element element )
        throws Exception
    {
        Constructor cons = this.mergeableClass.getConstructor( new Class[] { Element.class } );
        // XXX Is there a better way to determine this?
        if ( this.mergeableClass.getSuperclass().equals( AbstractMergeableElementList.class ) )
        {
            return (AbstractMergeableElementList) cons.newInstance( new Object[] { element } );
        }
        else if ( this.mergeableClass.getSuperclass().equals( AbstractMergeableElement.class ) )
        {
            return (AbstractMergeableElement) cons.newInstance( new Object[] { element } );
        }
        else
        {
            // TODO set up Logger
            // if (getLogger ().isErrorEnabled ())
            //     getLogger.error ( "Could not create Mergeable instance for specified class '" + this.mergeableClass + "'" );
            throw new Exception( "Could not create Mergeable instance for specified class " + "'" + this.mergeableClass
                + "'" );
        }
    }
}
