package org.codehaus.plexus.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileUtils
{
    public static String FS = System.getProperty( "file.separator" );

    public static List getFiles( File directory, String includes, String excludes )
    {
        return getFiles( directory, includes, excludes, true );
    }

    public static List getFiles( File directory, String includes, String excludes, boolean includeBasedir )
    {
        List fileNames = getFileNames( directory, includes, excludes, includeBasedir );

        List files = new ArrayList();

        for ( Iterator i = fileNames.iterator(); i.hasNext(); )
        {
            files.add( new File( (String) i.next() ) );
        }

        return files;
    }

    public static List getFileNames( File directory, String includes, String excludes, boolean includeBasedir )
    {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( directory );

        if ( includes != null )
        {
            scanner.setIncludes( StringUtils.split( includes, "," ) );
        }

        if ( excludes != null )
        {
            scanner.setExcludes( StringUtils.split( excludes, "," ) );
        }

        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        List list = new ArrayList();

        for ( int i = 0; i < files.length; i++ )
        {
            if ( includeBasedir )
            {
                list.add( directory + FS + files[i] );
            }
            else
            {
                list.add( files[i] );
            }
        }

        return list;
    }

    // used by maven-artifact which requires plexus anyway.

    public static void copyFile( final File source, final File destination )
        throws IOException
    {
        //check source exists
        if ( !source.exists() )
        {
            final String message = "File " + source + " does not exist";
            throw new IOException( message );
        }

        //does destinations directory exist ?
        if ( destination.getParentFile() != null &&
            !destination.getParentFile().exists() )
        {
            destination.getParentFile().mkdirs();
        }

        //make sure we can write to destination
        if ( destination.exists() && !destination.canWrite() )
        {
            final String message = "Unable to open file " +
                destination + " for writing.";
            throw new IOException( message );
        }

        final FileInputStream input = new FileInputStream( source );
        final FileOutputStream output = new FileOutputStream( destination );
        IOUtil.copy( input, output );

        input.close();
        output.close();

        if ( source.length() != destination.length() )
        {
            final String message = "Failed to copy full contents from " + source +
                " to " + destination;
            throw new IOException( message );
        }
    }

    public static String fileRead( String file )
        throws IOException
    {
        return fileRead( new File( file ) );
    }

    public static String fileRead( File file )
        throws IOException
    {
        StringBuffer buf = new StringBuffer();

        FileInputStream in = new FileInputStream( file );

        int count;
        byte[] b = new byte[512];
        while ( ( count = in.read( b ) ) > 0 )  // blocking read
        {
            buf.append( new String( b, 0, count ) );
        }

        in.close();

        return buf.toString();
    }

    /**
     * Writes data to a file. The file will be created if it does not exist.
     *
     * @param fileName The name of the file to write.
     * @param data The content to write to the file.
     */
    public static void fileWrite( String fileName, String data )
        throws IOException
    {
        FileOutputStream out = new FileOutputStream( fileName );
        out.write( data.getBytes() );
        out.close();
    }
}
