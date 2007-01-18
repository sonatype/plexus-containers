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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ContextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager, ClassRealm lookupRealm )
        throws PhaseExecutionException
    {
        if ( object instanceof Contextualizable )
        {
            Context context = manager.getContainer().getContext();

            ClassRealm origRealm = DefaultPlexusContainer.setLookupRealm( lookupRealm );

            try
            {
                ( (Contextualizable) object ).contextualize( context );
            }
            catch ( ContextException e )
            {
                throw new PhaseExecutionException( "Unable to contextualize component", e );
            }
            finally
            {
                DefaultPlexusContainer.setLookupRealm( origRealm );
            }
        }
    }
}
