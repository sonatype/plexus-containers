package org.codehaus.plexus.test;

import org.codehaus.plexus.lifecycle.phase.Eeny;
import org.codehaus.plexus.lifecycle.phase.Meeny;
import org.codehaus.plexus.lifecycle.phase.Miny;
import org.codehaus.plexus.lifecycle.phase.Mo;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/** This component implements the custom lifecycle defined by the phases
 *
 *  Eeny
 *  Meeny
 *  Miny
 *  Mo
 *
 */
public class DefaultServiceH
    extends AbstractLogEnabled
    implements ServiceH, Eeny, Meeny, Miny, Mo
{
    public boolean eeny;
    public boolean meeny;
    public boolean miny;
    public boolean mo;

    // ----------------------------------------------------------------------
    // Lifecycle Management
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
