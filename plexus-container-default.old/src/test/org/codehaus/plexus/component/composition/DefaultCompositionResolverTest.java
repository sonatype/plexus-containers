package org.codehaus.plexus.component.composition;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultCompositionResolverTest
    extends AbstractCompositionResolverTest
{
    protected CompositionResolver getCompositionResolver()
    {
        return new DefaultCompositionResolver();
    }
}
