package org.codehaus.plexus.component.composition;

/**
 * This composer does nothing and relies on a phase in the lifecycle
 * that exposes the component repository where the programmer can
 * manually lookup required components.
 *
 * This is considered bad but is supported because the Avalon model
 * of assembly promotes this usage pattern.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ManualComponentComposer
    extends AbstractComponentComposer
{
    public void compose( Object component )
        throws CompositionException
    {
    }
}
