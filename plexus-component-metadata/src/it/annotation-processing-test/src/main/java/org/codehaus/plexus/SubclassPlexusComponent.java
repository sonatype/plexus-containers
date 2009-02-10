package org.codehaus.plexus;

import org.codehaus.plexus.component.annotations.Component;

@Component(role = Component.class)
public class SubclassPlexusComponent
    extends AbstractPlexusComponent
{
    public String execute()
    {
        return executor.execute();
    }
}
