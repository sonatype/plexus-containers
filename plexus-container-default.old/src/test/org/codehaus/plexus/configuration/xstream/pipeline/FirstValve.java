package org.codehaus.plexus.configuration.xstream.pipeline;


/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class FirstValve
    implements Valve
{
    private String name;

    public String getId()
    {
        return "first";
    }

    public String getName()
    {
        return name;
    }
}
