package org.codehaus.plexus.logging;

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
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MockLoggerManager
    implements LoggerManager
{
    public void setThreshold(int threshold)
    {
    }

    public void setThresholds(int threshold)
    {
    }

    public int getThreshold()
    {
        return 0;
    }

    public void setThreshold(String role, int threshold)
    {
    }

    public void setThreshold(String role, String roleHint, int threshold)
    {
    }

    public int getThreshold(String role)
    {
        return 0;
    }

    public int getThreshold(String role, String roleHint)
    {
        return 0;
    }

    public Logger getLoggerForComponent(String role)
    {
        return new MockLogger(role.getClass().getName());
    }

    public Logger getLoggerForComponent(String role, String roleHint)
    {
        return new MockLogger(role.getClass().getName() + ":" + roleHint);
    }

    public void returnComponentLogger(String role)
    {
    }

    public void returnComponentLogger(String role, String hint)
    {
    }

    public int getActiveLoggerCount()
    {
        return 0;
    }
}
