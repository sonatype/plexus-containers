package org.codehaus.plexus.test;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
