package org.codehaus.plexus.component.repository;

import org.codehaus.plexus.PlexusTools;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.composition.CompositionResolver;
import org.codehaus.plexus.component.composition.DefaultCompositionResolver;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @todo We need to process component descriptors from a specified configuration file in addition
 * to component descriptors that are stored in the JAR along with the component. So we need to be
 * able to process a directory of components as we now can package any number of components
 * in a JAR which will all be described by a components.xml file in the top-level of the JAR.
 */
public class DefaultComponentRepository
    extends AbstractLogEnabled
    implements ComponentRepository
{
    private static String COMPONENTS = "components";

    private static String COMPONENT = "component";

    private PlexusConfiguration configuration;

    private Map componentDescriptorMaps;

    private Map componentDescriptors;

    private CompositionResolver compositionResolver;

    public DefaultComponentRepository()
    {
        componentDescriptors = new HashMap();

        componentDescriptorMaps = new HashMap();

        compositionResolver = new DefaultCompositionResolver();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    protected PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public boolean hasComponent( String role )
    {
        return componentDescriptors.containsKey( role );
    }

    public boolean hasComponent( String role, String id )
    {
        return componentDescriptors.containsKey( role + id );
    }

    public Map getComponentDescriptorMap( String role )
    {
        return (Map) componentDescriptorMaps.get( role );
    }

    public ComponentDescriptor getComponentDescriptor( String key )
    {
        return (ComponentDescriptor) componentDescriptors.get( key );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void configure( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        initializeComponentDescriptors();
    }

    public void initializeComponentDescriptors()
        throws Exception
    {
        initializeComponentDescriptorsFromComponents();

        initializeComponentDescriptorsFromUserConfiguration();
    }

    private void initializeComponentDescriptorsFromUserConfiguration()
        throws Exception
    {
        PlexusConfiguration[] componentConfigurations = configuration.getChild( COMPONENTS ).getChildren( COMPONENT );

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            addComponentDescriptor( componentConfigurations[i] );
        }
    }

    private void initializeComponentDescriptorsFromComponents()
        throws Exception
    {
        // This will consist of using the classworlds realm and searching for all
        // components.xml files and initializing the component descriptors
        // described within.
    }

    // ----------------------------------------------------------------------
    //  Component Descriptor processing.
    // ----------------------------------------------------------------------

    public void addComponentDescriptor( PlexusConfiguration configuration )
        throws ComponentRepositoryException
    {
        ComponentDescriptor componentDescriptor = null;
        try
        {
            componentDescriptor = PlexusTools.buildComponentDescriptor( configuration );
        }
        catch ( Exception e )
        {
            throw new ComponentRepositoryException( "Cannot unmarshall component descriptor:", e );
        }

        addComponentDescriptor( componentDescriptor );
    }

    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException
    {
        try
        {
            validateComponentDescriptor( componentDescriptor );
        }
        catch ( ComponentImplementationNotFoundException e )
        {
            throw new ComponentRepositoryException( "Component descriptor validation failed: ", e );
        }

        String roleHint = componentDescriptor.getRoleHint();

        if ( roleHint != null )
        {
            String role = componentDescriptor.getRole();

            Map map = (Map) componentDescriptorMaps.get( role );

            if ( map == null )
            {
                map = new HashMap();

                componentDescriptorMaps.put( role, map );
            }

            map.put( roleHint, componentDescriptor );
        }

        compositionResolver.addComponentDescriptor( componentDescriptor );

        componentDescriptors.put( componentDescriptor.getComponentKey(), componentDescriptor );
    }

    public void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentImplementationNotFoundException
    {
        // Make sure the component implementation classes can be found.
        // Make sure ComponentManager implementation can be found.
        // Validate lifecycle.
        // Validate the component configuration.
        // Validate the component profile if one is used.
    }

    public List getComponentDependencies( ComponentDescriptor componentDescriptor )
    {
        return compositionResolver.getRequirements( componentDescriptor.getComponentKey() );
    }
}
