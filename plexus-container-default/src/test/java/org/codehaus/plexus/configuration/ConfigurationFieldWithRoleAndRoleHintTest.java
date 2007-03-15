package org.codehaus.plexus.configuration;

import java.io.InputStream;
import java.util.HashMap;

import junit.framework.TestCase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

/*
 * Copyright 2007 The Codehaus Foundation.
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
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 15 mars 07
 * @version $Id$
 */
public class ConfigurationFieldWithRoleAndRoleHintTest
    extends TestCase
{

    private PlexusContainer plexusContainer;

    protected void setUp()
        throws Exception
    {
        String configuration = getClass().getName().replace( '.', '/' ) + ".xml";
        InputStream config = Thread.currentThread().getContextClassLoader().getResourceAsStream( configuration );
        this.plexusContainer = new DefaultPlexusContainer( "test", new HashMap(), null, config );
    }

    public void testFooImpl()
        throws Exception
    {
        try
        {
            MockComponent withFooOne = (MockComponent) this.plexusContainer.lookup( MockComponent.ROLE, "withfoo" );
            assertEquals( "foo", withFooOne.getMockComponentConfigurationField().getName() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void testBarImpl()
        throws Exception
    {
        try
        {
            MockComponent withFooOne = (MockComponent) this.plexusContainer.lookup( MockComponent.ROLE, "withbar" );
            assertEquals( "bar", withFooOne.getMockComponentConfigurationField().getName() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

}
package org.codehaus.plexus.configuration;

import java.io.InputStream;
import java.util.HashMap;

import junit.framework.TestCase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

/*
 * Copyright 2007 The Codehaus Foundation.
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
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 15 mars 07
 * @version $Id$
 */
public class ConfigurationFieldWithRoleAndRoleHintTest
    extends TestCase
{

    private PlexusContainer plexusContainer;

    protected void setUp()
        throws Exception
    {
        String configuration = getClass().getName().replace( '.', '/' ) + ".xml";
        InputStream config = Thread.currentThread().getContextClassLoader().getResourceAsStream( configuration );
        this.plexusContainer = new DefaultPlexusContainer( "test", new HashMap(), null, config );
    }

    public void testFooImpl()
        throws Exception
    {
        try
        {
            MockComponent withFooOne = (MockComponent) this.plexusContainer.lookup( MockComponent.ROLE, "withfoo" );
            assertEquals( "foo", withFooOne.getMockComponentConfigurationField().getName() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void testBarImpl()
        throws Exception
    {
        try
        {
            MockComponent withFooOne = (MockComponent) this.plexusContainer.lookup( MockComponent.ROLE, "withbar" );
            assertEquals( "bar", withFooOne.getMockComponentConfigurationField().getName() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

}
