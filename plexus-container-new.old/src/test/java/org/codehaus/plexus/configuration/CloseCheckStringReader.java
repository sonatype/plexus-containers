package org.codehaus.plexus.configuration;

import java.io.StringReader;

/**
 * Custom version of <code>StringReader</code> that can be used to verify
 * that a reader is closed properly.
 *
 * @author Mark Wilkinson
 * @version $Revision$
 */
final class CloseCheckStringReader extends StringReader
{
    private boolean closed = false;

    public CloseCheckStringReader( String s )
    {
        super( s );
    }

    public void close()
    {
        closed = true;
        super.close();
    }

    public boolean isClosed()
    {
        return closed;
    }
}
