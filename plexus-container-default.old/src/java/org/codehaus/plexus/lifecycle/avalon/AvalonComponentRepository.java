package org.codehaus.plexus.lifecycle.avalon;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.DefaultComponentRepository;
import org.codehaus.plexus.configuration.DefaultConfiguration;

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
    private String selector =
        "<component>" +
        "  <role>org.codehaus.plexus.ServiceSelectorr</role>" +
        "  <implementation>org.codehaus.plexus.lifecycle.avalon.AvalonServiceSelector</implementation>" +
        "  <configuration>" +
        "    <selectable-role>org.codehaus.plexus.ServiceC</selectable-role>" +
        "  </configuration>" +
        "</component>";

    public void initialize()
        throws Exception
    {
        super.initialize();


    }

    public Object lookup( String componentKey )
        throws ServiceException
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

            addComponentDescriptor( d );
        }

        return super.lookup( componentKey );

    }
}
