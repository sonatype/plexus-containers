/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */
package org.codehaus.plexus.logging.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

class MockAppender
    implements Appender
{
    boolean m_output;
    Level m_priority;
    String m_message;
    Throwable m_throwable;

    public void doAppend( LoggingEvent event )
    {
        m_output = true;
        m_priority = event.getLevel();
        m_message = (String) event.getMessage();
        final ThrowableInformation information = event.getThrowableInformation();
        if ( null != information )
        {
            m_throwable = information.getThrowable();
        }
    }

    public void addFilter( Filter filter )
    {
    }

    public Filter getFilter()
    {
        return null;
    }

    public void clearFilters()
    {
    }

    public void close()
    {
    }

    public String getName()
    {
        return null;
    }

    public void setErrorHandler( ErrorHandler errorHandler )
    {
    }

    public ErrorHandler getErrorHandler()
    {
        return null;
    }

    public void setLayout( Layout layout )
    {
    }

    public Layout getLayout()
    {
        return null;
    }

    public void setName( String classname )
    {
    }

    public boolean requiresLayout()
    {
        return false;
    }
}
