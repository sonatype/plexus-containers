package org.codehaus.plexus.component.composition;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentComposer
{
    /** Role for this component. */
    static String ROLE = ComponentComposer.class.getName();

    void compose( Object component )
        throws CompositionException;
}
