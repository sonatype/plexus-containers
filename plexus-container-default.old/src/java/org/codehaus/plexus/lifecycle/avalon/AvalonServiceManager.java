package org.codehaus.plexus.lifecycle.avalon;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.ComponentLookupException;

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
    /** Plexus component repository. */
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
            throw new IllegalStateException( "ComponentRespository is null." );
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
        try
        {
            return componentRepository.lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServiceException( role, "Cannot find component: " + role, e);
        }
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
}
