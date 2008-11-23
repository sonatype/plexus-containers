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
import java.io.IOException;
import java.util.List;

import org.jdom.Document;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public interface Merger
{
    String ROLE = Merger.class.getName();

    /**
     * Merge with the recessive document.
     * 
     * @param dDocument the dominant document.
     * @param rDocument the recessive document.
     * @return the merged {@link Document} instance.
     *
     * @throws MergeException if there was an error in merge.
     */
    Document merge( Document dDocument, Document rDocument )
        throws MergeException;

    /**
     * Allows writing out a merged JDom Document to the specified file.
     * 
     * @param mergedDocument the merged {@link Document} instance.
     * @param file File to write the merged contents to.
     * @throws IOException if there was an error while writing merged contents to the specified file.
     */
    void writeMergedDocument( Document mergedDocument, File file )
        throws IOException;
    
    void mergeDescriptors( File outputDescriptor, List<File> descriptors )
        throws IOException;    
}
