package org.codehaus.plexus;

import org.codehaus.plexus.component.annotations.Component;

@Component(role = Executor.class, hint = "fast")
public class FastExecutor
    implements Executor
{
    public String execute()
    {
        return "fast";
    }
}
