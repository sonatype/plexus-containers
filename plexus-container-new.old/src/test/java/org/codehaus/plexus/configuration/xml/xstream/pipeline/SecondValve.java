package org.codehaus.plexus.configuration.xml.xstream.pipeline;


/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class SecondValve
    implements Valve
{
    private String name;

    public SecondValve()
    {
    }

    public String getId()
    {
        return "second";
    }

    public String getName()
    {
        return name;
    }
}
