package org.codehaus.plexus.lifecycle.avalon;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.configuration.DefaultConfiguration;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.DefaultComponentRepository;

/**
 * A ComponentRepository for Avalon services that creates ServiceSelectors
 * for id'd components.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class AvalonComponentRepository
    extends DefaultComponentRepository
{
    /** Tag for selectors. */
    private static String SELECTORS = "selectors";

    /** Tag for selector. */
    private static String SELECTOR = "selector";

    /** Default instantiation strategy tag. */
    private static String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    /** Role tag. */
    private static String ROLE = "role";

    /** Implementation tag. */
    private static String IMPLEMENTATION = "implementation";

    /**
     * Adds a ComponentDescriptor.  If the descriptor has an Id or a RoleHint
     * a ServiceSelector is created also.
     *
     * @see org.codehaus.plexus.component.repository.DefaultComponentRepository#addComponentDescriptor(org.codehaus.plexus.component.repository.ComponentDescriptor)
     */
    protected void addComponentDescriptor( ComponentDescriptor descriptor )
    {
        super.addComponentDescriptor( descriptor );

        if ( descriptor.getRoleHint() != null )
        {
            if ( !hasService( descriptor.getRole() + "Selector" ) )
            {
                addServiceSelector( descriptor );
            }
        }
    }

    /**
     * @param descriptor
     */
    private void addServiceSelector( ComponentDescriptor descriptor )
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
    private ComponentDescriptor createSelectorDescriptor( ComponentDescriptor descriptor )
    {
        ComponentDescriptor selector = new ComponentDescriptor();

        selector.setRole( descriptor.getRole() + "Selector" );
        selector.setImplementation( AvalonServiceSelector.class.getName() );
        selector.setInstantiationStrategy( DEFAULT_INSTANTIATION_STRATEGY );

        DefaultConfiguration config = new DefaultConfiguration( selector.getRole() );
        config.setAttribute( AvalonServiceSelector.SELECTABLE_ROLE_KEY, descriptor.getRole() );

        selector.setConfiguration( config );
        return selector;
    }

    /**
     * @see org.codehaus.plexus.component.repository.ComponentRepository#initialize()
     */
    public void initialize() throws Exception
    {
        super.initialize();
        initializeSelectors();
    }

    /**
     * 
     */
    private void initializeSelectors()
        throws ConfigurationException
    {
        Configuration[] configuration = getConfiguration().getChild( SELECTORS ).getChildren( SELECTOR );
        
        for ( int i = 0; i < configuration.length; i++ )
        {
            ComponentDescriptor descriptor = createSelectorComponentDescriptor( configuration[i] );
            super.addComponentDescriptor( descriptor );
        }
    }

    /**
     * Create a ComponentDescriptor for the custom ServiceSelector;
     * @param configuration
     */
    private ComponentDescriptor createSelectorComponentDescriptor(Configuration configuration)
        throws ConfigurationException
    {
        ComponentDescriptor selector = new ComponentDescriptor();
        
        String role = configuration.getChild( ROLE ).getValue();
        
        selector.setRole( role + "Selector" );
        selector.setImplementation( configuration.getChild( IMPLEMENTATION ).getValue() );
        selector.setInstantiationStrategy( DEFAULT_INSTANTIATION_STRATEGY );

        DefaultConfiguration config = new DefaultConfiguration( selector.getRole() );
        config.setAttribute( AvalonServiceSelector.SELECTABLE_ROLE_KEY, role );

        selector.setConfiguration( config );
        return selector;
    }

}
