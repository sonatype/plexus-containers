package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentDescriptorBuilder
{
    /** Role tag. */
    private static String ROLE = "role";

    /** Role tag. */
    private static String ROLE_HINT = "role-hint";

    /** Implementation tag. */
    private static String IMPLEMENTATION = "implementation";

    /** Configuration tag. */
    private static String INSTANTIATION_STRATEGY = "instantiation-strategy";

    /** Configuration tag. */
    private static String CONFIGURATION = "configuration";

    /** Lifecycle handler tag. */
    private static String LIFECYCLE_HANDLER = "lifecycle-handler";

    /** Requirements tag. */
    private static String REQUIREMENTS = "requirements";

    /** Requirement tag. */
    private static String REQUIREMENT = "requirement";

    /**
     * Create a component descriptor.
     *
     * @param configuration
     * @return
     * @throws Exception
     */
    ComponentDescriptor build( Configuration configuration )
        throws Exception
    {
        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( configuration.getChild( ROLE ).getValue() );

        componentDescriptor.setRoleHint( configuration.getChild( ROLE_HINT ).getValue( null ) );

        componentDescriptor.setImplementation( configuration.getChild( IMPLEMENTATION ).getValue() );

        componentDescriptor.setInstantiationStrategy( configuration.getChild( INSTANTIATION_STRATEGY ).getValue( null ) );

        componentDescriptor.setLifecycleHandler( configuration.getChild( LIFECYCLE_HANDLER ).getValue( null ) );

        componentDescriptor.setConfiguration( configuration.getChild( CONFIGURATION ) );

        Configuration[] requirements = configuration.getChild( REQUIREMENTS ).getChildren( REQUIREMENT );

        for ( int i = 0; i < requirements.length; i++ )
        {
            componentDescriptor.addRequirement( requirements[i].getValue() );
        }

        return componentDescriptor;
    }
}
