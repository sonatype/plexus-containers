package org.codehaus.plexus.service.repository.instance;

import org.codehaus.plexus.service.repository.ComponentHousing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * <p>Created on 20/07/2003</p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public abstract class AbstractMultipleInstanceManager
    extends AbstractInstanceManager
{

    /** Component housings keyed by component. Used so we can dispose of
     * components. Need a threadsafe map as we could be putting and setting at the same
     * time. Were are putting and getting in equals amounts so a synchronized map is best*/
    private Map housings;

    /**
     *
     */
    public AbstractMultipleInstanceManager()
    {
        super();
        housings = Collections.synchronizedMap( new HashMap() );
    }

    protected ComponentHousing getHousing( Object component )
    {
        return (ComponentHousing) housings.get( component );
    }

    protected ComponentHousing removeHousing( Object component )
    {
        return (ComponentHousing) housings.remove( component );
    }

    protected void putHousing( Object component, ComponentHousing housing )
    {
        housings.put( component, housing );
    }

}
