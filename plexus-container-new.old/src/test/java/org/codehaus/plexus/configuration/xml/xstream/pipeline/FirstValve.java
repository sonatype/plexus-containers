package org.codehaus.plexus.configuration.xml.xstream.pipeline;


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

    public FirstValve()
    {
    }

    public String getId()
    {
        return "first";
    }

    public String getName()
    {
        return name;
    }
}
