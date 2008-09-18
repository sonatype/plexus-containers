package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2006, Codehaus.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.util.List;

/**
 * @goal merge-test-metadata
 * 
 * @phase process-test-resources
 * 
 * @description Merges all Plexus descriptors in the test sources.
 * 
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusTestMergeMojo
    extends AbstractMergeMojo
{
    /**
     * @parameter expression="${project.testResources}"
     * @required
     */
    private List resources;

    /**
     * @parameter default-value="${project.build.testOutputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    private File output;

    protected List getResources()
    {
        return resources;
    }

    protected File getOutput()
    {
        return output;
    }
}
