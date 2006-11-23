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

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.util.StringUtils;

/**
 * @todo (michal) should this phase be called only for components which
 *  does not implement Configurable interface?
 */
public class AutoConfigurePhase
    extends AbstractPhase
{
    public static final String DEFAULT_CONFIGURATOR_ID = "basic";
    
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        try
        {
            ComponentDescriptor descriptor = manager.getComponentDescriptor();
            
            String configuratorId = descriptor.getComponentConfigurator();
            
            if(StringUtils.isEmpty(configuratorId))
            {
                configuratorId = DEFAULT_CONFIGURATOR_ID;
            }
            
            ComponentConfigurator componentConfigurator =
                (ComponentConfigurator) manager.getContainer().lookup( ComponentConfigurator.ROLE, configuratorId );

            if ( manager.getComponentDescriptor().hasConfiguration() )
            {
                componentConfigurator.configureComponent( object, manager.getComponentDescriptor().getConfiguration(), manager.getContainer().getContainerRealm() );
            }
        }
        catch ( ComponentLookupException e )
        {
            throw new PhaseExecutionException( "Unable to auto-configure component as its configurator could not be found", e );
        }
        catch ( ComponentConfigurationException e )
        {
            throw new PhaseExecutionException( "Unable to auto-configure component", e );
        }
    }
}
