package org.codehaus.plexus.component.repository.io;

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

import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo these are all really tools for dealing with xml configurations so they
 * should be packaged as such.
 */
public class PlexusTools
{
    public static PlexusConfiguration buildConfiguration( String resourceName, Reader configuration )
        throws PlexusConfigurationException
    {
        try
        {
            return new XmlPlexusConfiguration( Xpp3DomBuilder.build( configuration ) );
        }
        catch ( XmlPullParserException e )
        {
            throw new PlexusConfigurationException( "Failed to parse configuration resource: \'" + resourceName + "\'\nError was: \'" + e.getLocalizedMessage() + "\'", e );
        }
        catch ( IOException e )
        {
            throw new PlexusConfigurationException( "IO error building configuration from: " + resourceName, e );
        }
    }

    public static PlexusConfiguration buildConfiguration( String configuration )
        throws PlexusConfigurationException
    {
        return buildConfiguration( "<String Memory Resource>", new StringReader( configuration ) );
    }

    public static ComponentDescriptor buildComponentDescriptor( String configuration )
        throws PlexusConfigurationException
    {
        return buildComponentDescriptor( buildConfiguration( configuration ) );
    }

    public static ComponentDescriptor buildComponentDescriptor( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        ComponentDescriptor cd = new ComponentDescriptor();

        cd.setRole( configuration.getChild( "role" ).getValue() );

        cd.setRoleHint( configuration.getChild( "role-hint" ).getValue() );

        cd.setImplementation( configuration.getChild( "implementation" ).getValue() );

        cd.setVersion( configuration.getChild( "version" ).getValue() );

        cd.setComponentType( configuration.getChild( "component-type" ).getValue() );

        cd.setInstantiationStrategy( configuration.getChild( "instantiation-strategy" ).getValue() );

        cd.setLifecycleHandler( configuration.getChild( "lifecycle-handler" ).getValue() );

        cd.setComponentProfile( configuration.getChild( "component-profile" ).getValue() );

        cd.setComponentComposer( configuration.getChild( "component-composer" ).getValue() );

        cd.setComponentConfigurator( configuration.getChild( "component-configurator" ).getValue() );

        cd.setComponentFactory( configuration.getChild( "component-factory" ).getValue() );

        cd.setDescription( configuration.getChild( "description" ).getValue() );

        cd.setAlias( configuration.getChild( "alias" ).getValue() );

        String s = configuration.getChild( "isolated-realm" ).getValue();

        if ( s != null )
        {
            cd.setIsolatedRealm( s.equals( "true" ) ? true : false );
        }

        // ----------------------------------------------------------------------
        // Here we want to look for directives for inlining external
        // configurations. we probably want to take them from files or URLs.
        // ----------------------------------------------------------------------

        cd.setConfiguration( configuration.getChild( "configuration" ) );

        // ----------------------------------------------------------------------
        // Requirements
        // ----------------------------------------------------------------------

        PlexusConfiguration[] requirements = configuration.getChild( "requirements" ).getChildren( "requirement" );

        for ( int i = 0; i < requirements.length; i++ )
        {
            PlexusConfiguration requirement = requirements[i];

            ComponentRequirement cr = new ComponentRequirement();

            cr.setRole( requirement.getChild( "role" ).getValue() );

            cr.setRoleHint( requirement.getChild( "role-hint" ).getValue() );

            cr.setFieldName( requirement.getChild( "field-name" ).getValue() );

            cd.addRequirement( cr );
        }

        return cd;
    }

    public static ComponentSetDescriptor buildComponentSet( PlexusConfiguration c )
        throws PlexusConfigurationException
    {
        ComponentSetDescriptor csd = new ComponentSetDescriptor();

        // ----------------------------------------------------------------------
        // Components
        // ----------------------------------------------------------------------

        PlexusConfiguration[] components = c.getChild( "components" ).getChildren( "component" );

        for ( int i = 0; i < components.length; i++ )
        {
            PlexusConfiguration component = components[i];

            csd.addComponentDescriptor( buildComponentDescriptor( component ) );
        }

        // ----------------------------------------------------------------------
        // Dependencies
        // ----------------------------------------------------------------------

        PlexusConfiguration[] dependencies = c.getChild( "dependencies" ).getChildren( "dependency" );

        for ( int i = 0; i < dependencies.length; i++ )
        {
            PlexusConfiguration d = dependencies[i];

            ComponentDependency cd = new ComponentDependency();

            cd.setArtifactId( d.getChild( "artifact-id" ).getValue() );

            cd.setGroupId( d.getChild( "group-id" ).getValue() );

            String type = d.getChild( "type" ).getValue();
            if(type != null)
            {
                cd.setType( type );
            }

            cd.setVersion( d.getChild( "version" ).getValue() );

            csd.addDependency( cd );
        }

        return csd;
    }
}
