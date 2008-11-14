package org.codehaus.plexus.metadata.merge;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import java.io.File;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.metadata.merge.support.AbstractMergeableElement;
import org.codehaus.plexus.metadata.merge.support.AbstractMergeableElementList;
import org.codehaus.plexus.metadata.merge.support.ComponentElement;
import org.codehaus.plexus.metadata.merge.support.ComponentsElement;
import org.codehaus.plexus.metadata.merge.support.RequirementsElement;
import org.codehaus.plexus.util.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Tests for {@link ComponentsXmlMerger}.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class ComponentsXmlMergerTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        ComponentsXmlMerger merger = (ComponentsXmlMerger) lookup( Merger.class, "componentsXml" );
        assertNotNull( merger );
    }

    public void testComponentsXmlFileMerge()
        throws Exception
    {
        File dominantXml = getTestFile( "src/test/resources/org/codehaus/plexus/metadata/merge/dominant.xml" );
        File recessiveXml = getTestFile( "src/test/resources/org/codehaus/plexus/metadata/merge/recessive.xml" );
        Document dDoc = new SAXBuilder().build( dominantXml );
        Document rDoc = new SAXBuilder().build( recessiveXml );
        // ComponentsXmlMerger merger = new ComponentsXmlMerger (dDoc);
        Merger merger = lookup( Merger.class, "componentsXml" );
        assertNotNull( merger );
        merger.merge( dDoc, rDoc );

        File merged_xml = getTestFile( "target/merged.xml" );
        if ( merged_xml.exists() )
        {
            FileUtils.forceDelete( merged_xml );
        }
        merger.writeMergedDocument( dDoc, merged_xml );
        assertTrue( merged_xml.exists() );
        // read merged xml and verify it was merged as expected 
        Document mDoc = new SAXBuilder().build( merged_xml );
        Element mRootElt = mDoc.getRootElement();
        assertTrue( mRootElt.getName().equals( "component-set" ) );
        assertEquals( 1, mRootElt.getChildren( "components" ).size() );
        List componentEltList = mRootElt.getChild( "components" ).getChildren( "component" );
        assertEquals( 2, componentEltList.size() );
        Element cElt = (Element) componentEltList.get( 0 );

        assertEquals( "org.codehaus.plexus.metadata.component.IComponent", cElt.getChildTextTrim( "role" ) );
        assertEquals( "org.codehaus.plexus.metadata.component.DominantComponent", cElt.getChildTextTrim( "implementation" ) );

        assertEquals( "Should only have 1 description element.", 1, cElt.getChildren( "description" ).size() );
        assertEquals( "Description for Dominant component", cElt.getChildTextTrim( "description" ) );

        assertEquals( "Should only have 1 configuration element.", 1, cElt.getChildren( "configuration" ).size() );
        // assert Merged configuration properties
        Element configurationElt = cElt.getChild( "configuration" );
        assertNotNull( configurationElt );
        assertEquals( 1, configurationElt.getChildren( "prop1" ).size() );
        assertEquals( "Dominant Property1 value", configurationElt.getChildTextTrim( "prop1" ) );
        assertEquals( 1, configurationElt.getChildren( "prop2" ).size() );
        assertEquals( 0, configurationElt.getChildren( "prop3" ).size() );

        // now for the second component
        cElt = (Element) componentEltList.get( 1 );
        assertEquals( "org.codehaus.plexus.metadata.component.INonConflictingComponent", cElt.getChildTextTrim( "role" ) );
        assertEquals( "org.codehaus.plexus.metadata.component.RecessiveComponent", cElt.getChildTextTrim( "implementation" ) );

        assertEquals( 1, mRootElt.getChildren( "lifecycle-handler-manager" ).size() );
        assertEquals( "org.codehaus.plexus.lifecycle.DefaultLifecycleHandlerManager", mRootElt
            .getChild( "lifecycle-handler-manager" ).getAttributeValue( "implementation" ) );
    }

    public void testInvalidMergeableElements()
        throws Exception
    {
        // dominant Component Element
        AbstractMergeableElement dCE = new ComponentElement( new Element( "component" ) );
        Element roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        dCE.addContent( roleElt );

        AbstractMergeableElementList reqElt = new RequirementsElement( new Element( "requirement" ) );
        // attempt and invalid merge
        try
        {
            dCE.merge( reqElt );
            fail( "Expected MergeException!" );
        }
        catch ( MergeException e )
        {
            // do nothing.
        }
    }

    /**
     * Tests if &lt;component&gt; elements from two sets are being merged properly.
     *
     * @throws Exception if there was an unexpected error.
     */
    public void testComponentsMerge()
        throws Exception
    {
        // dominant Components Element
        AbstractMergeableElement dParent = new ComponentsElement( new Element( "components" ) );
        Element dCE = new Element( "component" );
        dParent.addContent( dCE );
        Element roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        dCE.addContent( roleElt );
        Element roleHintElt = new Element( "role-hint" );
        roleHintElt.setText( "sample-role-hint" );
        dCE.addContent( roleHintElt );
        Element implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.DominantImplementation" );
        dCE.addContent( implElt );
        Element requirementsElt = new Element( "requirements" );
        Element reqElt = new Element( "requirement" );
        Element reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        dCE.addContent( requirementsElt );

        // recessive Component Element
        AbstractMergeableElement rParent = new ComponentsElement( new Element( "components" ) );
        Element rCE = new Element( "component" );
        rParent.addContent( rCE );
        roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        rCE.addContent( roleElt );
        roleHintElt = new Element( "role-hint" );
        roleHintElt.setText( "sample-role-hint" );
        rCE.addContent( roleHintElt );
        implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.RecessiveImplementation" );
        rCE.addContent( implElt );
        Element lifecycleHandlerElt = new Element( "lifecycle-handler" );
        rCE.addContent( lifecycleHandlerElt );
        lifecycleHandlerElt.setText( "plexus-configurable" );
        requirementsElt = new Element( "requirements" );
        reqElt = new Element( "requirement" );
        reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        Element reqRoleHintElt = new Element( "role-hint" );
        reqRoleHintElt.setText( "recessive-required-role-hint" );
        reqElt.addContent( reqRoleHintElt );
        rCE.addContent( requirementsElt );

        // attempt to merge
        dParent.merge( rParent );
        assertEquals( 1, dParent.getChildren( "component" ).size() );
        assertEquals( "org.codehaus.plexus.DominantImplementation", dParent.getChild( "component" )
            .getChildText( "implementation" ) );
        assertEquals( 1, dParent.getChild( "component" ).getChild( "requirements" ).getChildren( "requirement" ).size() );
    }

    /**
     * <em>This is deprecated as we dont' want to drill to merging
     * nested elements within a component.</em><p>
     * <em>Keeping this around for testing MergeStrategy implmentation.</em>
     *
     * @throws Exception
     */
    public void testDeepComponentsMerge()
        throws Exception
    {
        // FIXME: Review this after MergeStrategies are in place.
        if ( true )
        {
            return;
        }

        // dominant Component Element
        AbstractMergeableElement dCE = new ComponentElement( new Element( "component" ) );
        Element roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        dCE.addContent( roleElt );
        Element roleHintElt;
        // roleHintElt = new Element ("role-hint");
        // roleHintElt.setText ("sample-hint");
        // dCE.addContent (roleHintElt);
        Element implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.DominantImplementation" );
        dCE.addContent( implElt );
        Element requirementsElt = new Element( "requirements" );
        Element reqElt = new Element( "requirement" );
        Element reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        dCE.addContent( requirementsElt );

        // recessive Component Element
        AbstractMergeableElement rCE = new ComponentElement( new Element( "component" ) );
        roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        rCE.addContent( roleElt );
        roleHintElt = new Element( "role-hint" );
        roleHintElt.setText( "recessive-hint" );
        rCE.addContent( roleHintElt );
        implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.RecessiveImplementation" );
        rCE.addContent( implElt );
        Element lifecycleHandlerElt = new Element( "lifecycle-handler" );
        rCE.addContent( lifecycleHandlerElt );
        lifecycleHandlerElt.setText( "plexus-configurable" );
        requirementsElt = new Element( "requirements" );
        reqElt = new Element( "requirement" );
        reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        Element reqRoleHintElt = new Element( "role-hint" );
        reqRoleHintElt.setText( "recessive-required-role-hint" );
        reqElt.addContent( reqRoleHintElt );
        rCE.addContent( requirementsElt );

        // attempt to merge
        dCE.merge( rCE );

        // verify the merge
        assertTrue( null != dCE.getChild( "role" ) );
        assertEquals( "org.codehaus.plexus.ISampleRole", dCE.getChildText( "role" ) );
        assertTrue( null != dCE.getChild( "role-hint" ) );
        assertEquals( "recessive-hint", dCE.getChildText( "role-hint" ) );
        assertTrue( null != dCE.getChild( "lifecycle-handler" ) );
        assertEquals( "plexus-configurable", dCE.getChildText( "lifecycle-handler" ) );
        assertTrue( null != dCE.getChild( "requirements" ) );
        assertEquals( 1, dCE.getChild( "requirements" ).getChildren( "requirement" ).size() );
        assertEquals( "recessive-required-role-hint", ( (Element) dCE.getChild( "requirements" )
            .getChildren( "requirement" ).get( 0 ) ).getChildText( "role-hint" ) );
    }
}
