/*
 * The MIT License
 *
 * Copyright (c) 2007, The Codehaus
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

package org.codehaus.plexus.metadata;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * Serializes a {@link ComponentSetDescriptor}.
 *
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class DefaultComponentDescriptorWriter
    implements ComponentDescriptorWriter
{
    private static final String LS = System.getProperty( "line.separator" );

    public void writeDescriptorSet( Writer writer, ComponentSetDescriptor componentSetDescriptor, boolean containerDescriptor )
        throws ComponentDescriptorWriteException, IOException
    {
        try
        {
            XMLWriter w = new PrettyPrintXMLWriter( writer );

            w.startElement( containerDescriptor ? "plexus" : "component-set" );

            writeComponents( w, componentSetDescriptor.getComponents() );

            writeDependencies( w, componentSetDescriptor.getDependencies() );

            w.endElement();

            writer.write( LS );

            // Flush, but don't close the writer... we are not its owner
            writer.flush();
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ComponentDescriptorWriteException( "Internal error while writing out the configuration", e );
        }

    }

    private void writeComponents( XMLWriter w, List componentDescriptors )
        throws ComponentDescriptorWriteException, PlexusConfigurationException
    {
        if ( componentDescriptors == null )
        {
            return;
        }

        w.startElement( "components" );

        for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
        {
            w.startElement( "component" );

            ComponentDescriptor cd = (ComponentDescriptor) i.next();

            element( w, "role", cd.getRole() );

            element( w, "role-hint", cd.getRoleHint() );

            element( w, "implementation", cd.getImplementation() );

            element( w, "version", cd.getVersion() );

            element( w, "component-type", cd.getComponentType() );

            element( w, "instantiation-strategy", cd.getInstantiationStrategy() );

            element( w, "lifecycle-handler", cd.getLifecycleHandler() );

            element( w, "component-profile", cd.getComponentProfile() );

            element( w, "component-composer", cd.getComponentComposer() );

            element( w, "component-configurator", cd.getComponentConfigurator() );

            element( w, "component-factory", cd.getComponentFactory() );

            element( w, "description", cd.getDescription() );

            element( w, "alias", cd.getAlias() );

            element( w, "isolated-realm", Boolean.toString(cd.isIsolatedRealm()) );

            writeRequirements( w, cd.getRequirements() );

            writeConfiguration( w, cd.getConfiguration() );

            w.endElement();
        }

        w.endElement();
    }

    public void writeDependencies( XMLWriter w, List deps )
    {
        if ( deps == null || deps.size() == 0 )
        {
            return;
        }

        w.startElement( "dependencies" );

        for ( int i = 0; i < deps.size(); i++ )
        {
            writeDependencyElement( (ComponentDependency) deps.get( i ), w );
        }

        w.endElement();
    }

    private void writeDependencyElement( ComponentDependency dependency, XMLWriter w )
    {
        w.startElement( "dependency" );

        String groupId = dependency.getGroupId();

        element( w, "groupId", groupId );

        String artifactId = dependency.getArtifactId();

        element( w, "artifactId", artifactId );

        String type = dependency.getType();

        if ( type != null )
        {
            element( w, "type", type );
        }

        String version = dependency.getVersion();

        element( w, "version", version );

        w.endElement();
    }

    private void writeRequirements( XMLWriter w, List requirements )
    {
        if ( requirements == null || requirements.size() == 0 )
        {
            return;
        }

        w.startElement( "requirements" );

        for ( Iterator j = requirements.iterator(); j.hasNext(); )
        {
            ComponentRequirement cr = (ComponentRequirement) j.next();

            w.startElement( "requirement" );

            element( w, "role", cr.getRole() );

            if ( cr instanceof ComponentRequirementList )
            {
                List hints = ( (ComponentRequirementList) cr ).getRoleHints();

                if ( hints != null )
                {
                    w.startElement( "role-hints" );

                    for ( Iterator k = hints.iterator(); k.hasNext(); )
                    {
                        String roleHint = (String) k.next();

                        w.startElement( "role-hint" );

                        w.writeText( roleHint );

                        w.endElement();
                    }

                    w.endElement();
                }
            }
            else
            {
                element( w, "role-hint", cr.getRoleHint() );
            }

            element( w, "field-name", cr.getFieldName() );

            w.endElement();
        }

        w.endElement();
    }

    private void writeConfiguration( XMLWriter w, PlexusConfiguration configuration )
        throws ComponentDescriptorWriteException, PlexusConfigurationException
    {
        if ( configuration == null || configuration.getChildCount() == 0 )
        {
            return;
        }

        if ( !configuration.getName().equals( "configuration" ) )
        {
            throw new ComponentDescriptorWriteException( "The root node of the configuration must be 'configuration'.");
        }

        writePlexusConfiguration( w, configuration );
    }

    private void writePlexusConfiguration( XMLWriter xmlWriter, PlexusConfiguration c )
        throws PlexusConfigurationException
    {
        if ( c.getAttributeNames().length == 0 && c.getChildCount() == 0 && c.getValue() == null )
        {
            return;
        }

        xmlWriter.startElement( c.getName() );

        // ----------------------------------------------------------------------
        // Write the attributes
        // ----------------------------------------------------------------------

        String[] attributeNames = c.getAttributeNames();

        for ( int i = 0; i < attributeNames.length; i++ )
        {
            String attributeName = attributeNames[i];

            xmlWriter.addAttribute( attributeName, c.getAttribute( attributeName ) );
        }

        // ----------------------------------------------------------------------
        // Write the children
        // ----------------------------------------------------------------------

        PlexusConfiguration[] children = c.getChildren();

        if ( children.length > 0 )
        {
            for ( int i = 0; i < children.length; i++ )
            {
                writePlexusConfiguration( xmlWriter, children[i] );
            }
        }
        else
        {
            String value = c.getValue();

            if ( value != null )
            {
                xmlWriter.writeText( value );
            }
        }

        xmlWriter.endElement();
    }

    private void element( XMLWriter w, String name, String value )
    {
        if ( value == null )
        {
            return;
        }

        w.startElement( name );

        w.writeText( value );

        w.endElement();
    }

}
