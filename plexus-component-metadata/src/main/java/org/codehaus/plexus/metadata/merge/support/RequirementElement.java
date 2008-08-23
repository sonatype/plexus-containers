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
import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class RequirementElement
    extends AbstractMergeableElement
{
    static final DescriptorTag TAG = new DescriptorTag( "requirement", true, RequirementElement.class );

    public RequirementElement( Element element )
    {
        super( element );
    }

    public DescriptorTag[] getAllowedTags()
    {
        return new DescriptorTag[]{ComponentElement.ROLE, ComponentElement.ROLE_HINT, ComponentElement.FIELD_NAME};
    }

    public void merge( Mergeable me )
        throws MergeException
    {
        super.merge( me );
    }

    protected boolean isExpectedElementType( Mergeable me )
    {
        return me instanceof RequirementElement;
    }
}
