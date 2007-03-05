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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.LoggerManager;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LogDisablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager componentManager, ClassRealm lookupRealm )
        throws PhaseExecutionException
    {
        if ( object instanceof LogEnabled )
        {
            LoggerManager loggerManager;
            try
            {
                /* as we do not know what logger role hint has been configured pull the first logger we find. Andy
                 * TODO - figure how to make this more determanistic? */
                Map loggers = componentManager.getContainer().lookupMap( LoggerManager.ROLE, lookupRealm );
                loggerManager = (LoggerManager) loggers.get( loggers.keySet().iterator().next() );
            }
            catch ( ComponentLookupException e )
            {
                throw new PhaseExecutionException( "Unable to locate logger manager", e );
            }

            ComponentDescriptor descriptor = componentManager.getComponentDescriptor();
            loggerManager.returnComponentLogger( descriptor.getRole(), descriptor.getRoleHint() );
        }
    }
}
