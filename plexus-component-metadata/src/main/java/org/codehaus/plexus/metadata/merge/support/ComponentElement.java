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

import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class ComponentElement
    extends AbstractMergeableElement
{
    /**
     * Allowed elements/tags that we can expect under this element.
     */
    private final DescriptorTag[] allowedTags = {
        ROLE,
        ROLE_HINT,
        IMPLEMENTATION,
        FIELD_NAME,
        LIFECYCLE_HANDLER,
        DESCRIPTION,
        CONFIGURATION,
        RequirementsElement.TAG };

    static final DescriptorTag TAG = new DescriptorTag( "component", true, ComponentElement.class );

    static final DescriptorTag ROLE = new DescriptorTag( "role" );

    static final DescriptorTag ROLE_HINT = new DescriptorTag( "role-hint" );

    private static final DescriptorTag DESCRIPTION = new DescriptorTag( "description" );
    
    private static final DescriptorTag CONFIGURATION = new DescriptorTag( "configuration" );

    static final DescriptorTag FIELD_NAME = new DescriptorTag( "field-name" );

    private static final DescriptorTag IMPLEMENTATION = new DescriptorTag( "implementation" );

    private static final DescriptorTag LIFECYCLE_HANDLER = new DescriptorTag( "lifecycle-handler", false, null );

    public ComponentElement( Element element )
    {
        super( element );
    }

    protected boolean isExpectedElementType( Mergeable me )
    {
        return me instanceof ComponentElement;
    }

    public DescriptorTag[] getAllowedTags()
    {
        return allowedTags;
    }
}
