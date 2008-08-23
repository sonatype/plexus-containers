package org.codehaus.plexus.metadata.merge.support;

/**
 * All allowable tags in <code>components.xml</code> and their bindings to
 * {@link org.codehaus.plexus.metadata.merge.support.Mergeable} counterparts (if required).
 * <p/>
 * <em>This implementation may change.</em> <br>
 * TODO Might be an idea factor and set up the list of allowed tags here itself.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class PlexusXmlTag
    extends DescriptorTag
{
    public static final PlexusXmlTag COMPONENT_SET = new PlexusXmlTag( "component-set",
                                                                                                             false, ComponentSetElement.class );

    public static final PlexusXmlTag COMPONENTS = new PlexusXmlTag( "components",
                                                                                                          true, ComponentsElement.class );

    public static final PlexusXmlTag COMPONENT = new PlexusXmlTag( "component", true, ComponentElement.class );

    public static final PlexusXmlTag ROLE = new PlexusXmlTag( "role" );

    public static final PlexusXmlTag ROLE_HINT = new PlexusXmlTag( "role-hint" );

    public static final PlexusXmlTag FIELD_NAME = new PlexusXmlTag( "field-name" );

    public static final PlexusXmlTag IMPLEMENTATION = new PlexusXmlTag( "implementation" );

    public static final PlexusXmlTag LIFECYCLE_HANDLER = new PlexusXmlTag( "lifecycle-handler", false, null );

    public static final PlexusXmlTag REQUIREMENTS =
        new PlexusXmlTag( "requirements", true, RequirementsElement.class );

    public static final PlexusXmlTag CONFIGURATION =
        new PlexusXmlTag( "configuration", true, ConfigurationElement.class );

    public static final PlexusXmlTag REQUIREMENT =
        new PlexusXmlTag( "requirement", true, RequirementElement.class );

    /**
     * @param tagName
     * @param isMultipleAllowed
     * @param mergeableClass Class that wraps this tag (as JDom element) and provides for merging same tags.
     */
    private PlexusXmlTag( String tagName, boolean isMultipleAllowed, Class mergeableClass )
    {
        super( tagName, isMultipleAllowed, mergeableClass );
    }

    /**
     * By default we don't allow multiples of same tag names.
     *
     * @param tagName
     */
    private PlexusXmlTag( String tagName )
    {
        super( tagName, false, null );
    }
}
