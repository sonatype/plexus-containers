package org.codehaus.plexus;

import org.codehaus.plexus.component.annotations.Component;

@Component(role=Executor.class,hint="slow")
public class SlowExecutor
    implements Executor
{
    public String execute()
    {        
        return "slow";
    }
}
