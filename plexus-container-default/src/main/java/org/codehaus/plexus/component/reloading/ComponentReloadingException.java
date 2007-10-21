package org.codehaus.plexus.component.reloading;

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

/**
 * Exception for problems in the reloading strategy.
 * 
 * @author Jason van Zyl
 * @version $Revision$
 */
public class ComponentReloadingException
    extends Exception
{
    private static final long serialVersionUID = 2105955080579002718L;

    /**
     * Construct a new <code>ComponentReloadingException</code> instance.
     * @param message exception message
     */
    public ComponentReloadingException( String message )
    {
        super( message );
    }

    /**
     * Construct a new <code>ComponentReloadingException</code> instance.
     * @param cause causing exception to chain
     */
    public ComponentReloadingException( Throwable cause )
    {
        super( cause );
    }

    /**
     * Construct a new <code>ComponentReloadingException</code> instance.
     * @param message exception message
     * @param cause causing exception to chain
     */
    public ComponentReloadingException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
