package org.codehaus.plexus.component.connector;

public class DefaultProvider
    implements Provider
{
    boolean run;

    boolean execute;

    String name;

    public void run()
    {
        run = true;
    }

    public void execute()
    {
        execute = true;
    }

    public void setName( String name )
    {
        name = "jason";
    }
}

