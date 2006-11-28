package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

/**
 * @todo this little example works but is indicative of of some decoupling that
 * needs to happen wrt the lifecycle handlers. We should be able to specify by
 * configuration which entities for a lifecycle handler are required.
 */
public class CompositionPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        ComponentDescriptor descriptor = manager.getComponentDescriptor();

        if ( descriptor.getRequirements() == null )
        {
            return;
        }

        try
        {
            manager.getContainer().getComponentComposerManager().assembleComponent( object, descriptor, manager.getContainer() );
        }
        catch ( CompositionException e )
        {
            throw new PhaseExecutionException( "Error composing component", e );
        }
        catch ( UndefinedComponentComposerException e )
        {
            throw new PhaseExecutionException( "Error composing component", e );
        }
    }
}
