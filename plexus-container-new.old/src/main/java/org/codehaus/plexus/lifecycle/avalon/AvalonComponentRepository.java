package org.codehaus.plexus.lifecycle.avalon;

import org.codehaus.plexus.configuration.DefaultConfiguration;
import org.codehaus.plexus.service.repository.ComponentDescriptor;
import org.codehaus.plexus.service.repository.DefaultComponentRepository;

/**
 * A ComponentRepository for Avalon services that creates ServiceSelectors
 * for id'd components.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class AvalonComponentRepository extends DefaultComponentRepository
{
    /**
     * Adds a ComponentDescriptor.  If the descriptor has an Id or a RoleHint
     * a ServiceSelector is created also.
     * 
     * @see org.codehaus.plexus.service.repository.DefaultComponentRepository#addComponentDescriptor(org.codehaus.plexus.service.repository.ComponentDescriptor)
     */
    protected void addComponentDescriptor(ComponentDescriptor descriptor)
    {   
        super.addComponentDescriptor( descriptor );
        
        if ( descriptor.getId() != null
             ||
             descriptor.getRoleHint() != null )
        {
            if ( !hasService( descriptor.getRole() + "Selector" ) )
            {
                addServiceSelector(descriptor);
            }
        }
    }

    /**
     * @param descriptor
     */
    private void addServiceSelector(ComponentDescriptor descriptor)
    {
        ComponentDescriptor selDesc = createSelectorDescriptor( descriptor );

        addComponentDescriptor( selDesc );
    }

    /**
     * Create a ComponentDescriptor that represents a ServiceSelector for
     * a specified descriptor.
     * 
     * @param descriptor
     * @return
     */
    private ComponentDescriptor createSelectorDescriptor(ComponentDescriptor descriptor)
    {
        ComponentDescriptor selector = new ComponentDescriptor();
        selector.setRole( descriptor.getRole() + "Selector" );
        selector.setImplementation( AvalonServiceSelector.class.getName() );
        selector.setInstantiationStrategy( SINGLETON_STRATEGY );
        
        DefaultConfiguration config = new DefaultConfiguration( selector.getRole() );
        config.setAttribute( AvalonServiceSelector.SELECTABLE_ROLE_KEY, 
                             descriptor.getRole() );
        
        selector.setConfiguration( config );
        return selector;
    }
}
