package org.codehaus.plexus.lifecycle.avalon;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.DefaultComponentRepository;
import org.codehaus.plexus.configuration.DefaultConfiguration;

/**
 * A ComponentRepository for Avalon services that creates ServiceSelectors
 * for id'd components.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:jvanzyl@maven.org">Jason van Zyl</a>
 * @since May 10, 2003
 */
public class AvalonComponentRepository
    extends DefaultComponentRepository
{
    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        int i = componentKey.indexOf( "Selector" );

        if ( i > 0 && !hasService( componentKey ) )
        {
            ComponentDescriptor d = new ComponentDescriptor();

            String role = componentKey.substring( 0, i );

            d.setRole( componentKey );

            d.setImplementation( "org.codehaus.plexus.lifecycle.avalon.AvalonServiceSelector" );

            DefaultConfiguration configuration = new DefaultConfiguration( "configuration" );

            DefaultConfiguration selectableRole = new DefaultConfiguration( "selectable-role" );

            selectableRole.setValue( role );

            configuration.addChild( selectableRole );

            d.setConfiguration( configuration );

            try
            {
                addComponentDescriptor( d );
            }
            catch ( ComponentRepositoryException e )
            {
            }
        }

        return super.lookup( componentKey );

    }
}
