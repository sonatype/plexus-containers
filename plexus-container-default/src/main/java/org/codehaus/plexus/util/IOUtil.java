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
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public final class IOUtil
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static void copy( final InputStream input, final OutputStream output )
        throws IOException
    {
        copy( input, output, DEFAULT_BUFFER_SIZE );
    }

    public static void copy( final InputStream input,
                             final OutputStream output,
                             final int bufferSize )
        throws IOException
    {
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        while ( -1 != ( n = input.read( buffer ) ) )
        {
            output.write( buffer, 0, n );
        }
    }

    public static void copy( final Reader input, final Writer output )
        throws IOException
    {
        copy( input, output, DEFAULT_BUFFER_SIZE );
    }

    public static void copy( final Reader input, final Writer output, final int bufferSize )
        throws IOException
    {
        final char[] buffer = new char[bufferSize];
        int n = 0;
        while ( -1 != ( n = input.read( buffer ) ) )
        {
            output.write( buffer, 0, n );
        }
        output.flush();
    }

    public static void copy( final InputStream input, final Writer output )
        throws IOException
    {
        copy( input, output, DEFAULT_BUFFER_SIZE );
    }

    public static void copy( final InputStream input, final Writer output, final int bufferSize )
        throws IOException
    {
        final InputStreamReader in = new InputStreamReader( input );
        copy( in, output, bufferSize );
    }

    public static void copy( final Reader input, final OutputStream output )
        throws IOException
    {
        copy( input, output, DEFAULT_BUFFER_SIZE );
    }

    public static void copy( final Reader input, final OutputStream output, final int bufferSize )
        throws IOException
    {
        final OutputStreamWriter out = new OutputStreamWriter( output );
        copy( input, out, bufferSize );
        // NOTE: Unless anyone is planning on rewriting OutputStreamWriter, we have to flush
        // here.
        out.flush();
    }

    public static void copy( final String input, final OutputStream output, final int bufferSize )
        throws IOException
    {
        final StringReader in = new StringReader( input );
        final OutputStreamWriter out = new OutputStreamWriter( output );
        copy( in, out, bufferSize );
        // NOTE: Unless anyone is planning on rewriting OutputStreamWriter, we have to flush
        // here.
        out.flush();
    }

    // ----------------------------------------------------------------------
    // closeXXX()
    // ----------------------------------------------------------------------

    public static void close( InputStream inputStream )
    {
        if ( inputStream == null )
        {
            return;
        }

        try
        {
            inputStream.close();
        }
        catch( IOException ex )
        {
            // ignore
        }
    }

    public static void close( OutputStream outputStream )
    {
        if ( outputStream == null )
        {
            return;
        }

        try
        {
            outputStream.close();
        }
        catch( IOException ex )
        {
            // ignore
        }
    }

    public static void close( Reader reader )
    {
        if ( reader == null )
        {
            return;
        }

        try
        {
            reader.close();
        }
        catch( IOException ex )
        {
            // ignore
        }
    }

    public static void close( Writer writer )
    {
        if ( writer == null )
        {
            return;
        }

        try
        {
            writer.close();
        }
        catch( IOException ex )
        {
            // ignore
        }
    }

    // used

    public static String toString( final Reader input )
        throws IOException
    {
        return toString( input, DEFAULT_BUFFER_SIZE );
    }

    /**
     * Get the contents of a <code>Reader</code> as a String.
     * @param bufferSize Size of internal buffer to use.
     */
    public static String toString( final Reader input, final int bufferSize )
        throws IOException
    {
        final StringWriter sw = new StringWriter();
        copy( input, sw, bufferSize );
        return sw.toString();
    }

}
