package org.codehaus.plexus.component.configurator.converters.composite;

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

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;


public abstract class AbstractCompositeConverter implements CompositeConverter
{
    private static final String IMPLEMENTATION = "implementation";

    /**
     * We will check if user has provided a hint which class should be used for given field.
     * So we will check if something like <foo implementation="com.MyFoo"> is present in configuraion.
     * If 'implementation' hint was provided we will try to load correspoding class
     * If we are unable to do so error will be reported
     */
    protected Class getClassForImplementationHint( Class type,
                                                   PlexusConfiguration configuration,
                                                   ClassLoader classLoader,
                                                   ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        Class retValue = type;

        String implementation = configuration.getAttribute( IMPLEMENTATION, null );
        
        if ( implementation != null )
        {
            try
            {
                retValue = classLoader.loadClass( implementation );

            }
            catch ( ClassNotFoundException e )
            {
                String msg = "Class name which was explicitly given in configuration using 'implementation' attribute: '"
                        + implementation + "' cannot be loaded: " + e.getMessage();

                throw new ComponentConfigurationException( msg );
            }
        }

        return retValue;
    }


    protected Class loadClass( String classname, ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        Class retValue = null;

        try
        {
            retValue = classLoader.loadClass( classname );
        }
        catch ( Exception e )
        {
            throw new ComponentConfigurationException( "Class '" + classname + "' cannot be loaded" );
        }

        return retValue;

    }

    protected Object instantiateObject( String classname, ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        Class clazz = loadClass( classname, classLoader );

        Object retValue = instantiateObject( clazz );

        return retValue;
    }

    protected Object instantiateObject( Class clazz ) throws ComponentConfigurationException
    {
        Object retValue = null;

        try
        {
            retValue = clazz.newInstance();

            return retValue;
        }
        catch ( Exception e )
        {
            throw new ComponentConfigurationException( "Class '" + clazz.getName() + "' cannot be instantiated" );
        }
    }
}
