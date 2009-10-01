package org.codehaus.plexus.component.repository.io;

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

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.io.XmlPlexusConfigurationReader;


/**
 * @author Jason van Zyl 
 */
public class PlexusTools
{
    public static PlexusConfiguration buildConfiguration( String resourceName, Reader configuration )
        throws PlexusConfigurationException
    {
        try
        {
            XmlPlexusConfigurationReader reader = new XmlPlexusConfigurationReader();
            
            PlexusConfiguration result = reader.read( configuration );

            return result;
        }
        catch ( PlexusConfigurationException e )
        {
            throw new PlexusConfigurationException( "PlexusConfigurationException building configuration from: " + resourceName, e );
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

    public static ComponentDescriptor<?> buildComponentDescriptor( String configuration, ClassRealm realm  )
        throws PlexusConfigurationException
    {
        return buildComponentDescriptor( buildConfiguration( configuration ), realm );
    }

    public static ComponentDescriptor<?> buildComponentDescriptor( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        return buildComponentDescriptorImpl( configuration, null );
    }
    
    public static ComponentDescriptor<?> buildComponentDescriptor( PlexusConfiguration configuration, ClassRealm realm )
        throws PlexusConfigurationException
    {
        if ( realm == null )
        {
            throw new NullPointerException( "realm is null" );
        }

        return buildComponentDescriptorImpl( configuration, realm );
    }

    private static ComponentDescriptor<?> buildComponentDescriptorImpl( PlexusConfiguration configuration,
                                                                        ClassRealm realm )
        throws PlexusConfigurationException
    {
        String implementation = configuration.getChild( "implementation" ).getValue();
        if (implementation == null)
        {
            throw new PlexusConfigurationException( "implementation is null" );
        }

        ComponentDescriptor<?> cd;
        try
        {
            if ( realm != null )
            {
                Class<?> implementationClass = realm.loadClass( implementation );
                cd = new ComponentDescriptor(implementationClass, realm);
            }
            else
            {
                cd = new ComponentDescriptor();
                cd.setImplementation( implementation );
            }
        }
        catch ( Throwable e )
        {
            throw new PlexusConfigurationException("Can not load implementation class " + implementation +
                " from realm " + realm, e);
        }

        cd.setRole( configuration.getChild( "role" ).getValue() );

        cd.setRoleHint( configuration.getChild( "role-hint" ).getValue() );

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

            ComponentRequirement cr;

            PlexusConfiguration[] hints = requirement.getChild( "role-hints" ).getChildren( "role-hint" );
            if ( hints != null && hints.length > 0 )
            {
                cr = new ComponentRequirementList();

                List<String> hintList = new LinkedList<String>();
                for ( PlexusConfiguration hint : hints )
                {
                    hintList.add( hint.getValue() );
                }

                ( (ComponentRequirementList) cr ).setRoleHints( hintList );
            }
            else
            {
                cr = new ComponentRequirement();

                cr.setRoleHint( requirement.getChild( "role-hint" ).getValue() );
            }

            cr.setRole( requirement.getChild( "role" ).getValue() );

            cr.setOptional( Boolean.parseBoolean( requirement.getChild( "optional" ).getValue() ) );

            cr.setFieldName( requirement.getChild( "field-name" ).getValue() );

            cd.addRequirement( cr );
        }

        return cd;
    }

    public static ComponentSetDescriptor buildComponentSet( PlexusConfiguration c )
        throws PlexusConfigurationException
    {
        return buildComponentSet( c, null );
    }

    public static ComponentSetDescriptor buildComponentSet( PlexusConfiguration c, ClassRealm realm )
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

            csd.addComponentDescriptor( buildComponentDescriptorImpl( component, realm ) );
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

    public static void writeConfiguration( PrintStream out, PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        writeConfiguration( out, configuration, "" );
    }

    private static void writeConfiguration( PrintStream out, PlexusConfiguration configuration, String indent )
        throws PlexusConfigurationException
    {
        out.print( indent + "<" + configuration.getName() );
        String[] atts = configuration.getAttributeNames();

        if ( atts.length > 0 )
        {
            for ( int i = 0; i < atts.length; i++ )
            {
                out.print( "\n" + indent + "  " + atts[i] + "='" + configuration.getAttribute( atts[i] ) + "'" );
            }
        }

        PlexusConfiguration[] pc = configuration.getChildren();

        if ( ( configuration.getValue() != null && configuration.getValue().trim().length() > 0 ) || pc.length > 0 )
        {
            out.print( ">" + ( configuration.getValue() == null ? "" : configuration.getValue().trim() ) );

            if ( pc.length > 0 )
            {
                out.println();
                for ( int i = 0; i < pc.length; i++ )
                {
                    writeConfiguration( out, pc[i], indent + "  " );
                }
                out.print( indent );
            }

            out.println( "</" + configuration.getName() + ">" );
        }
        else
        {
            out.println( "/>" );
        }
    }

}
