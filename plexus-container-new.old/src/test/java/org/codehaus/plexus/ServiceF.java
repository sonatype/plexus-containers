package org.codehaus.plexus;

public interface ServiceF
{
    static String ROLE = ServiceF.class.getName();

    String getPlexusHome();
}
