package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2004-2005, Codehaus.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * PlexusCheckRoleHintsMojo
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @goal check-role-hints
 * 
 * @phase test
 */
public class PlexusCheckRoleHintsMojo
    extends AbstractMojo
{
    /**
     * The META-INF/plexus/ directory that contains the post-processed plexus data files. [
     * components.xml, application.xml, and plexus.xml ]
     * 
     * @parameter default-value="${project.build.outputDirectory}/META-INF/plexus"
     * @required
     */
    private File plexusDirectory;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File componentsXml = new File( plexusDirectory, "components.xml" );
        if ( componentsXml.exists() )
        {
            checkPlexusRoleHints( componentsXml );
        }

        File applicationXml = new File( plexusDirectory, "application.xml" );
        if ( applicationXml.exists() )
        {
            checkPlexusRoleHints( applicationXml );
        }

        File plexusXml = new File( plexusDirectory, "plexus.xml" );
        if ( plexusXml.exists() )
        {
            checkPlexusRoleHints( plexusXml );
        }
    }

    /**
     * Load document via jdom, perform some basic checks.
     * 
     * @param componentsXml
     * @throws MojoFailureException
     */
    private void checkPlexusRoleHints( File componentsXml )
        throws MojoExecutionException, MojoFailureException
    {
        int violationCount = 0;
        SAXBuilder builder = new SAXBuilder();

        try
        {
            Document doc = builder.build( componentsXml );

            Element root = doc.getRootElement();

            if ( !root.getName().equals( "component-set" ) )
            {
                getLog().warn( "Not a plexus components.xml - doesn't start with <component-set>" );
                return;
            }

            List componentsList = root.getChildren( "components" );
            for ( Iterator itcomponents = componentsList.iterator(); itcomponents.hasNext(); )
            {
                Element components = (Element) itcomponents.next();
                violationCount += countComponentRoleHintViolations( components );
            }

            if ( violationCount > 0 )
            {
                throw new MojoFailureException( componentsXml.getAbsolutePath() + " has " + violationCount + " role-hint violation(s)." );
            }
        }
        catch ( JDOMException e )
        {
            throw new MojoExecutionException( "Unable to load " + componentsXml.getAbsolutePath() + ", it is not valid.", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to load " + componentsXml.getAbsolutePath() + ": " + e.getMessage(), e );
        }
    }

    private int countComponentRoleHintViolations( Element components )
    {
        int violationCount = 0;

        List componentList = components.getChildren( "component" );
        for ( Iterator itcomponent = componentList.iterator(); itcomponent.hasNext(); )
        {
            Element component = (Element) itcomponent.next();
            String componentRole = component.getChildText( "role" );

            // Test component definition.
            Element componentRoleHint = component.getChild( "role-hint" );
            if ( componentRoleHint == null )
            {
                violationCount++;
                getLog().error( "Missing <role-hint> on component definition for <role> " + componentRole );
            }

            // Test requirements.
            List requirementsList = component.getChildren( "requirements" );
            for ( Iterator itrequirements = requirementsList.iterator(); itrequirements.hasNext(); )
            {
                Element requirements = (Element) itrequirements.next();
                violationCount += countRequirementRoleHintViolations( requirements );
            }
        }

        return violationCount;
    }

    private int countRequirementRoleHintViolations( Element requirements )
    {
        int violationCount = 0;

        List requirementList = requirements.getChildren( "requirement" );
        for ( Iterator itrequirement = requirementList.iterator(); itrequirement.hasNext(); )
        {
            Element requirement = (Element) itrequirement.next();
            String requirementRole = requirement.getChildText( "role" );

            // Test requirement definition.
            Element requirementRoleHint = requirement.getChild( "role-hint" );
            if ( requirementRoleHint == null )
            {
                violationCount++;
                getLog().error( "Missing <role-hint> on <requirement> definition for <role> " + requirementRole );
            }
        }

        return violationCount;
    }
}
