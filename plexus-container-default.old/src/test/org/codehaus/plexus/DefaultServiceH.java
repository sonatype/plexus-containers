package org.codehaus.plexus;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.lifecycle.phase.Mo;
import org.codehaus.plexus.lifecycle.phase.Miny;
import org.codehaus.plexus.lifecycle.phase.Meeny;
import org.codehaus.plexus.lifecycle.phase.Eeny;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.Logger;

/** This component implements all the start phases:
 *
 *  LogEnabled
 *  Contexualize
 *  Serviceable
 *  Configurable
 *  Initializable
 *  Startable
 *
 */
public class DefaultServiceH
    extends AbstractLogEnabled
    implements ServiceG, Eeny, Meeny, Miny, Mo
{
    boolean eeny;
    boolean meeny;
    boolean miny;
    boolean mo;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void eeny()
    {
        eeny = true;
    }

    public void meeny()
    {
        meeny = true;
    }

    public void miny()
    {
        miny = true;
    }

    public void mo()
    {
        mo = true;
    }
}
