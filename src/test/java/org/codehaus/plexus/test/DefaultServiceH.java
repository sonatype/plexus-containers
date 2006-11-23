package org.codehaus.plexus.test;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.test.lifecycle.phase.Eeny;
import org.codehaus.plexus.test.lifecycle.phase.Meeny;
import org.codehaus.plexus.test.lifecycle.phase.Miny;
import org.codehaus.plexus.test.lifecycle.phase.Mo;

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
