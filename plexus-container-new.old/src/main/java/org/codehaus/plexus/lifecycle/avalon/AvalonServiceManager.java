package org.codehaus.plexus.lifecycle.avalon;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.component.repository.ComponentRepository;

/**
 * Extended <code>ServiceManager</code> implementation.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 *
 * @version $Id$
 */

public class AvalonServiceManager
    extends AbstractLogEnabled
    implements ServiceManager
{
    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /** */
    private ComponentRepository componentRepository;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     *
     * @todo We shouldn't be handing the parent container to the child here.
     */
    public AvalonServiceManager( ComponentRepository componentRepository )
    {
        if ( componentRepository == null )
        {
            throw new IllegalStateException(
                "You cannot initialize an AvalonServiceManager with a null ComponentRespository" );
        }

        this.componentRepository = componentRepository;
    }

    // ----------------------------------------------------------------------
    // Avalon ServiceManager API
    // ----------------------------------------------------------------------

    /**
     * @see ComponentRepository#lookup(String)
     */
    public Object lookup( String role )
        throws ServiceException
    {
        // We have to add some magic here to deal with Selectors.
        return componentRepository.lookup( role );
    }

    /**
     * @see ComponentRepository#hasService(String)
     */
    public boolean hasService( String role )
    {
        return componentRepository.hasService( role );
    }

    /**
     * @see ComponentRepository#release(Object)
     */
    public void release( Object service )
    {
        componentRepository.release( service );
    }

    // ----------------------------------------------------------------------
    // Additional methods for dealing with component lookups by id.
    // ----------------------------------------------------------------------

    /**
     * @see ComponentRepository#lookup(String,String)
     */
    public Object lookup( String role, String id )
        throws ServiceException
    {
        return componentRepository.lookup( role, id );
    }

    /**
     * @see ComponentRepository#hasService(String,String)
     */
    public boolean hasService( String role, String id )
    {
        return componentRepository.hasService( role, id );
    }

    /**
     * @see ComponentRepository#dispose()
     */
    public void dispose()
    {
        componentRepository.dispose();
    }
}
