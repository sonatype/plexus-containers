package org.codehaus.plexus.lifecycle.avalon;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;

/**
 * PlexusServiceSelector selects components from the PlexusServiceBroker based
 * on their id.
 *
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 11, 2003
 */
public class AvalonServiceSelector
    implements ServiceSelector, Serviceable, Configurable
{
    /** Service Manager. */
    private ServiceManager serviceManager;

    /** Role being selected. */
    private String selectableRole;

    /** Selectable role configuration tag. */
    public final static String SELECTABLE_ROLE_KEY = "selectable-role";

    /**
     * @see org.apache.avalon.framework.service.ServiceSelector#select(java.lang.Object)
     */
    public Object select( Object hint )
        throws ServiceException
    {
        return serviceManager.lookup( selectableRole + hint );
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceSelector#isSelectable(java.lang.Object)
     */
    public boolean isSelectable( Object hint )
    {
        return serviceManager.hasService( selectableRole + hint );
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceSelector#release(java.lang.Object)
     */
    public void release( Object component )
    {
        serviceManager.release( component );
    }

    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        this.serviceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        selectableRole = configuration.getAttribute( SELECTABLE_ROLE_KEY );
    }
}
