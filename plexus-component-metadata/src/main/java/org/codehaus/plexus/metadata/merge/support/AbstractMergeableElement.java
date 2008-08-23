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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.metadata.merge.MergeException;
import org.codehaus.plexus.metadata.merge.MergeStrategy;
import org.jdom.Content;
import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public abstract class AbstractMergeableElement
    extends AbstractMergeableSupport
{
    public AbstractMergeableElement( Element element )
    {
        super( element );
    }

    /**
     * Detects if there was a conflict, that is the specified element was
     * present in both dominant and recessive element-sets.
     * <p/>
     * This delegates to
     * {@link #isRecessiveElementInConflict(AbstractMergeableElement,List)}.
     *
     * @param re      Recessive element.
     * @param eltName Element name to test for.
     * @return <code>true</code> if there was a conflict of element.
     * @deprecated <em>use {@link #isRecessiveElementInConflict(AbstractMergeableElement,List)} instead.</em>
     */
    protected boolean isRecessiveElementInConflict( AbstractMergeableElement re, String eltName )
    {
        // return (null != getChild (eltName) && null != re.getChild (eltName));
        List l = new ArrayList();
        l.add( eltName );
        return isRecessiveElementInConflict( re, l );
    }

    /**
     * Detects if there was a conflict, that is the specified element was
     * present in both dominant and recessive element-sets.
     * <p/>
     * Use this to determine conflicts when the Dominant and Recessive element
     * sets are keyed with Composite keys.<br>
     * For instance: <code>&lt;component&gt;</code> is keyed on
     * <code>&lt;role&gt;</code> and <code>&lt;role-hint&gt;</code>.
     *
     * @param re
     * @param eltNameList List of elements that will be checked for values in both dominant and recessive sets.
     * @return
     */
    protected boolean isRecessiveElementInConflict( AbstractMergeableElement re, List eltNameList )
    {
        // give opportunity to subclasses to provide any custom Composite keys
        // for conflict checks.
        eltNameList = getElementNamesForConflictResolution( eltNameList );

        if ( null == eltNameList || eltNameList.size() == 0 )
        {
            return false;
        }

        // assuming the elements will conflict.
        for ( Iterator it = eltNameList.iterator(); it.hasNext(); )
        {
            String eltName = (String) it.next();
            String dEltValue = getChildTextTrim( eltName );
            String rEltValue = re.getChildTextTrim( eltName );
            if ( null == dEltValue || null == rEltValue || !dEltValue.equals( rEltValue ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the Element to be merged is to be sourced from Recessive
     * Element set.
     *
     * @param re      Recessive element.
     * @param eltName Element name to test for.
     * @return
     */
    protected boolean mergeableElementComesFromRecessive( AbstractMergeableElement re, String eltName )
    {
        return null == getChildText( eltName ) && null != re.getChildText( eltName );
    }

    /**
     * Simply delegate to
     *
     * @see Mergeable#merge(Mergeable,org.codehaus.plexus.metadata.merge.MergeStrategy)
     */
    public void merge( Mergeable me, MergeStrategy strategy )
        throws MergeException
    {
        // TODO set up a unit test for this!
        strategy.apply( this, me );
    }

    public void merge( Mergeable me )
        throws MergeException
    {
        if ( !isExpectedElementType( me ) )
        {
            // if (getLogger().isErrorEnabled)
            //     getLogger().error ("Cannot Merge dissimilar elements. (Expected : '" + getClass ().getName () + "', found '" + me.getClass ().getName () + "')");
            throw new MergeException( "Cannot Merge dissimilar elements. " + "(Expected : '" + getClass().getName() +
                "', found '" + me.getClass().getName() + "')" );
        }
        // recessive Component Element.
        AbstractMergeableElement rce = (AbstractMergeableElement) me;

        Set allowedTags = new HashSet();

        for ( int i = 0; i < getAllowedTags().length; i++ )
        {
            String tagName = getAllowedTags()[i].getTagName();

            allowedTags.add( tagName );

            List defaultConflictChecklist = new ArrayList();
            defaultConflictChecklist.add( tagName );

            if ( !isRecessiveElementInConflict( rce, defaultConflictChecklist ) &&
                mergeableElementComesFromRecessive( rce, tagName ) )
            {
                this.addContent( (Element) rce.getChild( tagName ).clone() );
                // else dominant wins in anycase!
            }
            else
            if ( getAllowedTags()[i].isMergeable() && isRecessiveElementInConflict( rce, defaultConflictChecklist ) )
            {
                // this allows for merging multiple/list of elements.
                try
                {
                    getAllowedTags()[i].createMergeable( this.getChild( tagName ) )
                        .merge( getAllowedTags()[i].createMergeable( rce.getChild( tagName ) ),
                                getDefaultMergeStrategy() );
                }
                catch ( Exception e )
                {
                    // TODO log to error
                    throw new MergeException(
                        "Unable to create Mergeable instance for tag " + "'" + getAllowedTags()[i] + "'.", e );
                }
            }
        }

        for ( Iterator i = me.getElement().getChildren().iterator(); i.hasNext(); )
        {
            Element child = (Element) i.next();

            if ( !allowedTags.contains( child.getName() ) )
            {
                // not yet merged, copy over
                element.addContent( (Content) child.clone() );
            }
        }

    }

}
