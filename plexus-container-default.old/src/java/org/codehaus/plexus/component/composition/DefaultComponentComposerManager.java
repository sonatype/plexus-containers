package org.codehaus.plexus.component.composition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class DefaultComponentComposerManager implements ComponentComposerManager
{
    private Map composerMap = new HashMap();

    private List componentComposers;
    
    private String defaultComponentComposerId = "field";

    public void assembleComponent( final Object component,
                                   final ComponentDescriptor componentDescriptor,
                                   final PlexusContainer container )
            throws CompositionException, UndefinedComponentComposerException, ComponentLookupException
    {

        if ( componentDescriptor.getRequirements().size() == 0 )
        {
            //nothing to do
            return;
        }

        String componentComposerId = componentDescriptor.getComponentComposer();

        if ( StringUtils.isEmpty( componentComposerId ) )
        {
            componentComposerId = defaultComponentComposerId ;
        }

        final ComponentComposer componentComposer = getComponentComposer( componentComposerId );

        final List descriptors = componentComposer.assembleComponent( component, componentDescriptor, container );

        // @todo: michal: we need to build the graph of component dependencies
        // and detect cycles.  Not sure exactly when and how it should happen
        //assembleComponents( descriptors, container );
    }

    protected ComponentComposer getComponentComposer( final String id ) throws UndefinedComponentComposerException
    {
        ComponentComposer retValue = null;

        if ( composerMap.containsKey( id ) )
        {
            retValue = ( ComponentComposer ) composerMap.get( id );
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

    private ComponentComposer findComponentComposer( final String id )
    {
        ComponentComposer retValue = null;

        for ( Iterator iterator = componentComposers.iterator(); iterator.hasNext(); )
        {
            final ComponentComposer componentComposer = ( ComponentComposer ) iterator.next();

            if ( componentComposer.getId().equals( id ) )
            {
                retValue = componentComposer;

                break;
            }
        }

        return retValue;
    }
}
