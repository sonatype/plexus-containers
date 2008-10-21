package org.codehaus.plexus.metadata.merge;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.codehaus.plexus.util.IOUtil;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * Base class for common mergers.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 */
public abstract class AbstractMerger
    implements Merger
{
    /**
     * @see org.codehaus.plexus.metadata.merge.Merger#writeMergedDocument(org.jdom.Document, java.io.File)
     */
    public void writeMergedDocument( Document mergedDocument, File file )
        throws IOException
    {
        if ( !file.getParentFile().exists() )
        {
            file.getParentFile().mkdirs();
        }

        XMLOutputter out = new XMLOutputter();
        Writer fw = null;
        try
        {
            fw = new OutputStreamWriter( new FileOutputStream( file ), "UTF-8" );
            out.output( mergedDocument, fw );
        }
        finally
        {
            IOUtil.close( fw );
        }
    }
}
