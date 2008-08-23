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

import org.codehaus.plexus.metadata.merge.MergeException;
import org.codehaus.plexus.metadata.merge.MergeStrategy;
import org.jdom.Element;

/**
 * Interface that marks an implementing entity as <b>mergeable</b>.<p>
 * Not all the elements/tags are expected to implement this interface. <br>
 * It should be implemented by elements/tags that need to have a certain control on how elements of the same type are merged with them.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public interface Mergeable
{
    /**
     * Merges an element of same type.
     *
     * @param me Another entity that is mergeable.
     * @throws MergeException if there was an error merging the mergeables.
     */
    void merge( Mergeable me )
        throws MergeException;

    /**
     * Applies the passed in {@link MergeStrategy} to merge two {@link Mergeable} instance.<p>
     *
     * @param me Recessive {@link Mergeable} instance.
     * @param strategy {@link MergeStrategy} to apply for merging.
     * @throws MergeException if there was an error while merging.
     */
    void merge( Mergeable me, MergeStrategy strategy )
        throws MergeException;

    /**
     * Returns the wrapped up JDom {@link Element} instance that was used to create this Mergeable.
     *
     * @return the wrapped up JDom {@link Element} instance.
     */
    Element getElement();

    /**
     * Returns an array of tags/elements that are allowed under the current
     * element.
     *
     * @return the allowedTags
     */
    DescriptorTag[] getAllowedTags();
}
