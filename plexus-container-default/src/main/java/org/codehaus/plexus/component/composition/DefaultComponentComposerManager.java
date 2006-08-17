package org.codehaus.plexus.component.composition;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public class DefaultComponentComposerManager
    implements ComponentComposerManager
{
    private static final String DEFAULT_COMPONENT_COMPOSER_ID = "field";

    private Map composerMap = new HashMap();

    private List componentComposers;

    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container )
        throws UndefinedComponentComposerException, CompositionException
    {

        if ( componentDescriptor.getRequirements().size() == 0 )
        {
            //nothing to do
            return;
        }

        String componentComposerId = componentDescriptor.getComponentComposer();

        if ( componentComposerId == null || componentComposerId.trim().length() == 0 )
        {
            componentComposerId = DEFAULT_COMPONENT_COMPOSER_ID;
        }

        ComponentComposer componentComposer = getComponentComposer( componentComposerId );

        componentComposer.assembleComponent( component, componentDescriptor, container );
    }

    protected ComponentComposer getComponentComposer( String id )
        throws UndefinedComponentComposerException
    {
        ComponentComposer retValue = null;

        if ( composerMap.containsKey( id ) )
        {
            retValue = (ComponentComposer) composerMap.get( id );
        }
        else
        {
            retValue = findComponentComposer( id );
        }

        if ( retValue == null )
        {
            throw new UndefinedComponentComposerException( "Specified component composer cannot be found: " + id );
        }

        return retValue;
    }

    private ComponentComposer findComponentComposer( String id )
    {
        ComponentComposer retValue = null;

        for ( Iterator iterator = componentComposers.iterator(); iterator.hasNext(); )
        {
            ComponentComposer componentComposer = (ComponentComposer) iterator.next();

            if ( componentComposer.getId().equals( id ) )
            {
                retValue = componentComposer;

                break;
            }
        }

        return retValue;
    }
}
