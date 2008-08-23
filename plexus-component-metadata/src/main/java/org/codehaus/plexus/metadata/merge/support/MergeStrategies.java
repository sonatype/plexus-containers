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

import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.metadata.merge.MergeException;
import org.codehaus.plexus.metadata.merge.MergeStrategy;

/**
 * Collection of available Merge Strategies.<p>
 * TODO: Revisit and factor {@link Mergeable#merge(Mergeable)} to use a {@link MergeStrategy}.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class MergeStrategies
{
    /**
     * {@link MergeStrategy} implementation wherein the elements are merged
     * down to the deepest available {@link Mergeable} instance in the DOM tree.
     */
    public static final MergeStrategy DEEP = new MergeStrategy()
    {
        public void apply( Mergeable dElt, Mergeable rElt )
            throws MergeException
        {
            dElt.merge( rElt );
        }
    };

    /**
     * {@link MergeStrategy} implementation wherein only the element on
     * which the merge operation is called is 'merged'. The merge does not
     * traverse the DOM tree any further.
     */
    public static final MergeStrategy SHALLOW = new MergeStrategy()
    {
        /**
         * @throws MergeException
         * @see org.codehaus.plexus.metadata.merge.MergeStrategy#apply(Mergeable,Mergeable)
         */
        public void apply( Mergeable dElt, Mergeable rElt )
            throws MergeException
        {
            AbstractMergeableElement dame = (AbstractMergeableElement) dElt;
            AbstractMergeableElement rame = (AbstractMergeableElement) rElt;

            // check if the dominant was in conflict with recessive.
            List elementNames = dame.getElementNamesForConflictResolution( Collections.EMPTY_LIST );

            if ( !dame.isRecessiveElementInConflict( rame, elementNames ) )
            {
                // no conflict, simply add recessive to dominant's parent
                dame.getElement().addContent( rame.getElement() );
            }
        }
    };
}
