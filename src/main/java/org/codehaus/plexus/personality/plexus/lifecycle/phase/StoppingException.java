/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/**
 * Error occuring while starting a component.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class StoppingException
    extends Exception
{
    public StoppingException( String message )
    {
        super( message );
    }

    public StoppingException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
