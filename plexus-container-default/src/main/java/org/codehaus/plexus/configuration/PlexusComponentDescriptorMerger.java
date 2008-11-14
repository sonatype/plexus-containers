package org.codehaus.plexus.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author <a href="mailto:kenney@apache.org">Kenney Westerhof</a>
 *
 */
public class PlexusComponentDescriptorMerger
{
    /**
     * Merges override and target, where target is updated with override.
     */
    public static void merge( ComponentDescriptor<?> override, ComponentDescriptor<?> target )
    {
        if ( override.getImplementation() != null )
        {
            target.setImplementation( override.getImplementation() );
        }

        mergeRequirements( override, target );

        mergeConfiguration( override, target );

        // TODO: other getters/setters.
    }

    private static void mergeConfiguration( ComponentDescriptor<?> override, ComponentDescriptor<?> target )
    {
        // try to parse the override dom. If this fails, do not update anything and keep
        // the original target configuration.
        Xpp3Dom overrideDom;

        try
        {

            overrideDom = Xpp3DomBuilder.build( new StringReader( override.getConfiguration().toString() ) );
        }
        catch ( XmlPullParserException e1 )
        {
            return;
        }
        catch ( IOException e1 )
        {
            return;
        }

        // try to parse the target dom. if this fails, replace it with the override configuration,
        // dom, otherwise merge it.

        Xpp3Dom targetDom = null;

        if ( target.getConfiguration() != null )
        {
            try
            {
                targetDom = Xpp3DomBuilder.build( new StringReader( target.getConfiguration().toString() ) );
            }
            catch ( XmlPullParserException e1 )
            {
            }
            catch ( IOException e1 )
            {
            }
        }

        if ( targetDom != null )
        {
            Xpp3Dom.mergeXpp3Dom( overrideDom, targetDom );
        }
        else
        {
            targetDom = overrideDom;
        }

        target.setConfiguration( new XmlPlexusConfiguration( targetDom ) );
    }

    private static void mergeRequirements( ComponentDescriptor<?> override, ComponentDescriptor<?> target )
    {
        List<ComponentRequirement> toAdd = new ArrayList<ComponentRequirement>();
        List<ComponentRequirement> toRemove = new ArrayList<ComponentRequirement>();

        for ( ComponentRequirement sourceReq : override.getRequirements() )
        {
            for ( ComponentRequirement targetReq : target.getRequirements() )
            {
                // if a fieldName is specified, only override target requirements
                // that also have a fieldname.
                if ( sourceReq.getFieldName() != null )
                {
                    if ( sourceReq.getFieldName().equals( targetReq.getFieldName() ) )
                    {
                        toRemove.add( targetReq );
                        toAdd.add( sourceReq );
                        break;
                    }
                }
                else if ( targetReq.getFieldName() == null )
                {
                    // no fieldnames specified - just check for the role; hints may be
                    // overriden too.

                    if ( sourceReq.getRole().equals( targetReq.getRole() ) )
                    {
                        toRemove.add( targetReq );
                        toAdd.add( sourceReq );
                    }
                }
            }
        }

        target.removeRequirements( toRemove );
        target.addRequirements( toAdd );
    }

}
